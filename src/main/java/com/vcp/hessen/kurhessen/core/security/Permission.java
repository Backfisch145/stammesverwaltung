package com.vcp.hessen.kurhessen.core.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {
    MEMBER_READ(""),
    MEMBER_INSERT(""),
    MEMBER_DELETE(""),
    MEMBER_UPDATE(""),
    INVENTORY_READ(""),
    INVENTORY_INSERT(""),
    INVENTORY_DELETE(""),
    INVENTORY_UPDATE(""),
    EVENT_READ(""),
    EVENT_INSERT(""),
    EVENT_DELETE(""),
    EVENT_UPDATE("");

    // TODO: Add descriptions
    private String description;

}
