package com.shodhAI.ShodhAI.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String datasourceType = DataSourceContextHolder.getDataSourceRoutingKey(); // e.g., "READ" or "WRITE"
        log.info("Using datasource: {}", datasourceType); // or use logger
        return DataSourceContextHolder.getDataSourceRoutingKey();
    }

}

