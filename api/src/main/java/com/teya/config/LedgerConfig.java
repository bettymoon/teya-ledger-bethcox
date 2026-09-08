package com.teya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.teya.service.LedgerService;

@Configuration
public class LedgerConfig {

    @Bean
    public LedgerService ledgerService() {
        return new LedgerService();
    }
}
