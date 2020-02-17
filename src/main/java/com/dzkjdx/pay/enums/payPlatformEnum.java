package com.dzkjdx.pay.enums;

import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;

public enum payPlatformEnum {
    //1.支付包，2.微信
    ALIPAY(1),

    WX(2),

    ;

    Integer code;

    payPlatformEnum(Integer code){
        this.code = code;
    }

    public Integer getCode(){
        return this.code;
    }

    public static payPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
        for (payPlatformEnum value : payPlatformEnum.values()) {
            if(bestPayTypeEnum.getPlatform().name().equals(value.name())){
                return value;
            }
        }
        throw new RuntimeException("错误的支付平台" + bestPayTypeEnum.name());
    }
}
