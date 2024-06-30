package in.nvijaykarthik.cmc.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AuditService {

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    AuditErrorMessageCreator auditErrorMessageCreator;

    @Bean("auditChannel")
    public MessageChannel wiretapChannel() {
        return new DirectChannel();
    }

    @Bean
    @GlobalChannelInterceptor(patterns = "audit-*")
    public WireTap globalWireTap() {
        return new WireTap("auditChannel");
    }

    @Transactional(value = "jmsAmqTransactionManager", propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void sendToAuditQueueInTransaction(Message<String> inputMessage, Message<String> outputMessage,
            boolean isSuccess, Exception exception) {
        try {
            AuditErrorMessage errorAuditMessage = auditErrorMessageCreator.createErrorAuditMessage(inputMessage,
                    outputMessage, isSuccess, exception);
           
            // Send XML message to auditQueue
            jmsTemplate.convertAndSend("auditQueue", convertToJson(errorAuditMessage));
            log.info("Sent audit message to auditQueue: {}", convertToJson(errorAuditMessage));
        } catch (Exception e) {
            log.error("Error preparing or sending error audit message", e);
            //send email
            
        } finally {
            ThreadContext.clear();
        }
    }

    @ServiceActivator(inputChannel = "auditChannel")
    public void audit(Message<String> message) {
        log.info("Audit message: {}", message);
        Message<String> inputMsg = ThreadContext.getInputMessage();
        sendToAuditQueueInTransaction(inputMsg, message, true, null);
        //throw new RuntimeException("Error in audit");
    }

    private static String convertToJson(AuditErrorMessage errorAuditMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(errorAuditMessage);
        } catch (JsonProcessingException e) {
           throw new RuntimeException("Audit msgs Error converting to JSON", e);
        }
    }
}
