package in.nvijaykarthik.cmc.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cmc.queue")
@Data
public class CmcProperties {

    private String inputQueue;
    private String outputQueue;
    private String errorQueue;
    private int concurrentConsumer;
    
}
