package com.duy.ipay.alipay.service;

import com.duy.ipay.alipay.model.dto.PCPayDTO;

public interface AliPayService {
    /**
     * 支付宝网页支付
     * @param payDTO
     * @return
     */
    String pc(PCPayDTO payDTO);
}
