package com.shodhAI.ShodhAI.Configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource.write")
public class WriteDataSourceProperties extends DataSourceProperties {
    // This will automatically bind properties with prefix "spring.datasource.write"
}
