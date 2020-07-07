package by.vorakh.dev.fibo.r2dbc.configuration;

import by.vorakh.dev.fibo.jdbc.configuration.FlyWayConfiguration;
import by.vorakh.dev.fibo.jdbc.configuration.JdbcConfiguration;
import by.vorakh.dev.fibo.r2dbc.repository.TaskRepository;
import by.vorakh.dev.fibo.r2dbc.repository.impl.R2dbcTaskRepository;
import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.core.DatabaseClient;

import java.time.Duration;

@Configuration
@Import({JdbcConfiguration.class, FlyWayConfiguration.class})
@PropertySource("classpath:r2dbc.properties")
public class R2dbcConfiguration {

    @Autowired
    Environment environment;

    @Bean("mySqlConnectionFactory")
    @NotNull ConnectionFactory connectionFactory() {

        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
            .host(environment.getRequiredProperty("r2dbc.host"))
            .port(environment.getRequiredProperty("r2dbc.port", Integer.class))
            .database(environment.getRequiredProperty("r2dbc.database"))
            .username(environment.getRequiredProperty("r2dbc.user"))
            .password(environment.getRequiredProperty("r2dbc.password"))
            .connectTimeout(Duration.ofSeconds(10))
//            .useServerPrepareStatement()
            .build();

        return MySqlConnectionFactory.from(configuration);
    }

    @Bean
    @NotNull DatabaseClient databaseClient(
        @Qualifier("mySqlConnectionFactory") @NotNull ConnectionFactory connectionFactory
    ) {

        return DatabaseClient.create(connectionFactory);
    }

    @Bean
    @NotNull TaskRepository taskRepository(@NotNull DatabaseClient databaseClient) {

        return new R2dbcTaskRepository(databaseClient);
    }
}
