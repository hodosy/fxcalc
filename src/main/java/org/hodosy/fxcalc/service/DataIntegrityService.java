package org.hodosy.fxcalc.service;

import org.apache.log4j.Logger;
import org.hodosy.fxcalc.service.pojo.DailyCurrencyRateHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class DataIntegrityService {

    private static final Logger logger = Logger.getLogger(DataIntegrityService.class);

    private final QName timeAttribute = new QName("time");
    private final QName currencyAttribute = new QName("currency");
    private final QName rateAttribute = new QName("rate");
    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private final String eurofxrefDailyUrl;
    private final String eurofxref90daysUrl;

    private final FxService fxService;
    private final TaskExecutor taskExecutor;
    private final TaskScheduler taskScheduler;
    private final CronTrigger dailyRateUpdateCronTrigger;
    private final CronTrigger evictionCronTrigger;

    public DataIntegrityService(FxService fxService,
                                TaskExecutor taskExecutor,
                                TaskScheduler taskScheduler,
                                @Value("${eurofxref.daily.url}") String eurofxrefDailyUrl,
                                @Value("${eurofxref.90day.url}") String eurofxref90daysUrl,
                                @Value("${cron.daily.rate.update}") String dailyRateUpdateSchedule,
                                @Value("${cron.evict.old.entries}") String evictionSchedule
                                ) {
        this.fxService = Objects.requireNonNull(fxService, "Required FxService has not initialised");
        this.taskExecutor = Objects.requireNonNull(taskExecutor, "Required task executor service has not initialised");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "Required task scheduler service has not initialised");
        this.eurofxrefDailyUrl = eurofxrefDailyUrl;
        this.eurofxref90daysUrl = eurofxref90daysUrl;
        this.dailyRateUpdateCronTrigger = new CronTrigger(dailyRateUpdateSchedule);
        this.evictionCronTrigger = new CronTrigger(evictionSchedule);
    }

    @PostConstruct
    public void afterInit() {
        taskExecutor.execute(this::updateAllRate);
        taskScheduler.schedule(this::updateTheDailyRate, this.dailyRateUpdateCronTrigger);
        taskScheduler.schedule(this::evictOldEntries, this.evictionCronTrigger);
    }

    private void updateAllRate() {
        logger.info("Executing global update");
        taskExecutor.execute(() -> parseFile(this.eurofxref90daysUrl));
    }

    private void updateTheDailyRate() {
        logger.info("Executing daily rate update");
        taskExecutor.execute(() -> parseFile(this.eurofxrefDailyUrl));
    }

    private void evictOldEntries() {
        logger.info("Executing eviction");
        taskExecutor.execute(fxService::evictOldEntries);
    }

    private void parseFile(String xmlAccessPath) {
        XMLEventReader eventReader;
        try {
            eventReader = factory.createXMLEventReader(
                    new URL(xmlAccessPath).openStream());
        } catch (IOException e) {
            logger.error("Error on retrieving the xml. Will try again.", e);
            taskExecutor.execute(() -> parseFile(xmlAccessPath));
            return;
        } catch (Exception e) {
            logger.error("Unknown error.",e);
            return;
        }

        StartElement startElement;
        LocalDate entryDate = null;
        Map<String, BigDecimal> currencyPairs = new HashMap<>();
        try {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement() &&
                        (startElement = event.asStartElement()).getName().getLocalPart().equals("Cube")) {

                    LocalDate localTimeEntry = Optional.ofNullable(startElement.getAttributeByName(timeAttribute))
                            .map(Attribute::getValue)
                            .map(LocalDate::parse)
                            .orElse(null);

                    if (localTimeEntry != null && entryDate == null) {
                        entryDate = localTimeEntry;
                        currencyPairs = new HashMap<>();
                    } else if (localTimeEntry == null) {
                        String currency = Optional.ofNullable(startElement.getAttributeByName(currencyAttribute))
                                .map(Attribute::getValue)
                                .orElse(null);
                        BigDecimal rate = Optional.ofNullable(startElement.getAttributeByName(rateAttribute))
                                .map(Attribute::getValue)
                                .map(BigDecimal::new)
                                .orElse(null);
                        if (currency != null && rate != null) {
                            currencyPairs.put(currency, rate);
                        }
                    } else if (localTimeEntry != null && entryDate != null) {
                        fxService.addCurrencyMapping(entryDate, new DailyCurrencyRateHolder(currencyPairs));
                        entryDate = localTimeEntry;
                        currencyPairs = new HashMap<>();
                    }
                }
            }
            if (entryDate != null) {
                fxService.addCurrencyMapping(entryDate, new DailyCurrencyRateHolder(currencyPairs));
            }
        } catch (Exception e) {
            logger.error("Error on parsing the XML. Will be tried again", e);
            taskExecutor.execute(() -> parseFile(xmlAccessPath));
        }
    }

}
