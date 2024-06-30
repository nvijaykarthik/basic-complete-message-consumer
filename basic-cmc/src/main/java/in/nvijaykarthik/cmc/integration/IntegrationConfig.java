package in.nvijaykarthik.cmc.integration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import in.nvijaykarthik.cmc.audit.ITrackingIdService;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableTransactionManagement
@Slf4j
public class IntegrationConfig {

    @Autowired
    CmcProperties cmcProperties;

    @Autowired
    @Qualifier("activeMqJmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("jmsAmqTransactionManager")
    PlatformTransactionManager jmsAmqTransactionManager;
   
    @Bean("inpChannel")
    public DirectChannel inpChannel() {
        return new DirectChannel();
    }
   
    @Bean
    public ITrackingIdService trackingIdService() {
        return (Message<String> message) ->{
            String txnId = message.getPayload();
            return txnId;
        };
    }

    @Bean
    public IntegrationFlow jmsMessageDrivenChannelAdapter() {
        return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(connectionFactory)
                .destination(cmcProperties.getInputQueue()) // Replace with your input queue name
                .errorChannel("errChannel")
                .configureListenerContainer(t -> t.concurrentConsumers(cmcProperties.getConcurrentConsumer())
                        .transactionManager(jmsAmqTransactionManager))
                .autoStartup(true))
                .handle("threadContextService","setThreadContext")
                .channel("inpChannel")
                .get();
    }

    @Bean
    public IntegrationFlow processFlow() {
        return IntegrationFlow.from("inpChannel")
                .handle("transformation", "transform")
                .enrichHeaders(Map.of("vijay", "karthik"))
                .split("cmcSplitter","splitMessage")
                .channel("audit-outboundChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outboundFlow() {
        return IntegrationFlow.from("audit-outboundChannel")
                .headerFilter("vijay")
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination(cmcProperties.getOutputQueue()))
                .get();
    }

    @Bean
    public IntegrationFlow errorQueueFlow() {
        return IntegrationFlow.from("errQueueChannel")
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination(cmcProperties.getErrorQueue()))
                .get();
    }
}