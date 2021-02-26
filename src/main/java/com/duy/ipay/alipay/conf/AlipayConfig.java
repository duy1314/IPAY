package com.duy.ipay.alipay.conf;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class AlipayConfig implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(AlipayConfig.class);

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.gatewayHost}")
    private String gatewayHost;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Factory.setOptions(getOptions());
    }
    private Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = gatewayHost;
        config.signType = "RSA2";

        config.appId = appId;

        // 应用私钥
        Resource resource = new ClassPathResource("static/alipayAppPrivateKey.txt");
        StringBuffer merchantPrivateKey = new StringBuffer();
        InputStream is = null;
        try {
            is = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine()) != null) {
                merchantPrivateKey.append(s);
            }

            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            log.error("读取支付宝应用公钥失败：{}",e.getMessage());
        }
        config.merchantPrivateKey = merchantPrivateKey.toString();

        // 支付宝公钥
        resource = new ClassPathResource("static/alipayPublicKey.txt");
        StringBuffer alipayPublicKey = new StringBuffer();
        try {
            is = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine()) != null) {
                alipayPublicKey.append(s);
            }

            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            log.error("读取支付宝公钥失败：{}",e.getMessage());
        }
        config.alipayPublicKey = alipayPublicKey.toString();

        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = notifyUrl;

        return config;
    }
}
