//package com.shodhAI.ShodhAI.Configuration;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Bean(name = "writeDataSource")
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource.write")
//    public DataSource writeDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "readDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.read")
//    public DataSource readDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "writeDataSource")
//    @Primary
//    public DataSource writeDataSource() {
//        return DataSourceBuilder.create()
//                .url("jdbc:postgresql://shodhai.cr464e2myueb.ap-south-1.rds.amazonaws.com:5432/postgres")
//                .username("postgres")
//                .password("shodhAIpostgres")
//                .driverClassName("org.postgresql.Driver")
//                .build();
//    }
//
//    @Bean(name = "readDataSource")
//    public DataSource readDataSource() {
//        return DataSourceBuilder.create()
//                .url("jdbc:postgresql://shodhai.cr464e2myueb.ap-south-1.rds.amazonaws.com:5432/postgres")
//                .username("postgres")
//                .password("shodhAIpostgres")
//                .driverClassName("org.postgresql.Driver")
//                .build();
//    }
//
//    @Bean
//    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
//                                        @Qualifier("readDataSource") DataSource readDataSource) {
//        Map<Object, Object> dataSources = new HashMap<>();
//        dataSources.put("WRITE", writeDataSource);
//        dataSources.put("READ", readDataSource);
//
//        RoutingDataSource routingDataSource = new RoutingDataSource();
//        routingDataSource.setDefaultTargetDataSource(writeDataSource);
//        routingDataSource.setTargetDataSources(dataSources);
//        return routingDataSource;
//    }
//
//    @Bean
//    public DataSource dataSource(DataSource routingDataSource) {
//        return new LazyConnectionDataSourceProxy(routingDataSource);
//    }
//}
//
//

package com.shodhAI.ShodhAI.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean(name = "writeDataSource")
    @Primary
    public DataSource writeDataSource(WriteDataSourceProperties writeDataSourceProperties) {
        return writeDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource(ReadDataSourceProperties readDataSourceProperties) {
        return readDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                        @Qualifier("readDataSource") DataSource readDataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        routingDataSource.setTargetDataSources(Map.of("WRITE", writeDataSource, "READ", readDataSource));
        return routingDataSource;
    }

    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}

