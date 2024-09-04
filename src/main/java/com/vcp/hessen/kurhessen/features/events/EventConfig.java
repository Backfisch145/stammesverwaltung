package com.vcp.hessen.kurhessen.features.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.stammesverwaltung.event")
@Configuration("EventConfig")
@Getter
@Setter
@ToString
public class EventConfig {
    private boolean enabled = false;
}
