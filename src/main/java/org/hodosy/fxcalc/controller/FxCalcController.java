package org.hodosy.fxcalc.controller;

import org.apache.log4j.Logger;
import org.hodosy.fxcalc.controller.request.CalculateExchangeRateRequest;
import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.controller.response.CustomResponseEntity;
import org.hodosy.fxcalc.controller.response.Message;
import org.hodosy.fxcalc.global.ErrorCodes;
import org.hodosy.fxcalc.global.MessageException;
import org.hodosy.fxcalc.global.SideEnum;
import org.hodosy.fxcalc.service.IFxService;
import org.hodosy.fxcalc.service.pojo.CalculateFxInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

@RestController
public class FxCalcController {

    private static final Logger logger = Logger.getLogger(FxCalcController.class);

    private final IFxService fxService;

    @Autowired
    public FxCalcController(IFxService fxService) {
        this.fxService = Objects.requireNonNull(fxService, "Required FxService is missing");
    }

    @PostMapping(value = "/calculateExchangeRate_1_0.json")
    public CustomResponseEntity<CalculateExchangeRateResponse> calculateExchangeRate(
            @RequestBody @Valid CalculateExchangeRateRequest request, BindingResult bindingResult) throws BindException {

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return Optional.of(request)
                .map(R -> new CalculateFxInput.CalculateFxInputBuilder()
                        .currency(R.getCurrency())
                        .side(SideEnum.valueOf(R.getSide()))
                        .amount(new BigDecimal(R.getAmount()))
                        .dateTime(Optional.ofNullable(R.getDateTime()).map(ZonedDateTime::parse).orElse(ZonedDateTime.now()))
                        .build()
                ).map(fxService::calculateFx)
                .map(CustomResponseEntity::new)
                .orElseThrow(() -> new IllegalArgumentException("Unknown reason occured"));
    }

    @ExceptionHandler(BindException.class)
    public CustomResponseEntity conflict(BindException errors) {
        CustomResponseEntity cre = new CustomResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        errors.getGlobalErrors()
                .stream()
                .map(FE -> new Message.MessageBuilder(ErrorCodes.INPUT_ERROR).text(FE.getDefaultMessage()).build())
                .peek(logger::error)
                .forEach(cre::appendMessage);
        errors.getFieldErrors()
                .stream()
                .map(FE -> new Message.MessageBuilder(ErrorCodes.INPUT_ERROR).text(FE.getDefaultMessage()).field(FE.getField()).build())
                .peek(logger::error)
                .forEach(cre::appendMessage);
        return cre;
    }

    @ExceptionHandler(MessageException.class)
    public CustomResponseEntity conflict(MessageException errors) {
        CustomResponseEntity cre = new CustomResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        errors.forEach(cre::appendMessage);
        return cre;
    }

    @ExceptionHandler(Exception.class)
    public CustomResponseEntity conflict(Exception e) {
        CustomResponseEntity cre = new CustomResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        cre.appendMessage(new Message.MessageBuilder(ErrorCodes.UNKNOWN_ERROR).build());
        logger.fatal("Unexpected problem", e);
        return cre;
    }
}
