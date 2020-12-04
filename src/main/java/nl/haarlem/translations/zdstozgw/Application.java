package nl.haarlem.translations.zdstozgw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@DependsOn("springContext")
@ImportResource("classpath:spring-ladybug.xml")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
