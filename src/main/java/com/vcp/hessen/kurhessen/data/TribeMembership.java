package com.vcp.hessen.kurhessen.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import java.time.temporal.ChronoUnit;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class TribeMembership {
    @Column
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private double price;
    private int intervall;
    @Enumerated(EnumType.STRING)
    private ChronoUnit intervallUnit;

    public double getPrice() {
        return price;
    }
}
