package com.codetoday.orderservice.config;

import com.codetoday.orderservice.external.errordecoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
@Bean
    ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
