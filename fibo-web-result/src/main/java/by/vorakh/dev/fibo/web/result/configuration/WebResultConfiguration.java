package by.vorakh.dev.fibo.web.result.configuration;

import by.vorakh.dev.fibo.service.configuration.ServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ServiceConfiguration.class)
public class WebResultConfiguration {

}
