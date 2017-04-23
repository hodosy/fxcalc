package org.hodosy.fxcalc.service;

import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.global.MessageException;
import org.hodosy.fxcalc.service.pojo.CalculateFxInput;
import org.springframework.stereotype.Service;

@Service
public class FxService implements IFxService{

    @Override
    public CalculateExchangeRateResponse calculateFx(CalculateFxInput input) throws MessageException {
        System.out.println(input);
        return null;
    }
}
