package in.nvijaykarthik.cmc.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;

@Configuration
public class Channels {

    @Bean("audit-outboundChannel")
    public DirectChannel outboundChannel() {
        return new DirectChannel();
    }


}
