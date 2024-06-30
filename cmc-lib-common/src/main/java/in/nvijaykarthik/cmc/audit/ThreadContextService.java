package in.nvijaykarthik.cmc.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class ThreadContextService {

    @Autowired
    ITrackingIdService trackingIdService;

    public Message<String> setThreadContext(Message<String> message) {
        Message<String> msg = MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader("tx_id", trackingIdService.getTxnId(message))
                .build();
        ThreadContext.setInputMessage(msg);
        return msg;
    }
}
