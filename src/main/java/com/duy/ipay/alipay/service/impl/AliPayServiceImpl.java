package com.duy.ipay.alipay.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.duy.ipay.alipay.model.dto.PCPayDTO;
import com.duy.ipay.alipay.service.AliPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AliPayServiceImpl implements AliPayService {
    private Logger log = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Override
    public String pc(PCPayDTO payDTO) {
        try {
            //1.发起api调用
            AlipayTradePagePayResponse response = Factory.Payment.Page().pay(payDTO.getSubject(),payDTO.getOrderNo(),payDTO.getTotal(),payDTO.getReturnUrl());
            //2.处理响应或异常
            if(ResponseChecker.success(response)){
                log.info("【支付宝电脑网站支付】请求调用成功");
            } else {
                log.error("【支付宝电脑网站支付】请求调用失败，结果：",response.getBody());
            }
            return response.body;
        } catch (Exception e) {
            log.error("【支付宝电脑网站支付】请求调用发生异常：{}",e.getMessage());
        }
        return null;
    }
}
