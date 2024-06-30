package in.nvijaykarthik.cmc.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.util.ErrorHandler;

import in.nvijaykarthik.cmc.audit.AuditService;
import in.nvijaykarthik.cmc.audit.ThreadContext;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class ErrorService {

    @Autowired
    private AuditService auditService;

    @Bean("errChannel")
    public DirectChannel errChannel() {
        return new DirectChannel();
    }

    @Bean("errQueueChannel")
    public DirectChannel errQueueChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "errChannel")
    public void errorHandler(Message<?> message) {
        Exception exception = (Exception) message.getPayload();
        exception.printStackTrace();
        Message<String> inputMsg = ThreadContext.getInputMessage();
        auditService.sendToAuditQueueInTransaction(inputMsg, null, false, exception);       
        log.error("Error while the processing: {}", message);
        //check the exception and decide to retry or not
        //if retry is needed, throw a custom exception with retry count
        //if retry is not needed, Move the input msg to error queue
        if(exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        }
        //Move the input msg to error queue
        errQueueChannel().send(inputMsg);
        
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler() {
            @Override
            public void handleError(Throwable t) {
                log.error("Throwable Error while the processing: {}", t);
                //send the message to error channel
                
            }
        };
    }

}
