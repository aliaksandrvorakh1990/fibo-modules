package by.vorakh.dev.fibo.jdbc.configuration;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:flyway.properties")
public class FlyWayConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @NotNull Flyway flyway(@NotNull DataSource dataSource) {

        String schema = environment.getProperty("flyway.schemas");
        String location = environment.getProperty("flyway.locations");

        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas(schema)
            .locations(location)
            .baselineOnMigrate(true)
            .load();

        flyway.migrate();

        return flyway;
    }
}
