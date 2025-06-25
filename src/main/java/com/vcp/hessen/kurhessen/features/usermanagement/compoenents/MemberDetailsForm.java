package com.vcp.hessen.kurhessen.features.usermanagement.compoenents;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.util.Callback;
import com.vcp.hessen.kurhessen.data.Gender;
import com.vcp.hessen.kurhessen.data.Level;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.core.components.DatePickerLocalised;


public class MemberDetailsForm extends FormLayout {
    private final BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    private IntegerField membershipId;
    private TextField username;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private DatePicker joinDate;
    private TextField address;
    private ComboBox<Gender> gender = new ComboBox<>(new TranslatableText("Gender").translate(), Gender.getEntries());
    private ComboBox<Level> level = new ComboBox<>(new TranslatableText("Level").translate(), Level.getEntries());
    private final Button cancel = new Button(new TranslatableText("Cancel").translate());
    private final Button save = new Button(new TranslatableText("Save").translate());
    private final Button delete = new Button(new TranslatableText("Delete").translate());
    private final Callback<MemberDetailsFormEvent> actionCallback;

    public MemberDetailsForm(Callback<MemberDetailsFormEvent> onAction) {

        this.actionCallback = onAction;
        membershipId = new IntegerField(new TranslatableText("MembershipNumber").translate());
        username = new TextField(new TranslatableText("Username").translate());
        firstName = new TextField(new TranslatableText("FirstName").translate());
        lastName = new TextField(new TranslatableText("LastName").translate());
        email = new TextField(new TranslatableText("Email").translate());
        phone = new TextField(new TranslatableText("Phone").translate());
        dateOfBirth = new DatePickerLocalised(new TranslatableText("Birthday").translate());
        joinDate = new DatePickerLocalised(new TranslatableText("Joining").translate());
        joinDate.setEnabled(false);
        address = new TextField(new TranslatableText("Address").translate());
        gender.setItemLabelGenerator(Gender::getTitleTranslated);
        level.setItemLabelGenerator(Level::getTitleTranslated);

        binder.bindInstanceFields(this);
        this.add(membershipId, username, firstName, lastName, email, phone, dateOfBirth, joinDate, address, gender, level, createButtonLayout());
    }

    public void setUser(User value) {
        binder.setBean(value);
    }


    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete, cancel);


        save.addClickListener(click -> validateAndSave());
        cancel.addClickListener(click -> actionCallback.call(new CancelEvent(this, binder.getBean())));
        delete.addClickListener(click -> actionCallback.call(new DeleteEvent(this, binder.getBean())));

        binder.addStatusChangeListener(statusChangeEvent -> save.setEnabled(binder.isValid()));

        return buttonLayout;
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            actionCallback.call(new SaveEvent(this, binder.getBean()));
        }
    }

    public static abstract class MemberDetailsFormEvent extends ComponentEvent<MemberDetailsForm> {
        private User user;

        protected MemberDetailsFormEvent(MemberDetailsForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends MemberDetailsFormEvent {
        SaveEvent(MemberDetailsForm source, User user) {
            super(source, user);
        }
    }
    public static class DeleteEvent extends MemberDetailsFormEvent {
        DeleteEvent(MemberDetailsForm source, User user) {
            super(source, user);
        }
    }
    public static class CancelEvent extends MemberDetailsFormEvent {
        CancelEvent(MemberDetailsForm source, User user) {
            super(source, user);
        }
    }
}
