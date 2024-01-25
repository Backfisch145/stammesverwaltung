package com.vcp.hessen.kurhessen.views.meinedaten;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vcp.hessen.kurhessen.components.PictureAllowanceCheckBox;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;

@PageTitle("Meine Daten")
@Route(value = "me", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class MeineDatenView extends Composite<VerticalLayout> {

    private final AuthenticatedUser authenticatedUser;
    private final UserRepository userRepository;

    private final UserForm form;
    record UserForm(
            TextField firstName,
            TextField lastName,
            DatePicker birthday,
            ComboBox<Gender> gender,
            TextField phone,
            EmailField email,
            TextField intolerances,
            TextField eatingHabits,
            PictureAllowanceCheckBox picturesAllowed
    ) {
        public boolean isValid() {
            if (firstName.isInvalid()) {
                return false;
            }
            if (lastName.isInvalid()) {
                return false;
            }
            if (birthday.isInvalid()) {
                return false;
            }
            if (gender.isInvalid()) {
                return false;
            }
            if (phone.isInvalid()) {
                return false;
            }
            if (email.isInvalid()) {
                return false;
            }
            if (intolerances.isInvalid()) {
                return false;
            }
            if (eatingHabits.isInvalid()) {
                return false;
            }
            if (picturesAllowed.isInvalid()) {
                return false;
            }

            return true;
        }
    }

    public MeineDatenView(AuthenticatedUser authenticatedUser, UserRepository userRepository) {
        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;

        form = new UserForm(
                firstNameElement(),
                lastNameElement(),
                birthdayElement(),
                genderElement(),
                phoneElement(),
                emailElement(),
                intolerancesElement(),
                eatingHabitsElement(),
                picturesAllowedElement()
        );


        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn2.setAlignItems(Alignment.START);
        h3.setText("Personal Information");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(membershipIdElement());
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(form.firstName);
        formLayout2Col.add(form.lastName);
        formLayout2Col.add(form.birthday);
        formLayout2Col.add(form.gender);
        formLayout2Col.add(form.phone);
        formLayout2Col.add(form.email);
        layoutColumn2.add(form.intolerances);
        layoutColumn2.add(form.eatingHabits);
        layoutColumn2.add(new Hr());
        layoutColumn2.add(form.picturesAllowed);
        layoutColumn2.add(new Hr());
        layoutColumn2.add(layoutRow);
        layoutRow.add(saveButton());
        getContent().add(layoutRow2);
    }

    @NotNull
    private TextField membershipIdElement() {
        TextField textField = new TextField();
        textField.setLabel(new TranslatableText("MembershipNumber").translate());
        textField.setWidth("400px");
        textField.setReadOnly(true);
        authenticatedUser.get().ifPresent(u -> textField.setValue("" + u.getMembershipId()));
        return textField;
    }
    @NotNull
    private TextField firstNameElement() {
        TextField textField = new TextField();
        textField.setLabel("Vorname");
        textField.setWidth("400px");
        textField.setMinLength(1);
        authenticatedUser.get().ifPresent(u -> textField.setValue(u.getFirstName()));
        return textField;
    }
    @NotNull
    private TextField lastNameElement() {
        TextField textField = new TextField();
        textField.setLabel("Nachname");
        textField.setWidth("400px");
        textField.setMinLength(1);
        authenticatedUser.get().ifPresent(u -> textField.setValue(u.getLastName()));
        return textField;
    }
    @NotNull
    private TextField phoneElement() {
        TextField textField = new TextField();
        textField.setLabel("Telefonnummer");
        textField.setWidth("min-content");
        authenticatedUser.get().ifPresent(u -> textField.setValue(u.getPhone()));
        return textField;
    }
    @NotNull
    private EmailField emailElement() {
        EmailField emailField = new EmailField();
        emailField.setLabel("Email");
        emailField.setWidth("min-content");
        authenticatedUser.get().ifPresent(u -> emailField.setValue(u.getEmail()));
        return emailField;
    }
    @NotNull
    private DatePicker birthdayElement() {
        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Geburtstag");
        authenticatedUser.get().ifPresent(u -> datePicker.setValue(u.getDateOfBirth()));
        return datePicker;
    }
    @NotNull
    private ComboBox<Gender> genderElement() {
        ComboBox<Gender> comboBox = new ComboBox<>();
        comboBox.setLabel("Geschlecht");
        comboBox.setWidth("min-content");
        comboBox.setItems(Gender.getEntries());
        comboBox.setItemLabelGenerator(g -> new TranslatableText(g.getLangKey()).translate());
        authenticatedUser.get().ifPresent(u -> comboBox.setValue(u.getGender()));
        return comboBox;
    }
    @NotNull
    private TextField intolerancesElement() {
        TextField textField = new TextField();
        textField.setLabel(new TranslatableText("Intolerances").translate());
        textField.setPlaceholder(new TranslatableText("IntolerancesPlaceholder").translate());
        textField.setWidthFull();
        authenticatedUser.get().ifPresent(u -> textField.setValue(u.getIntolerances()));
        return textField;
    }
    @NotNull
    private TextField eatingHabitsElement() {
        TextField textField = new TextField();
        textField.setLabel(new TranslatableText("EatingHabits").translate());
        textField.setPlaceholder(new TranslatableText("EatingHabitsPlaceholder").translate());
        textField.setWidthFull();
        authenticatedUser.get().ifPresent(u -> textField.setValue(u.getEatingHabits()));
        return textField;
    }

    @NotNull
    private PictureAllowanceCheckBox picturesAllowedElement() {
        PictureAllowanceCheckBox pictureAllowance = new PictureAllowanceCheckBox();

        authenticatedUser.get().ifPresent(u -> pictureAllowance.setValue(u.getPicturesAllowed()));
        return pictureAllowance;
    }


    @NotNull
    private Button saveButton() {
        Button buttonPrimary = new Button();
        buttonPrimary.setText("Save");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonPrimary.setDisableOnClick(true);
        buttonPrimary.addClickListener(event -> {
            authenticatedUser.get().ifPresent(u -> {

                if (!form.isValid()) {
                    Notification
                            .show(new TranslatableText("FormFieldsContainErrors").translate())
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }



                u.setFirstName(form.firstName.getValue());
                u.setLastName(form.lastName.getValue());
                u.setDateOfBirth(form.birthday.getValue());
                u.setEmail(form.email.getValue());
                u.setGender(form.gender.getValue());
                u.setPhone(form.phone.getValue());
                u.setIntolerances(form.intolerances.getValue());
                u.setEatingHabits(form.eatingHabits.getValue());
                u.setPicturesAllowed(form.picturesAllowed.getValue());
                userRepository.save(u);
                Notification
                        .show(new TranslatableText("UserUpdated").translate())
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });
            buttonPrimary.setEnabled(true);
        });

        return buttonPrimary;
    }




}
