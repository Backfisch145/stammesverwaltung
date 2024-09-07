package com.vcp.hessen.kurhessen.views.mitglieder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.core.i18n.TranslationHelper;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.features.usermanagement.UserService;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Mitglieder")
@Route(value = "admin/members", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@Slf4j
public class MitgliederView extends Div {

    private Grid<User> grid;

    private Filters filters;
    private final UserService userService;

    public MitgliederView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        addClassNames("mitglieder-view");

        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
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

    public static class Filters extends Div implements Specification<User> {

        private final TextField membershipId = new TextField(new TranslatableText("MembershipNumber").translate());
        private final TextField name = new TextField(new TranslatableText("Name").translate());
        private final TextField phone = new TextField(new TranslatableText("Phone").translate());
        private final DatePicker startDate = new DatePicker(new TranslatableText("Birthday").translate());
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> levels = new MultiSelectComboBox<>(new TranslatableText("Level").translate());

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder(new TranslatableText("FirstOrLastName").translate());

            levels.setItems(
                   Level.getEntries().stream().map(Enum::name).toList()
            );
            levels.setItemLabelGenerator(l -> new TranslatableText(l).translate());

            // Action buttons
            Button resetBtn = new Button(new TranslatableText("Reset").translate());
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                levels.clear();
                membershipId.clear();
                onSearch.run();
            });
            Button searchBtn = new Button(new TranslatableText("Search").translate());
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, phone, createDateRangeFilter(), membershipId, levels, actions);
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
                String databaseColumn = "membership_id";
                Predicate membershipMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get(databaseColumn)), "%" + membershipId.getValue() + "%");
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
            if (!phone.isEmpty()) {
                String databaseColumn = "phone";
                String ignore = "- ()";

                String lowerCaseFilter = ignoreCharacters(ignore, phone.getValue().toLowerCase());
                Predicate phoneMatch = criteriaBuilder.like(
                        ignoreCharacters(ignore, criteriaBuilder, criteriaBuilder.lower(root.get(databaseColumn))),
                        "%" + lowerCaseFilter + "%");
                predicates.add(phoneMatch);

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

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

        private String ignoreCharacters(String characters, String in) {
            String result = in;
            for (int i = 0; i < characters.length(); i++) {
                result = result.replace("" + characters.charAt(i), "");
            }
            return result;
        }

        private Expression<String> ignoreCharacters(String characters, CriteriaBuilder criteriaBuilder,
                Expression<String> inExpression) {
            Expression<String> expression = inExpression;
            for (int i = 0; i < characters.length(); i++) {
                expression = criteriaBuilder.function("replace", String.class, expression,
                        criteriaBuilder.literal(characters.charAt(i)), criteriaBuilder.literal(""));
            }
            return expression;
        }

    }

    private Component createGrid() {
        grid = new Grid<>(User.class, false);
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
        grid.addColumn(dateOfBirthRenderer(), "dateOfBirth")
                .setHeader(new TranslatableText("Birthday").translate())
                .setAutoWidth(true);
        grid.addColumn(genderRenderer(), "gender")
                .setHeader(new TranslatableText("Gender").translate())
                .setAutoWidth(true);
        grid.addColumn(levelRenderer(), "level")
                .setHeader(new TranslatableText("Level").translate())
                .setAutoWidth(true);

        grid.addColumn(nextLevelRenderer(), "nextLevel")
                .setHeader(new TranslatableText("NextLevel").translate())
                .setAutoWidth(true);

        grid.setItems(query -> userService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    @NotNull
    private static ValueProvider<User, String> genderRenderer() {
        return user -> new TranslatableText(user.getGender().name()).translate();
    }
    @NotNull
    private static ValueProvider<User, String> levelRenderer() {
        return user -> new TranslatableText(user.getLevel().name()).translate();
    }
    @NotNull
    private static ValueProvider<User, String> nextLevelRenderer() {
        return User::getUntilNextLevelString;
    }
    @NotNull
    private static ValueProvider<User, String> dateOfBirthRenderer() {
        DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
        return user -> user.getDateOfBirth().format(usDateFormatter);
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

}
