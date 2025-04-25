package com.shodhAI.ShodhAI.Configuration;

import lombok.Data;

@Data
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setToRead() {
        contextHolder.set("READ");
    }

    public static void setToWrite() {
        contextHolder.set("WRITE");
    }

    public static String getDataSourceRoutingKey() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

}

