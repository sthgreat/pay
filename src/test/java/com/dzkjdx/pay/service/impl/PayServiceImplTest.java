package com.dzkjdx.pay.service.impl;

import com.dzkjdx.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

public class PayServiceImplTest extends PayApplicationTests {

    @Autowired
    PayServiceImpl payServiceImpl;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void creat() {
        payServiceImpl.creat("10005", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
    }

    @Test
    public void senMQMsg(){
        amqpTemplate.convertAndSend("payNotify","hello");
    }
}