package com.shiguang.market.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 自动生成在线API文档
 *
 * @author gugu
 */

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("拾光集市接口文档")
                        .version("V1.0.0")
                        .description("二手交易与失物招领平台"));
    }
}
