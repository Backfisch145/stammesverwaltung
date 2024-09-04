package com.vcp.hessen.kurhessen.components;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PictureAllowanceCheckBox extends VerticalLayout {
    private static final boolean DEFAULT_STATE = false;

    private final Checkbox checkbox;

    public PictureAllowanceCheckBox() {

        H2 h2 = new H2(new TranslatableText("PictureAllowance").translate());
        add(h2);
        String asd = new TranslatableText("PictureAllowanceDescription").translate();
        Paragraph paragraph = new Paragraph(asd);
        paragraph.getStyle().setWhiteSpace(Style.WhiteSpace.PRE_LINE);
        add(paragraph);

        checkbox = new Checkbox();
        checkbox.setLabel(new TranslatableText("MarkConsentForUpcomingRegistrations").translate());
        checkbox.setValue(DEFAULT_STATE);
        add(checkbox);
    }

    public void setValue(Boolean state) {
        if (state == null) {
            state = DEFAULT_STATE;
        }

        checkbox.setValue(state);
    }

    public boolean getValue() {
        return checkbox.getValue();
    }

    public boolean isInvalid() {
        return false;
    }

}
