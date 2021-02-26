package com.duy.ipay.alipay.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 电脑网站支付请求实体
 */
@Data
public class PCPayDTO {

    /**
     * 商品标题/交易标题/订单标题/订单关键字等。
     * 注意：不可使用特殊字符，如 /，=，& 等。
     */
    @NotBlank
    private String subject;

    /**
     * 商户订单号，由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
     */
    @NotBlank
    private String orderNo;

    /**
     * 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
     */
    @NotBlank
    private String total;

    /**
     * 支付完成回调页面
     */
    @NotBlank
    private String returnUrl;
}
