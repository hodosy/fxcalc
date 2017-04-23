package org.hodosy.fxcalc.controller;

import org.hodosy.fxcalc.controller.request.CalculateExchangeRateRequest;
import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.controller.response.CustomResponseEntity;
import org.hodosy.fxcalc.controller.response.Message;
import org.hodosy.fxcalc.global.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class FxCalcController {

    @PostMapping(value = "/calculateExchangeRate_1_0.json")
    public CustomResponseEntity<CalculateExchangeRateResponse> calculateExchangeRate(
            @RequestBody @Valid CalculateExchangeRateRequest request, BindingResult bindingResult) throws BindException {

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        CalculateExchangeRateResponse response = new CalculateExchangeRateResponse();
        response.setBuyCurrency("HUF");
        response.setSellCurrency("EUR");
        return new CustomResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(BindException.class)
    public CustomResponseEntity conflict(BindException errors) {
        CustomResponseEntity cre = new CustomResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        errors.getGlobalErrors()
                .stream()
                .map(FE -> new Message.MessageBuilder().code(ErrorCodes.INPUT_ERROR).text(FE.getDefaultMessage()).build())
                .forEach(cre::appendMessage);
        errors.getFieldErrors()
                .stream()
                .map(FE -> new Message.MessageBuilder().code(ErrorCodes.INPUT_ERROR).text(FE.getDefaultMessage()).field(FE.getField()).build())
                .forEach(cre::appendMessage);
        return cre;
    }
}
