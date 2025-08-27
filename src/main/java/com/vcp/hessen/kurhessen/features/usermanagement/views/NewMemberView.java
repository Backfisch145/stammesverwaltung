package com.vcp.hessen.kurhessen.features.usermanagement.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.TribeService;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;
import com.vcp.hessen.kurhessen.features.usermanagement.compoenents.MyselfForm;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;


@Route(value = NewMemberView.URL_PATH, layout = MainLayout.class)
@RolesAllowed("MEMBER_READ")
@Uses(Icon.class)
public class NewMemberView extends Composite<VerticalLayout> implements HasDynamicTitle {
    public static final String URL_PATH = "members/add";

    private final MyselfForm form;

    public NewMemberView(AuthenticatedUser authenticatedUser, UserRepository userRepository, TribeService tribeService) {

        User u = new User();
        u.setTribe(authenticatedUser.get().get().getTribe());


        form = new MyselfForm(
                tribeService,
                value -> {
            userRepository.save(u);
            Notification
                    .show(new TranslatableText("UserUpdated").translate())
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        form.setUser(u);

        form.getMembershipId().setEnabled(true);
        form.getPicturesAllowed().setVisible(false);
        getContent().add(form);
    }


    @Override
    public String getPageTitle() {
        return new TranslatableText("AddUser").translate();
    }
}
