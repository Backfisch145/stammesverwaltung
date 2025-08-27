package com.vcp.hessen.kurhessen.features.usermanagement.compoenents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.HasValidator;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.data.UserEmergencyContact;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmergencyContactFormComponent extends CustomField<UserEmergencyContact> implements HasValidator<UserEmergencyContact> {
    private final TextField name = new TextField(new TranslatableText("Name").translate());
    private final TextField address = new TextField(new TranslatableText("Address").translate());

    private final TextField phone = new TextField(new TranslatableText("Phone").translate());
    private final TextField email = new TextField(new TranslatableText("Email").translate());


    public EmergencyContactFormComponent() {
        this(null);
    }

    @Override
    protected UserEmergencyContact generateModelValue() {
        UserEmergencyContact userEmergencyContact = new UserEmergencyContact();
        userEmergencyContact.setName(name.getValue());
        userEmergencyContact.setAddress(address.getValue());
        userEmergencyContact.setPhone(phone.getValue());
        userEmergencyContact.setEmail(email.getValue());
        return userEmergencyContact;
    }

    @Override
    protected void setPresentationValue(UserEmergencyContact contact) {
        this.name.setValue(contact.getName());
        this.address.setValue(contact.getAddress());
        this.phone.setValue(contact.getPhone());
        this.email.setValue(contact.getEmail());
    }

    public EmergencyContactFormComponent(Component title) {

        FormLayout formLayout = new FormLayout();
        if (title != null) {
            formLayout.add(title, 2);
        }
        formLayout.add(name);
        formLayout.add(address);
        formLayout.add(phone);
        formLayout.add(email);

        this.add(formLayout);
    }
}
