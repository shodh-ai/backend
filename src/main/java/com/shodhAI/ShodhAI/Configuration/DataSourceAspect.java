package com.shodhAI.ShodhAI.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Slf4j
public class DataSourceAspect {

    @Before("@annotation(transactional)")
    public void setDataSource(Transactional transactional) {
        if (transactional.readOnly()) {
            DataSourceContextHolder.setToRead();
            log.info("READ ONLY");
        } else {
            DataSourceContextHolder.setToWrite();
            log.info("WRITE ONLY");
        }
    }

    @After("@annotation(transactional)")
    public void clearContext(Transactional transactional) {
        DataSourceContextHolder.clear();
    }
}

