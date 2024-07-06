package in.nvijaykarthik.cmc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class CMCApplication {

	public static void main(String[] args) {
		SpringApplication.run(CMCApplication.class, args);
	}
}
