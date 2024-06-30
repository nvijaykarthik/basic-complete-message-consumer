package in.nvijaykarthik.cmc.mq;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

@Configuration
public class ActionMqConnectionConfig {

    @Autowired
    MqConnectionProperties mqConnectionProperties;

    @Bean("activeMqJmsConnectionFactory")
    public ConnectionFactory activeMqJmsConnectionFactory() throws JMSException {
        
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(mqConnectionProperties.getBrokerUrl());
        activeMQConnectionFactory.setUser(mqConnectionProperties.getUser());
        activeMQConnectionFactory.setPassword(mqConnectionProperties.getPassword());

        // Wrap the ActiveMQConnectionFactory with a CachingConnectionFactory
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(mqConnectionProperties.getCacheSize()); 

        return cachingConnectionFactory;
    }


    @Bean("jmsAmqTransactionManager")
    public PlatformTransactionManager jmsAmqTransactionManager(@Autowired @Qualifier("activeMqJmsConnectionFactory") ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }

}
