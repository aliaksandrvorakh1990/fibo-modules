package by.vorakh.dev.fibo.jdbc.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:jdbc.properties")
public class JdbcConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @NotNull JdbcTemplate jdbcTemplate(@NotNull DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }

    @Bean
    @NotNull DataSource dataSource() {

        String driverClassName = environment.getProperty("dataSource.driverClassName");
        String url = environment.getProperty("dataSource.url");
        String username = environment.getProperty("dataSource.username");
        String password = environment.getProperty("dataSource.password");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
