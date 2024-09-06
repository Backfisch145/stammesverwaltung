package com.vcp.hessen.kurhessen.views.meinedaten;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vcp.hessen.kurhessen.core.i18n.TranslationHelper;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.views.components.forms.UserForm;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
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

    public MeineDatenView(AuthenticatedUser authenticatedUser, UserRepository userRepository) {
        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;

        form = new UserForm(authenticatedUser);


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
        h3.setText(new TranslatableText("MeViewTitle").translate());
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
        layoutColumn2.add(form.getMemberId());
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(form.getFirstName());
        formLayout2Col.add(form.getLastName());
        formLayout2Col.add(form.getBirthday());
        formLayout2Col.add(form.getGender());
        formLayout2Col.add(form.getPhone());
        formLayout2Col.add(form.getEmail());
        layoutColumn2.add(form.getIntolerances());
        layoutColumn2.add(form.getEatingHabits());
        layoutColumn2.add(new Hr());
        layoutColumn2.add(form.getEmergencyContact());
        layoutColumn2.add(new Hr());
        layoutColumn2.add(form.getPicturesAllowed());
        layoutColumn2.add(new Hr());
        layoutColumn2.add(layoutRow);
        layoutRow.add(saveButton());
        getContent().add(layoutRow2);
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

                u.setFirstName(form.getFirstName().getValue());
                u.setLastName(form.getLastName().getValue());
                u.setDateOfBirth(form.getBirthday().getValue());
                u.setEmail(form.getEmail().getValue());
                u.setGender(form.getGender().getValue());
                u.setPhone(form.getPhone().getValue());
                u.setIntolerances(form.getIntolerances().getValue());
                u.setEatingHabits(form.getEatingHabits().getValue());
                form.getPicturesAllowed();
                u.setPicturesAllowed(form.getPicturesAllowed().getValue());
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
