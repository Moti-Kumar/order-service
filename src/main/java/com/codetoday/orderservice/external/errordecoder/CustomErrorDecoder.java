package com.codetoday.orderservice.external.errordecoder;

import com.codetoday.orderservice.exception.CustomOrderException;
import com.codetoday.orderservice.external.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("URL::{}",response.request().url());
        log.info("HEADER::{}",response.request().headers());
        try {
            ErrorResponse errorResponse=objectMapper.readValue(
                    response.body().asInputStream(),ErrorResponse.class);

            throw  new CustomOrderException(errorResponse.getErrorMessage(),
                    errorResponse.getErrorCode(),response.status());
        } catch (IOException e) {
            throw new CustomOrderException("INTERNAL Server Error","INTERNAL SERVER ERROR",500);

        }

    }
}
