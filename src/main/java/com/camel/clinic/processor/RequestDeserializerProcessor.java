package com.camel.clinic.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("requestDeserializerProcessor")
public class RequestDeserializerProcessor implements Processor {
    private final ObjectMapper objectMapper;

    public RequestDeserializerProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        Class<?> targetType = exchange.getIn().getHeader("X-DTO-Class", Class.class);
        Boolean isList = exchange.getIn().getHeader("X-DTO-List", Boolean.class);

        if (targetType != null && body != null) {
            log.debug("Deserializing request exchangeId={} routeId={} targetType={} isList={} bodyLength={}",
                    exchange.getExchangeId(),
                    exchange.getFromRouteId(),
                    targetType.getName(),
                    Boolean.TRUE.equals(isList),
                    body.length());
            if (Boolean.TRUE.equals(isList)) {
                var listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, targetType);
                exchange.getIn().setBody(objectMapper.readValue(body, listType));
            } else {
                exchange.getIn().setBody(objectMapper.readValue(body, targetType));
            }
        } else {
            log.debug("Skipping request deserialization exchangeId={} routeId={} targetTypePresent={} bodyPresent={}",
                    exchange.getExchangeId(),
                    exchange.getFromRouteId(),
                    targetType != null,
                    body != null);
        }
    }
}
