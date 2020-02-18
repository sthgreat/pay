package com.dzkjdx.pay.service;

import com.dzkjdx.pay.pojo.PayInfo;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

public interface IPayService {
    /**
     * 发起支付
     */
    PayResponse creat(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String asynNotify(String notifyData);

    /**
     * 通过订单号查询支付记录
     * @param orderId
     * @return
     */
    PayInfo queryByOrderId(String orderId);
}
