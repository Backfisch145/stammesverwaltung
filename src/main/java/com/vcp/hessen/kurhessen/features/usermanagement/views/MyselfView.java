package com.vcp.hessen.kurhessen.features.usermanagement.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.TribeService;
import com.vcp.hessen.kurhessen.data.UserRepository;
import com.vcp.hessen.kurhessen.features.usermanagement.compoenents.MyselfForm;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PageTitle("Meine Daten")
@Route(value = "me", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MyselfView extends Composite<VerticalLayout> {

    private final MyselfForm form;

    public MyselfView(AuthenticatedUser authenticatedUser, UserRepository userRepository, TribeService tribeService) {

        form = new MyselfForm(
                tribeService,
                value -> {
            userRepository.save(value);
            Notification
                    .show(new TranslatableText("UserUpdated").translate())
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        form.setUser(authenticatedUser.get().get());

        getContent().addAndExpand(form);
    }





}
