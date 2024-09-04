package com.vcp.hessen.kurhessen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.stammesverwaltung")
@Configuration("stammesverwaltungProperties")
public class Config {

}
