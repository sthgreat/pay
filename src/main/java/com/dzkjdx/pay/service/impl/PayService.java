package com.dzkjdx.pay.service.impl;

import com.dzkjdx.pay.dao.PayInfoMapper;
import com.dzkjdx.pay.enums.payPlatformEnum;
import com.dzkjdx.pay.pojo.PayInfo;
import com.dzkjdx.pay.service.IPayService;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayService implements IPayService {

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public PayResponse creat(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {
        //缺少步骤：写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                payPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("8498166-支付初体验6");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(request);

        log.info("response={}",response);
        return response;
    }

    @Override
    public String asynNotify(String notifyData) {
        //1.签名校验，确认是否为微信官方发送的消息
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}",payResponse);

        //2.支付金额校验（从数据库查找订单）

        //3.修改订单支付状态

        //4.告诉微信成功接到通知，不需再次通知
        if(payResponse.getPayPlatformEnum()==BestPayPlatformEnum.WX){
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if(payResponse.getPayPlatformEnum()==BestPayPlatformEnum.ALIPAY){
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");
    }
}
