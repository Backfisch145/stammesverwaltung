package com.vcp.hessen.kurhessen.features.inventory.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.server.StreamResource;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.util.Callback;
import com.vcp.hessen.kurhessen.features.inventory.data.Item;
import com.vcp.hessen.kurhessen.core.components.MyUploadI18N;
import com.vcp.hessen.kurhessen.core.components.DatePickerLocalised;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.http.MediaType;

import java.io.*;


@Slf4j
public class ItemDetailsForm extends FormLayout {
    private final BeanValidationBinder<Item> binder = new BeanValidationBinder<>(Item.class);
    private Avatar avatar;
    private TextField name;
    private TextField description;
    private DatePicker createdAt;
    private final Button cancel = new Button(new TranslatableText("Cancel").translate());
    private final Button save = new Button(new TranslatableText("Save").translate());
    private final Button delete = new Button(new TranslatableText("Delete").translate());
    private final Callback<ItemDetailsFormEvent> actionCallback;


    private final MultipartProperties multipartProperties;

    public ItemDetailsForm(MultipartProperties multipartProperties, Callback<ItemDetailsFormEvent> onAction) {
        this.actionCallback = onAction;
        this.multipartProperties = multipartProperties;
        avatar = new Avatar();
        avatar.setHeight(25f, Unit.PERCENTAGE);
        avatar.setWidth(25f, Unit.PERCENTAGE);

        name = new TextField(new TranslatableText("Name").translate());
        description = new TextField(new TranslatableText("Description").translate());
        createdAt = new DatePickerLocalised(new TranslatableText("AddedAt").translate());

        binder.bindInstanceFields(this);
        this.add(avatar, name, description, createdAt, createButtonLayout());
    }

    public void setItem(Item value) {
        binder.setBean(value);
        if (value != null) {
            avatar.setName(value.getName());
            if (value.getPicture() != null && value.getPicture().length > 0) {
                StreamResource resource = new StreamResource("item-pic",
                        () -> new ByteArrayInputStream(value.getPicture()));
                avatar.setImageResource(resource);
            }
        }
    }

    private Upload uploadButton() {
        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);

        MyUploadI18N i18n = new MyUploadI18N();
        i18n.getAddFiles().setOne(new TranslatableText("UploadImage").translate());
        i18n.getDropFiles().setOne(new TranslatableText("DropFileHere").translate());
        i18n.getError()
                .setIncorrectFileType(new TranslatableText("WrongFileFormat").translate());
        upload.setI18n(i18n);

        upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            InputStream fileData = fileBuffer.getInputStream();
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            FileData savedFileData = fileBuffer.getFileData();
            String absolutePath = savedFileData.getFile().getAbsolutePath();

            log.info("upload: fileData[{}] fileName[{}] length[{}] mimeType[{}] location[{}]", fileData, fileName, contentLength, mimeType, absolutePath);

            File importFile = new File(absolutePath);

            try {
                FileInputStream inputStream = new FileInputStream(importFile);
                byte[] picture = inputStream.readAllBytes();
                binder.getBean().setPicture(picture);
                try {
                    StreamResource resource = new StreamResource("item-pic",
                            () -> new ByteArrayInputStream(picture));
                    avatar.setImageResource(resource);
                } catch (Exception e) {
                    log.warn("addSucceededListener: Could not set Avatar directly after upload", e);
                }
            } catch (IOException e) {

                Notification notification = Notification.show(
                        new TranslatableText("AvatarCouldNotBeSet").translate(),
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            //noinspection ResultOfMethodCallIgnored
            importFile.delete();
        });
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        upload.setMaxFileSize((int)multipartProperties.getMaxFileSize().toBytes());
        upload.setDropAllowed(false);

        return upload;
    }
    @NotNull
    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, uploadButton(), delete, cancel);

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

    @Getter
    public static abstract class ItemDetailsFormEvent extends ComponentEvent<ItemDetailsForm> {
        private final Item item;

        protected ItemDetailsFormEvent(ItemDetailsForm source, Item item) {
            super(source, false);
            this.item = item;
        }
    }
    public static class SaveEvent extends ItemDetailsFormEvent {
        SaveEvent(ItemDetailsForm source, Item item) {
            super(source, item);
        }
    }
    public static class DeleteEvent extends ItemDetailsFormEvent {
        DeleteEvent(ItemDetailsForm source, Item item) {
            super(source, item);
        }
    }
    public static class CancelEvent extends ItemDetailsFormEvent {
        CancelEvent(ItemDetailsForm source, Item item) {
            super(source, item);
        }
    }
}
