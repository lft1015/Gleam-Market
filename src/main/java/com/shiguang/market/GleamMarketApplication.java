package com.shiguang.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 商品交易市场应用主类
 *
 * @author gugu
 */
@SpringBootApplication
@EnableScheduling
public class GleamMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(GleamMarketApplication.class, args);
    }

}