package in.nvijaykarthik.cmc.audit;

import org.springframework.messaging.Message;

public class ThreadContext {

     private static final ThreadLocal<Message<String>> inputMessageHolder = new ThreadLocal<>();

        public static void setInputMessage(Message<String> message) {
            inputMessageHolder.set(message);
        }

        public static Message<String> getInputMessage() {
            return inputMessageHolder.get();
        }

        public static void clear() {
            inputMessageHolder.remove();
        }
}
