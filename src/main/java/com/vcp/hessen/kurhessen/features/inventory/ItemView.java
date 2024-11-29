package com.vcp.hessen.kurhessen.features.inventory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.features.inventory.components.ItemDetailsForm;
import com.vcp.hessen.kurhessen.features.inventory.data.Item;
import com.vcp.hessen.kurhessen.features.inventory.data.ItemService;
import com.vcp.hessen.kurhessen.features.usermanagement.compoenents.MyUploadI18N;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PageTitle("Inventar")
@Route(value = "items", layout = MainLayout.class)
@RolesAllowed("INVENTORY_READ")
@Uses(Icon.class)
@Slf4j
public class ItemView extends Div {

    private final TreeGrid<Item> treeGrid;
    private final Filters filters;
    private final ItemDetailsForm form;
    private final ItemService itemService;

    public ItemView(ItemService itemService, AuthenticatedUser user, MultipartProperties multipartProperties) {
        this.itemService = itemService;
        setSizeFull();
        addClassNames("mitglieder-view");

        filters = new Filters(user, this::refreshGrid);
        VerticalLayout layout = new VerticalLayout();
        layout.add(createImportButton());
        layout.add(createMobileFilters());
        layout.add(filters);

        form = new ItemDetailsForm(
                multipartProperties,
                value -> {
                    if (value instanceof ItemDetailsForm.SaveEvent event) {
                        saveItem(event);
                    }
                    if (value instanceof ItemDetailsForm.DeleteEvent event) {
                        deleteItem(event);
                    }
                    if (value instanceof ItemDetailsForm.CancelEvent) {
                        closeEditor();
                    }
                }
        );

        treeGrid = createGrid();

        SplitLayout splitLayout = new SplitLayout(treeGrid, form);
        splitLayout.setSizeFull();
        layout.add(splitLayout);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        closeEditor();
    }

    @NotNull
    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }
    @NotNull
    private Component createImportButton() {

        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("application/json");

        MyUploadI18N i18n = new MyUploadI18N();
        i18n.getAddFiles().setOne(new TranslatableText("Upload").translate());
        i18n.getDropFiles().setOne(new TranslatableText("DropFileHere").translate());
        i18n.getError()
                .setIncorrectFileType(
                        "The provided file does not have the correct format. Please provide a json document.");
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
            String errMsg  = itemService.importFile(importFile);

            //noinspection ResultOfMethodCallIgnored
            importFile.delete();

            if (!errMsg.isBlank()) {
                Notification notification = Notification.show(
                        errMsg,
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                this.refreshGrid();
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

        return upload;
    }

    public static class Filters extends Div implements Specification<Item> {

       private final AuthenticatedUser user;
        private final TextField name = new TextField(new TranslatableText("Name").translate());

        public Filters(AuthenticatedUser user, Runnable onSearch) {
            this.user = user;

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            name.setPlaceholder(new TranslatableText("Name").translate());

            // Action buttons
            Button resetBtn = new Button(new TranslatableText("Reset").translate());
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> name.clear());
            Button searchBtn = new Button(new TranslatableText("Search").translate());
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(
                    name,
                    actions
            );
        }

        @SuppressWarnings({"NullableProblems", "OptionalGetWithoutIsPresent"})
        @Override
        public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            Predicate tribeMatch = criteriaBuilder.equal(root.get("tribe"), user.get().get().getTribe());
            predicates.add(tribeMatch);

            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        lowerCaseFilter + "%");
                predicates.add(firstNameMatch);
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    @NotNull
    private TreeGrid<Item> createGrid() {
        TreeGrid<Item> treeGrid = new TreeGrid<>();
        treeGrid.setSizeFull();
        treeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        treeGrid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        treeGrid.addComponentHierarchyColumn(item -> {
            Avatar avatar = new Avatar();
            avatar.setName(item.getName());

            if (item.getPicture() != null) {
                    StreamResource resource = new StreamResource("item-pic",
                            () -> new ByteArrayInputStream(item.getPicture()));
                    avatar.setImageResource(resource);
                }

            Span name = new Span(item.getName());

            Span id = new Span("id: " + item.getId());
            id.getStyle()
                        .set("color", "var(--lumo-secondary-text-color)")
                        .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout column = new VerticalLayout(name, id);
            column.getStyle().set("line-height", "var(--lumo-line-height-m)");
            column.setPadding(false);
            column.setSpacing(false);

            HorizontalLayout row = new HorizontalLayout(avatar, column);
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setSpacing(true);
            return row;
        }).setHeader(new TranslatableText("Item").translate());
        treeGrid.addColumn((ValueProvider<Item, String>) Item::getDescription);
        treeGrid.asSingleSelect().addValueChangeListener(event -> editItem(event.getValue()));

        TreeDataProvider<Item> dataProvider = (TreeDataProvider<Item>) treeGrid.getDataProvider();
        TreeData<Item> data = dataProvider.getTreeData();
        Set<Item> rootItems = itemService.getAllRootElements();
        data.addRootItems(rootItems);

        for (Item rootItem : rootItems) {
            data.addItems(rootItem, rootItem.getItems());
        }


        return treeGrid;
    }

    private void editItem(Item value) {
        if (value == null) {
            closeEditor();
        } else {
            form.setItem(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setItem(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void refreshGrid() {
        treeGrid.getDataProvider().refreshAll();
    }

    private void saveItem(ItemDetailsForm.SaveEvent event) {
        try {
            itemService.update(event.getItem());
            refreshGrid();
            closeEditor();
        } catch (Exception e) {
            Notification notification = Notification.show(
                    e.getMessage(),
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
    }
    private void deleteItem(ItemDetailsForm.DeleteEvent event) {
        itemService.delete(event.getItem());
        refreshGrid();
        closeEditor();
    }
}
