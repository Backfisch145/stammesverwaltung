package com.vcp.hessen.kurhessen.features.usermanagement.compoenents;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.components.DatePickerLocalised;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.core.util.Callback;
import com.vcp.hessen.kurhessen.core.util.ColorPairGenerator;
import com.vcp.hessen.kurhessen.core.util.converter.SetToListConverter;
import com.vcp.hessen.kurhessen.data.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
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
    private MultiSelectComboBox<UserTag> tags;
    private UserFileListComponent userFiles;
    private ComboBox<Gender> gender = new ComboBox<>(new TranslatableText("Gender").translate(), Gender.getEntries());
    private ComboBox<Level> level = new ComboBox<>(new TranslatableText("Level").translate(), Level.getEntries());
    private final Button cancel = new Button(new TranslatableText("Cancel").translate());
    private final Button save = new Button(new TranslatableText("Save").translate());
    private final Button delete = new Button(new TranslatableText("Delete").translate());
    private final Callback<MemberDetailsFormEvent> actionCallback;

    private final VirtualList<UserFile> fileList = new VirtualList<>();
    private final ArrayList<File> uploadedFiles = new ArrayList<>();
    private final AuthenticatedUser authenticatedUser;

    public MemberDetailsForm(AuthenticatedUser authenticatedUser, TribeService tribeService,Callback<MemberDetailsFormEvent> onAction) {

        this.actionCallback = onAction;
        this.authenticatedUser = authenticatedUser;
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
        userFiles = new UserFileListComponent(authenticatedUser);

        tags = new MultiSelectComboBox<>(new TranslatableText("Tags").translate());
        tags.setAllowCustomValue(true);
        tags.setRenderer(new ComponentRenderer<>(userTag -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setPadding(false);
//            layout.setSpacing(false);
//            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.addClassName(LumoUtility.Padding.SMALL);
            layout.addClassName(LumoUtility.BorderRadius.SMALL);

            Color tagColor =  userTag.getColor();
            log.info("tagColor=" +  tagColor);
            Color textColor = ColorPairGenerator.getReadableColor(tagColor);
            log.info("textColor=" +  textColor);

            layout.getStyle().set("background", ColorPairGenerator.toHex(tagColor));
            layout.getStyle().set("color", ColorPairGenerator.toHex(textColor));
            Icon icon = new Icon(userTag.getIcon());

            Paragraph paragraph = new Paragraph();
            paragraph.setText(userTag.getName());

            layout.add(icon, paragraph);
            return layout;
        }));
        tags.setItems((CallbackDataProvider.FetchCallback<UserTag, String>) query -> tribeService.getUserTags().stream());
        tags.addCustomValueSetListener((ComponentEventListener<ComboBoxBase.CustomValueSetEvent<MultiSelectComboBox<UserTag>>>) event -> {
            log.info("detail: {}", event.getDetail());

            UserTag userTag = new  UserTag();
            userTag.setName(event.getDetail());
            userTag = tribeService.addUserTag(userTag);
            tags.getDataProvider().refreshAll();
            tags.select(userTag);
        });

        binder.forField(tags).withConverter(new SetToListConverter<>());
        binder.bind(tags, User::getTags, User::setTags);
        binder.bindInstanceFields(this);

        this.add(membershipId, username, firstName, lastName, email, phone, dateOfBirth, joinDate, address, gender, level, tags);

        this.add(userFiles, 2);


        HorizontalLayout buttons = createButtonLayout();
        this.add(buttons,  2);



        userFiles.addValueChangeListener(event -> {
            if (event.getValue() != null && !event.getValue().isEmpty()) {
//                validateAndSave();
            }
        });

    }

    public void setUser(User value) {
        binder.setBean(value);
        userFiles.setTargetUser(value);

        if (value != null) {
            fileList.setDataProvider(new ListDataProvider<>(value.getUserFiles()));
        } else {
            fileList.setDataProvider(new ListDataProvider<>(Collections.emptyList()));
        }

    }

    public void setAvailableUserTags(Set<UserTag> availableUserTags) {
        tags.setItems(availableUserTags);
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete, cancel);


        save.addClickListener(click -> validateAndSave());
        cancel.addClickListener(click -> {
            //noinspection ResultOfMethodCallIgnored
            uploadedFiles.forEach(File::delete);
            actionCallback.call(new CancelEvent(this, binder.getBean()));
        });
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
