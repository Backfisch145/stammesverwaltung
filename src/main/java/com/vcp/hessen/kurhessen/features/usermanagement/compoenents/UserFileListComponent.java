package com.vcp.hessen.kurhessen.features.usermanagement.compoenents;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.FileFactory;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.components.MyUploadI18N;
import com.vcp.hessen.kurhessen.core.constants.CustomColors;
import com.vcp.hessen.kurhessen.core.constants.Directories;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class UserFileListComponent extends VerticalLayout implements HasValue<AbstractField.ComponentValueChangeEvent<UserFileListComponent, Set<UserFile>>, Set<UserFile>> {

    private Set<UserFile> value = new LinkedHashSet<>();
    private boolean readOnly = false;
    private final List<ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<UserFileListComponent, Set<UserFile>>>> listeners = new ArrayList<>();
    private final AuthenticatedUser authenticatedUser;
    private User user;

    public UserFileListComponent(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setSpacing(true);
        setPadding(false);
        setWidthFull();
        log.info("UserFileListComponent: constructor called");
    }

    public void addFile(UserFile file) {
        if (readOnly) return;
        value.add(file);
        refreshDisplay();
        fireEvent();
    }

    public void removeFile(UserFile file) {
        if (readOnly) return;
        value.remove(file);
        refreshDisplay();
        fireEvent();
    }

    private void refreshDisplay() {
        removeAll();
        add(createFileItem(value));
    }

    private Component createFileItem(Set<UserFile> files) {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        H3 title = new H3(new TranslatableText("Files").translate() + ":");
        title.getStyle().setPaddingBottom("0mm");
//            title.getStyle().setTextDecoration("underline");

        root.add(title);
        root.add(new Hr());

        if (files.isEmpty()) {
            root.add(new Paragraph(new TranslatableText("NoFilesUploaded").translate()));
        } else {
            for (UserFile file : files) {
                HorizontalLayout fileLayout = new HorizontalLayout();
                fileLayout.setWidthFull();
                fileLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                fileLayout.setClassName(LumoUtility.Background.CONTRAST_10);
                fileLayout.addClassName(LumoUtility.Padding.XSMALL);
                fileLayout.addClassName(LumoUtility.BorderRadius.MEDIUM);

                fileLayout.addClickListener(click -> getUI().get().getPage().open("/api/files/" + file.getId(), "_blank"));

                Icon icon = VaadinIcon.FILE.create();
                icon.addClassName(LumoUtility.IconSize.MEDIUM);
                fileLayout.add(icon);

                VerticalLayout vl = new VerticalLayout();
                vl.setSpacing(false);
                vl.setMargin(false);
                vl.setPadding(false);

                Span span = new Span(new TranslatableText(file.getType().translationKey) + "\n");
                span.setClassName(LumoUtility.FontWeight.BOLD);

                vl.add(span);
                Span fileNameText = new Span(file.getFilename());
                vl.add(fileNameText);
                fileLayout.add(vl);
                fileLayout.setFlexGrow(1, vl);

                Paragraph fileMetadata = new Paragraph(file.getUploadDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + "\n" +
                        file.getSizeOfFileString()
                );


                fileLayout.add(fileMetadata);

                Button button = new Button();
                button.setIcon(VaadinIcon.TRASH.create());
                button.getStyle().setColor(CustomColors.DELETE_COLOR.color);
                button.getStyle().setBackground(CustomColors.DELETE_COLOR_CONTAINER.color);
                button.addClickListener(click -> {
                    new File(file.getPath()).delete();
                    removeFile(file);
                });
                fileLayout.add(button);
                root.add(fileLayout);
            }
            root.add(new Hr());
        }


        FileFactory fileFactory = fileName -> {
            File dir = Directories.USER_FILES.getDir();
            File file = new File(dir, fileName);
            dir.mkdirs();
            file.createNewFile();
            return file;
        };

        HorizontalLayout uploadLayout = new HorizontalLayout();
        for (UserFile.UserFileType type : UserFile.UserFileType.values()) {
            MultiFileBuffer fileBuffer = new MultiFileBuffer(fileFactory);

            Upload upload = new Upload();
            upload.setReceiver(fileBuffer);
            upload.setDropLabelIcon(null);
            upload.setMaxFileSize(8 * 1024 * 1024);
            upload.setAcceptedFileTypes("image/jpeg", "image/png", "application/pdf");

            MyUploadI18N i18n = new MyUploadI18N();
            i18n.getAddFiles().setOne(type.name());
            upload.setI18n(i18n);

            upload.addSucceededListener(event -> {

                long contentLength = event.getContentLength();
                String mimeType = event.getMIMEType();

                // Determine which file was uploaded successfully
                String uploadFileName = event.getFileName();
                // Get information for that specific file
                FileData savedFileData = fileBuffer.getFileData(uploadFileName);
                String absolutePath = savedFileData.getFile().getAbsolutePath();

                log.info("upload: fileName[{}] length[{}] mimeType[{}] location[{}]", uploadFileName, contentLength, mimeType, absolutePath);

                UserFile userFile = new UserFile(authenticatedUser.get().get(), uploadFileName, absolutePath, type);
                userFile.setUser(user);

                boolean success = value.add(userFile);


                if (success) {
                    Notification notification = Notification.show(
                            new TranslatableText("FileUploaded").translate(),
                            2000,
                            Notification.Position.MIDDLE
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    upload.clearFileList();
                    addFile(userFile);
                } else {
                    Notification notification = Notification.show(
                            new TranslatableText("FileUploadFailed").translate(),
                            5000,
                            Notification.Position.MIDDLE
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

                    try {
                        new File(absolutePath).delete();
                    } catch (Exception ignored) {
                    }

                }

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
            uploadLayout.add(upload);
        }

        root.add(uploadLayout);
        return root;
    }

    @Override
    public void setValue(Set<UserFile> value) {
        this.value = value == null ? new LinkedHashSet<>() : new LinkedHashSet<>(value);
        refreshDisplay();
        fireEvent();
    }

    @Override
    public Set<UserFile> getValue() {
        return new LinkedHashSet<>(value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        // Optional: Add UI indicator (like asterisk)
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<UserFileListComponent, Set<UserFile>>> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    private void fireEvent() {
        AbstractField.ComponentValueChangeEvent<UserFileListComponent, Set<UserFile>> event =
                new AbstractField.ComponentValueChangeEvent<>(this, this, null, true);
        listeners.forEach(l -> l.valueChanged(event));
    }

    public void setTargetUser(User value) {
        this.user = value;
    }
}
