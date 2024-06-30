package in.nvijaykarthik.cmc.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cmc.activemq")
@Data
public class MqConnectionProperties {

    String brokerUrl;
    String user;
    String password;
    Integer cacheSize;
}
