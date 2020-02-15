package com.dzkjdx.pay.service;

import java.math.BigDecimal;

public interface IPayService {
    /**
     * 发起支付
     */
    void creat(String orderId, BigDecimal amount);
}
