package com.vcp.hessen.kurhessen.core.components;

import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;

public class ChangePasswordDialog extends Dialog {


    public ChangePasswordDialog(AuthenticatedUser user) {
        this.setHeaderTitle(new TranslatableText("ChangePassword").translate());


        PasswordField currentPassword = new PasswordField();
        currentPassword.setLabel("Current Password");
        currentPassword.setId("currentPassword");
        PasswordField newPassword = new PasswordField();
        newPassword.setId("newPassword");
        newPassword.setLabel("New password");
        PasswordField newPasswordCheck = new PasswordField();
        newPasswordCheck.setId("newPasswordCheck");
        newPasswordCheck.setLabel("Repeat new password");


        Row row = new Row();
        row.setWidth("100%");
        row.add(new Button(new TranslatableText("Cancel").translate(), buttonClickEvent -> {
            this.close();
        }));
        row.add(new Button(new TranslatableText("ChangePassword").translate(), buttonClickEvent -> {
            if (!newPassword.getValue().equals(newPasswordCheck.getValue())) {
                Notification.show("The new passwords do not match");
                return;
            }
            user.changePassword(currentPassword.getValue(),  newPassword.getValue());
            this.close();
        }));

        this.add(new VerticalLayout(currentPassword, newPassword, newPasswordCheck, row));

    }
}
