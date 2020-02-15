package com.dzkjdx.pay.service.impl;

import com.dzkjdx.pay.service.IPayService;

import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;

import java.math.BigDecimal;

public class PayService implements IPayService {

    @Override
    public void creat(String orderId, BigDecimal amount) {
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();

    }
}
