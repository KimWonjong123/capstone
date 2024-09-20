package capstone.capbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableWebFlux
public class CapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapBackendApplication.class, args);
        Hooks.enableAutomaticContextPropagation();
    }

}
