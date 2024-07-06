package in.nvijaykarthik.cmc.config.client;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomBootstrapRegistryInitializer implements BootstrapRegistryInitializer {

      private RestTemplate createRestTemplate(Properties props) {
        try {
            if(null==props){
                log.info("unable to load properties to create rest template");
                return new RestTemplate();
            }
            log.info("Creating rest template");
            String keyStorePath = props.getProperty("spring.config.ssl.key-store");
            String keyStorePassword = props.getProperty("spring.config.ssl.key-store-password");
            String keyPassword = props.getProperty("spring.config.ssl.key-password");
            String trustStorePath = props.getProperty("spring.config.ssl.trust-store");
            String trustStorePassword = props.getProperty("spring.config.ssl.trust-store-password");

            // Load KeyStore and TrustStore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            KeyStore trustStore = KeyStore.getInstance("JKS");

            try (InputStream keyStoreStream = new ClassPathResource(keyStorePath).getInputStream();
                 InputStream trustStoreStream = new ClassPathResource(trustStorePath).getInputStream()) {
                keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
                trustStore.load(trustStoreStream, trustStorePassword.toCharArray());
            }

            // Create SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyPassword.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Create HTTPS connection factory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            // Set up HostnameVerifier to bypass verification
            HostnameVerifier allowAllHosts = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allowAllHosts);

            // Create simple client HTTP request factory
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            // Set timeout properties if needed
            requestFactory.setConnectTimeout(5000);
            requestFactory.setReadTimeout(5000);

            // Return customized RestTemplate
            return new RestTemplateBuilder()
                    .requestFactory(() -> requestFactory)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with SSL support", e);
        }
    }

    @Override
    public void initialize(BootstrapRegistry registry) {
        log.info("Started customizing");
        registry.registerIfAbsent(RestTemplate.class, context -> {
            Resource resource = new ClassPathResource("application.properties");
            Properties props=new Properties();
            try {
                props = PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("{}",props);
           // Environment environment = context.get(Environment.class);
            RestTemplate restTemplate = createRestTemplate(props);
            return restTemplate;
        });
    }
}
