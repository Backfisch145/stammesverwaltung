package com.vcp.hessen.kurhessen.core.components;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.data.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PictureAllowanceCheckBox extends CustomField<Boolean> implements HasValidator<Boolean> {

    private final Checkbox picturesAllowed = new Checkbox(new TranslatableText("Consent").translate());

    public PictureAllowanceCheckBox() {

        H2 h2 = new H2(new TranslatableText("PictureAllowance").translate());
        add(h2);
        String asd = new TranslatableText("PictureAllowanceDescription").translate();
        Paragraph paragraph = new Paragraph(asd);
        paragraph.addClassName(LumoUtility.LineHeight.SMALL);
        paragraph.getStyle().setWhiteSpace(Style.WhiteSpace.PRE_LINE);
        add(paragraph);
        add(picturesAllowed);
    }

    @Override
    protected Boolean generateModelValue() {
        return picturesAllowed.getValue();
    }

    @Override
    protected void setPresentationValue(Boolean aBoolean) {
        picturesAllowed.setValue(aBoolean);
    }

    boolean validate(Boolean value) {
        return true;
    }

    @Override
    public Validator<Boolean> getDefaultValidator() {
        return (value, context) -> {
            if (validate(value)) {
                return ValidationResult.ok();
            } else {
                return ValidationResult.error("");
            }
        };
    }
}
