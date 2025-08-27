package com.vcp.hessen.kurhessen.data;

import com.vaadin.flow.component.icon.VaadinIcon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;

@Entity
@Table(name = "user_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTag {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "tribe_id")
    private Tribe tribe;

    private String name;
    private String color = "#ffffffff";

    private String icon = VaadinIcon.TAG.name();

    public UserTag(String name) {
        this.name = name;
    }

    public VaadinIcon getIcon() {
        return VaadinIcon.valueOf(icon);
    }

    public void setIcon(VaadinIcon icon) {
        this.icon = icon.name();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getColorString() {
        return color;
    }

    public Color getColor() {
        return new Color((int)Long.parseLong(color.replace("#", ""), 16), true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        UserTag that = (UserTag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
