package com.vcp.hessen.kurhessen.features.usermanagement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.stammesverwaltung.usermanagement")
@Configuration("UsermanagementConfig")
@Getter
@Setter
@ToString
public class UsermanagementConfig {

    private String gruenDateFormat = "dd.MM.yyyy";

}
