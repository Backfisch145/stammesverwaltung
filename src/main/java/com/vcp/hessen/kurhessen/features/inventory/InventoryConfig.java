package com.vcp.hessen.kurhessen.features.inventory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.stammesverwaltung.inventory")
@Configuration("InventoryConfig")
@Getter
@Setter
@ToString
public class InventoryConfig {
    private boolean enabled = false;
}
