package com.vcp.hessen.kurhessen.data;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vcp.hessen.kurhessen.core.util.ColorPairGenerator;
import org.vaadin.addons.tatu.ColorPicker;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UserTagDialog extends Dialog {

    private final TextField nameField = new TextField("Name");
    private final ComboBox<VaadinIcon> iconBox = new ComboBox<>("Icon");
    private final ColorPicker colorPicker = new ColorPicker();

    private final Button saveBtn = new Button("Save");
    private final Button deleteBtn = new Button("Delete");
    private final Button cancelBtn = new Button("Cancel");

    public UserTagDialog(@Nullable UserTag tag,
                         Consumer<UserTag> onSave,
                         Consumer<UserTag> onDelete) {

        setHeaderTitle(tag == null ? "Create Tag" : "Edit Tag");

        colorPicker.setLabel("Color");
        colorPicker.setValue(tag.getColorString());


        // Configure inputs
        iconBox.setItems(VaadinIcon.values());
        iconBox.setItemLabelGenerator(VaadinIcon::name);
        iconBox.setRenderer(new ComponentRenderer<>(icon -> new Icon(icon)));
        iconBox.setWidthFull();

        if (tag != null) {
            nameField.setValue(tag.getName() != null ? tag.getName() : "");
            iconBox.setValue(tag.getIcon());
        }

        FormLayout form = new FormLayout();
        form.add(nameField, iconBox);

        saveBtn.addClickListener(e -> {
            String name = nameField.getValue();
            VaadinIcon icon = iconBox.getValue();

            if (name == null || name.trim().isEmpty() || icon == null) {
                Notification.show("Name and Icon are required.");
                return;
            }

            if (tag != null) {
                tag.setName(name);
                tag.setIcon(icon);
                onSave.accept(tag);
            } else {
                UserTag newTag = new UserTag(name);
                newTag.setIcon(icon);
                onSave.accept(newTag);
            }
            close();
        });

        deleteBtn.addClickListener(e -> {
            if (tag != null) {
                onDelete.accept(tag);
            }
            close();
        });

        cancelBtn.addClickListener(e -> close());

        getFooter().add(saveBtn);
        if (tag != null) {
            getFooter().add(deleteBtn);
        }
        getFooter().add(cancelBtn);

        add(form);
        setWidth("400px");
    }
}

