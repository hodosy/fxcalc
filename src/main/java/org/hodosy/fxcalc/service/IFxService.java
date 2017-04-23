package org.hodosy.fxcalc.service;

import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.service.pojo.CalculateFxInput;

public interface IFxService {

    CalculateExchangeRateResponse calculateFx(CalculateFxInput imput);
}
