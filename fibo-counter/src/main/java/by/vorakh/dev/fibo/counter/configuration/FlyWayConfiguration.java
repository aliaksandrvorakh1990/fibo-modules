package by.vorakh.dev.fibo.counter.configuration;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

@Log
@Configuration
@PropertySource("classpath:flyway.properties")
public class FlyWayConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    Flyway flyway(DataSource dataSource) {

        String schema = environment.getProperty("flyway.schemas");
        String location = environment.getProperty("flyway.locations");

        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas(schema)
            .locations(location)
            .baselineOnMigrate(true)
            .load();

//        CompletableFuture.runAsync(() -> {
//            int counter = flyway.migrate();
//
//            log.info("Migrate:" + counter);
//        });

        int counter = flyway.migrate();

        log.info("Migrate:" + counter);

        return flyway;
    }
}
