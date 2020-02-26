package com.dzkjdx.pay.controller;

import com.dzkjdx.pay.pojo.PayInfo;
import com.dzkjdx.pay.service.impl.PayServiceImpl;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private PayServiceImpl payServiceImpl;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum){
        PayResponse response = payServiceImpl.creat(orderId, amount, bestPayTypeEnum);
        Map<String,String> map = new HashMap<>();
        //支付方式不同，渲染页面不同，微信NATIVE使用codeurl，支付宝PC使用body
        if(bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE){
            map.put("codeUrl", response.getCodeUrl());
            map.put("orderId",orderId);
            map.put("returnUrl",wxPayConfig.getReturnUrl());
            return new ModelAndView("createForWxNative", map);
        }else if(bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC){
            map.put("body", response.getBody());
            return new ModelAndView("createForAlipayPC", map);
        }
        throw new RuntimeException("暂不支持的支付方式");
    }

    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData){
        return payServiceImpl.asynNotify(notifyData);
    }

    @GetMapping("/queryByOrderId")//前端发起的异步请求，从服务端获取订单信息
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam("orderId") String orderId){
        return payServiceImpl.queryByOrderId(orderId);
    }

}
