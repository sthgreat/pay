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
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                payPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        //订单写入数据库
        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("8498166-支付初体验");
        request.setOrderId(orderId);//商家系统的订单Id
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(request);
        log.info("response={}", response);
        return response;
    }

    @Override
    public String asynNotify(String notifyData) {
        //1.签名校验，确认是否为微信官方发送的消息
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);

        //2.支付金额校验（从数据库查找订单）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null) {
            throw new RuntimeException("通过orderno查询结果为null");
        }
        //如果订单支付状态不是已经支付
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            //判断金额
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0){
                //告警
                throw new RuntimeException("异步通知中金额与数据库不一致,orderNo="+payResponse.getOrderId());
            }
            //3.修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());//交易流水号
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO 发送MQ消息，pay系统发送，mall系统接受


        //4.告诉微信成功接到通知，不需再次通知
        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");
    }

    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
