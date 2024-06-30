package in.nvijaykarthik.cmc.service;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Transformation {

    public Message<String> transform(Message<String> msg) {
        log.info("Transforming payload: {}", msg.getPayload());
        String transformedMsg= msg.getPayload().toUpperCase();
        log.info("Transformed payload: {}", transformedMsg);
        return MessageBuilder.withPayload(transformedMsg).copyHeaders(msg.getHeaders()).build();
    }
    
}
