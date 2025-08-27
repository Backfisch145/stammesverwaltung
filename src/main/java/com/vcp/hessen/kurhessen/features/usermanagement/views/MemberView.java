package com.vcp.hessen.kurhessen.features.usermanagement.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.components.MyUploadI18N;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.i18n.TranslationHelper;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.core.util.ColorPairGenerator;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.features.usermanagement.compoenents.MemberDetailsForm;
import com.vcp.hessen.kurhessen.features.usermanagement.domain.UserService;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@PageTitle("Mitglieder")
@Route(value = "members", layout = MainLayout.class)
@RolesAllowed("MEMBER_READ")
@Uses(Icon.class)
@Slf4j
public class MemberView extends Div {
    private final Grid<User> grid;
    private final Filters filters;
    private final MemberDetailsForm form;
    private final UserService userService;
    private final TribeService tribeService;
    private final AuthenticatedUser user;

    public MemberView(UserService userService, AuthenticatedUser user, TribeService tribeService) {
        this.tribeService = tribeService;
        this.userService = userService;
        this.user = user;
        setSizeFull();
        addClassNames("mitglieder-view");

        filters = new Filters(user, tribeService, this::refreshGrid);
        VerticalLayout layout = new VerticalLayout();
        layout.add(createTopButtons());
        layout.add(createMobileFilters());
        layout.add(filters);

        form = new MemberDetailsForm(
                user,
                tribeService,
                value -> {
                    if (value instanceof MemberDetailsForm.SaveEvent event) {
                        saveUser(event);
                    }
                    if (value instanceof MemberDetailsForm.DeleteEvent event) {
                        deleteUser(event);
                    }
                    if (value instanceof MemberDetailsForm.CancelEvent) {
                        closeEditor();
                    }
                }
        );
        form.setAvailableUserTags(tribeService.getUserTags());

        grid = createGrid();

        SplitLayout splitLayout = new SplitLayout(grid, form);
        splitLayout.setSizeFull();
        layout.add(splitLayout);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        closeEditor();
    }

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

    private HorizontalLayout createTopButtons() {
        HorizontalLayout topButtons = new HorizontalLayout();
        topButtons.setSpacing(true);

        topButtons.add(createImportButton());

        if (user.get().get().hasPermission("MEMBER_INSERT")) {
            topButtons.add(createAddButton());
        }

        return topButtons;
    }
    private Component createAddButton() {
        Button button = new Button(new TranslatableText("AddUser").translate(),buttonClickEvent -> {
            getUI().get().navigate("/" + NewMemberView.URL_PATH);
        });


        return button;
    }
    private Component createImportButton() {

        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        MyUploadI18N i18n = new MyUploadI18N();
        i18n.getAddFiles().setOne(new TranslatableText("ImportFromGruen").translate());
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
            String errMsg  = userService.importGruenFile(importFile);

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

    public static class Filters extends Div implements Specification<User> {

        private final AuthenticatedUser user;
        private final TextField membershipId = new TextField(new TranslatableText("MembershipNumber").translate());
        private final TextField name = new TextField(new TranslatableText("Name").translate());
        private final DatePicker startDate = new DatePicker(new TranslatableText("Birthday").translate());
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> levels = new MultiSelectComboBox<>(new TranslatableText("Level").translate());
        private final MultiSelectComboBox<String> tags = new MultiSelectComboBox<>(new TranslatableText("Tags").translate());

        public Filters(AuthenticatedUser user, TribeService tribeService, Runnable onSearch) {
            this.user = user;

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder(new TranslatableText("FirstOrLastName").translate());

            levels.setItems(
                   Level.getEntries().stream().map(Enum::name).toList()
            );
            levels.setItemLabelGenerator(l -> new TranslatableText(l).translate());

//            tags.setItems(tribeService.getUserTags());

            // Action buttons
            Button resetBtn = new Button(new TranslatableText("Reset").translate());
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                membershipId.clear();
                name.clear();
                startDate.clear();
                endDate.clear();
                levels.clear();
                tags.clear();
                onSearch.run();
            });
            Button searchBtn = new Button(new TranslatableText("Search").translate());
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(
                    membershipId,
                    name,
                    createDateRangeFilter(),
                    levels,
//                    tags,
                    actions
            );
        }

        @NotNull
        private Component createDateRangeFilter() {
            startDate.setPlaceholder(new TranslatableText("From").translate());

            endDate.setPlaceholder(new TranslatableText("To").translate());

            // For screen readers
            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        @Override
        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!membershipId.isEmpty()) {
                Predicate membershipMatch = criteriaBuilder.like(root.get( "membershipId").as(String.class), "%" + membershipId.getValue() + "%");
                predicates.add(membershipMatch);
            }
            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        lowerCaseFilter + "%");
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(firstNameMatch, lastNameMatch));
            }
            if (startDate.getValue() != null) {
                String databaseColumn = "dateOfBirth";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "dateOfBirth";
                predicates.add(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (!levels.isEmpty()) {
                String databaseColumn = "level";
                List<Predicate> levelPredicates = new ArrayList<>();
                for (String level : levels.getValue()) {
                    levelPredicates
                            .add(criteriaBuilder.equal(criteriaBuilder.literal(level), root.get(databaseColumn)));
                }
                predicates.add(criteriaBuilder.or(levelPredicates.toArray(Predicate[]::new)));
            }
            if (!tags.isEmpty()) {
                String databaseColumn = "tags";
                List<Predicate> predicates2 = new ArrayList<>();
                for (String tags : levels.getValue()) {
                    predicates2
                            .add(criteriaBuilder.like(criteriaBuilder.literal("%" + tags + "%"), root.get(databaseColumn)));
                }
                predicates.add(criteriaBuilder.or(predicates2.toArray(Predicate[]::new)));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Grid<User> createGrid() {
        Grid<User> grid = new Grid<>(User.class, false);
        grid.setSizeFull();
        grid.addColumn(traitRenderer())
                .setHeader(new TranslatableText("Traits").translate())
                .setSortable(false)
                .setAutoWidth(true);
        grid.addColumn(tagRenderer())
                .setHeader(new TranslatableText("Tags").translate())
                .setSortable(false)
                .setAutoWidth(true);
        grid.addColumn("membershipId")
                .setHeader(new TranslatableText("MembershipNumberShort").translate())
                .setAutoWidth(true);
        grid.addColumn("firstName")
                .setHeader(new TranslatableText("FirstName").translate())
                .setAutoWidth(true);
        grid.addColumn("lastName")
                .setHeader(new TranslatableText("LastName").translate())
                .setAutoWidth(true);
        grid.addColumn("email")
                .setHeader(new TranslatableText("Email").translate())
                .setAutoWidth(true);
        grid.addColumn("phone")
                .setHeader(new TranslatableText("Phone").translate())
                .setAutoWidth(true);
        grid.addColumn("address")
                .setHeader(new TranslatableText("Address").translate())
                .setAutoWidth(true);

        grid.addColumn(dateOfBirthRenderer(), "dateOfBirth")
                .setHeader(new TranslatableText("Birthday").translate())
                .setAutoWidth(true);
        grid.addColumn(dateOfJoinRenderer(), "joinDate")
                .setHeader(new TranslatableText("Joining").translate())
                .setAutoWidth(true);
        grid.addColumn(genderRenderer(), "gender")
                .setHeader(new TranslatableText("Gender").translate())
                .setAutoWidth(true);
        grid.addColumn(levelRenderer())
                .setHeader(new TranslatableText("Level").translate())
                .setSortProperty("level")
                .setAutoWidth(true);

        grid.addColumn(nextLevelRenderer())
                .setHeader(new TranslatableText("NextLevel").translate())
                .setAutoWidth(true)
                .setSortable(false);

        grid.setItems(query -> userService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        grid.asSingleSelect().addValueChangeListener(event -> {
            editContact(event.getValue());
        });

        return grid;
    }

    private void editContact(User value) {
        if (value == null) {
            closeEditor();
        } else {
            form.setUser(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    @NotNull
    private static ValueProvider<User, String> genderRenderer() {
        return user -> {
            Gender g = user.getGender();
            if (g == null) {
                g = Gender.UNKNOWN;
            }
            return g.getTitleTranslated();
        };
    }

    @NotNull
    private static ComponentRenderer<Icon, User> levelRenderer() {
        return new ComponentRenderer<>(user -> {
            Level level = user.getLevel();
            Icon icon;
            if (level == null) {
                icon = VaadinIcon.CIRCLE_THIN.create();
                icon.setTooltipText(new TranslatableText("Unknown").translate());
            } else {
                icon = VaadinIcon.CIRCLE.create();
                icon.setTooltipText(level.getTitleTranslated());
                icon.setColor(level.getColorStr());
            }
            return icon;


        });
    }

    @NotNull
    private static ValueProvider<User, String> nextLevelRenderer() {
        return User::getUntilNextLevelString;
    }
    @NotNull
    private static ValueProvider<User, String> dateOfBirthRenderer() {
        DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());


        return user -> {
            LocalDate date = user.getDateOfBirth();
            if (date != null) {
                return date.format(usDateFormatter);
            }

            return new TranslatableText("Unknown").translate();
        };
    }
    @NotNull
    private static ValueProvider<User, String> dateOfJoinRenderer() {
        DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
        return user -> {
            LocalDate ld = user.getJoinDate();
            if (ld != null) {
                return ld.format(usDateFormatter);
            }
            return new TranslatableText("Unknown").translate();
        };
    }
    @NotNull
    private static ComponentRenderer<HorizontalLayout, User> traitRenderer() {
        return new ComponentRenderer<>(user -> {
            HorizontalLayout vl = new HorizontalLayout();

            Icon cameraIcon = VaadinIcon.CAMERA.create();
            if (!user.isPicturesAllowed()) {
                cameraIcon.getElement().getStyle().set("color", "var(--lumo-error-color)");
                cameraIcon.setTooltipText(new TranslatableText("NoPictureAllowance").translate());
            } else {
                cameraIcon.getElement().getStyle().set("color", "var(--lumo-success-color)");
                cameraIcon.setTooltipText(new TranslatableText("PictureAllowance").translate());
            }
            vl.add(cameraIcon);

            Icon memberIcon = VaadinIcon.CLIPBOARD_USER.create();
            if (user.getTribe().getTribeMembership() != null) {
                if (user.hasMembershipContract()) {
                    memberIcon.getElement().getStyle().set("color", "var(--lumo-success-color)");
                    memberIcon.setTooltipText(new TranslatableText("HasSeparateMembership").translate());
                } else {
                    memberIcon.getElement().getStyle().set("color", "var(--lumo-error-color)");
                    memberIcon.setTooltipText(new TranslatableText("NoSeparateMembership").translate());
                }
                vl.add(memberIcon);
            }


            return vl;
        });
    }

    private static HashMap<String, ColorPairGenerator.ColorPair> tagColors = new HashMap<>();
    private static ColorPairGenerator.ColorPair getTagColor(String tag) {
        tagColors.putIfAbsent(tag, ColorPairGenerator.generateColorPair());
        return tagColors.get(tag);
    }

    @NotNull
    private static ComponentRenderer<HorizontalLayout, User> tagRenderer() {
        return new ComponentRenderer<>(user -> {
            HorizontalLayout vl = new HorizontalLayout();
            vl.setClassName(LumoUtility.Padding.XSMALL);
            vl.setSpacing(false);
            vl.setPadding(false);
            vl.setWrap(true);
            vl.setMaxWidth(4, Unit.CM);
            Hibernate.initialize(user.getTags());
            Set<UserTag> tags = user.getTags();
            if (tags == null || tags.isEmpty()) {
                return vl;
            }

            for (UserTag tag : tags) {
                ColorPairGenerator.ColorPair colorPair = new ColorPairGenerator.ColorPair(
                        tag.getColor(), null);
                log.info("tagRenderer: tag={}, background={}, text={} ", tag, colorPair.background, colorPair.text);

                HorizontalLayout div = new HorizontalLayout();
                div.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.LARGE);
                div.getStyle().setMargin("0.5mm");
                div.getStyle().set("background", ColorPairGenerator.toHex(colorPair.background));
                div.getStyle().set("color", ColorPairGenerator.toHex(colorPair.text));

                div.add(new Icon(tag.getIcon()));

                Paragraph text = new Paragraph(tag.getName());
                text.addClassNames(LumoUtility.Margin.NONE);
                text.getStyle().setFontSize("0.7rem");
                text.getStyle().set("color", colorPair.text.toString());
                text.getStyle().setPadding("0.5mm");
                div.add(text);
                vl.add(div);
            }


            return vl;
        });
    }


    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
        form.setAvailableUserTags(tribeService.getUserTags());
    }

    @Transactional
    protected void saveUser(MemberDetailsForm.SaveEvent event) {
        log.info("saveUser: user = " + event.getUser().getUsername());
        userService.update(event.getUser());
        refreshGrid();
        closeEditor();
    }
    private void deleteUser(MemberDetailsForm.DeleteEvent event) {
        log.info("deleteUser: user = " + event.getUser().getUsername());
        userService.delete(event.getUser());
        refreshGrid();
        closeEditor();
    }
}
