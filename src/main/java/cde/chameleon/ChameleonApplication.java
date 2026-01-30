package cde.chameleon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

@SpringBootApplication
public class ChameleonApplication {
	static void main(String[] args) {
		SpringApplication.run(ChameleonApplication.class, args);
	}

	@Bean
	public ForwardedHeaderFilter forwardedHeaderFilter() { // because the application runs behind a reverse proxy
		return new ForwardedHeaderFilter();
	}
}
