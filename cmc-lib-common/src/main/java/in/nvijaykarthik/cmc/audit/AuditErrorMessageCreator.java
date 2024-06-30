package in.nvijaykarthik.cmc.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuditErrorMessageCreator{
    
    public static final String SUCCESS="SUCCESS";
    public static final String FAILURE="FAILURE";

    public AuditErrorMessage createErrorAuditMessage(Message<?> inputMessage, Message<?> outputMessage, boolean isSuccess, Exception exception) {
        AuditErrorMessage errorAuditMessage = new AuditErrorMessage();
        errorAuditMessage.setId(UUID.randomUUID().toString());
        errorAuditMessage.setTimestamp(LocalDateTime.now().toString());

        // Set success or failure status
        if (isSuccess) {
            errorAuditMessage.setStatus(SUCCESS);
        } else {
            errorAuditMessage.setStatus(FAILURE);
        }

        // Populate input message details
        if (inputMessage != null) {
            errorAuditMessage.setInputMessage(inputMessage.getPayload().toString());
            errorAuditMessage.setInputHeaders(copyOnlyString(inputMessage.getHeaders()));
            // Add more fields as needed
        }

        // Populate output message details
        if (outputMessage != null) {
            errorAuditMessage.setOutputMessage(outputMessage.getPayload().toString());
            errorAuditMessage.setOutputHeaders(copyOnlyString(outputMessage.getHeaders()));
            // Add more fields as needed
        }

        // Populate error details in case of failure
        if (!isSuccess && exception != null) {
            errorAuditMessage.setExceptionStackTrace(exception.toString());
        }

        return errorAuditMessage;
    }

    //this is generated by copilot
    private Map<String, String> copyOnlyString(MessageHeaders headers) {
        return headers.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (String) entry.getValue()));
    }
}