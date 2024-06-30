package in.nvijaykarthik.cmc.service;

import java.util.ArrayList;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class CmcSplitter {

    public ArrayList<Message<String>> splitMessage(Message<?> message) {

        ArrayList<Message<String>> messages = new ArrayList<>();
        messages.add(MessageBuilder.withPayload("Hello").copyHeaders(message.getHeaders()).build());
        messages.add(MessageBuilder.withPayload("World").copyHeaders(message.getHeaders()).build());
        messages.add(MessageBuilder.withPayload("Spring").copyHeaders(message.getHeaders()).build());
        return messages;

    }

}
