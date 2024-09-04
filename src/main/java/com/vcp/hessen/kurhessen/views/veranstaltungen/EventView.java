package com.vcp.hessen.kurhessen.views.veranstaltungen;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
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
import com.vcp.hessen.kurhessen.features.events.data.Event;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.features.events.EventService;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Veranstaltungen")
@Route(value = "events", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class EventView extends Div {

    private Grid<Event> grid;

    private Filters filters;
    private final EventService eventService;

    private Event selectedEvent = null;

    public EventView(EventService eventService) {
        this.eventService = eventService;
        setSizeFull();
        addClassNames("veranstaltungen-view");

        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
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

    public static class Filters extends Div implements Specification<Event> {
        private final TextField name = new TextField(new TranslatableText("Name").translate());
        private final TextField address = new TextField(new TranslatableText("Address").translate());
        private final DatePicker startDate = new DatePicker(new TranslatableText("From").translate());
        private final DatePicker endDate = new DatePicker();

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            // Action buttons
            Button resetBtn = new Button(new TranslatableText("Reset").translate());
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                address.clear();
                startDate.clear();
                endDate.clear();
                onSearch.run();
            });
            Button searchBtn = new Button(new TranslatableText("Search").translate());
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, address, createDateRangeFilter(), actions);
        }

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
        public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(firstNameMatch);
            }
            if (!address.isEmpty()) {
                String databaseColumn = "address";

                Predicate addressMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(databaseColumn)),
                        "%" + address.getValue().toLowerCase() + "%"
                );
                predicates.add(addressMatch);

            }
            if (startDate.getValue() != null) {
                String databaseColumn = "startingTime";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "endingTime";
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(endDate.getValue())));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(Event.class, false);
        grid.addColumn("name")
                .setHeader(new TranslatableText("Name").translate())
                .setAutoWidth(true);
        grid.addColumn("address")
                .setHeader(new TranslatableText("Address").translate())
                .setAutoWidth(true);
        grid.addColumn(startingTimeRenderer(), "startingTime")
                .setHeader(new TranslatableText("From").translate())
                .setAutoWidth(true);
        grid.addColumn(endingTimeRenderer(), "endingTime")
                .setHeader(new TranslatableText("To").translate())
                .setAutoWidth(true);
        grid.addColumn(participationDeadlineRenderer(), "participationDeadline")
                .setHeader(new TranslatableText("DeadlineParticipation").translate())
                .setAutoWidth(true);
        grid.addColumn(paymentDeadlineRenderer(), "paymentDeadline")
                .setHeader(new TranslatableText("DeadlinePayment").translate())
                .setAutoWidth(true);
        grid.addColumn("participantCount")
                .setHeader(new TranslatableText("ParticipantCount").translate())
                .setAutoWidth(true);
        grid.addColumn(priceRenderer(), "price")
                .setHeader(new TranslatableText("Price").translate())
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);

        grid.setItems(query -> eventService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());

        grid.addItemClickListener((ComponentEventListener<ItemClickEvent<Event>>) eventItemClickEvent -> {
            Event clickedEvent = eventItemClickEvent.getItem();

            if (selectedEvent == clickedEvent) {
                getUI().ifPresent(ui -> ui.navigate(String.format(AddEventView.EVENT_ROUTE_TEMPLATE, clickedEvent.getId())));
            } else {
                selectedEvent = clickedEvent;
            }


        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    @NotNull
    private static ValueProvider<Event, String> startingTimeRenderer() {
        return event -> {
            LocalDateTime ldt = event.getStartingTime();
            if (ldt != null) {
                DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
                return ldt.format(usDateFormatter);
            } else {
                return "";
            }
        };
    }
    @NotNull
    private static ValueProvider<Event, String> endingTimeRenderer() {
        return event -> {
            LocalDateTime ldt = event.getEndingTime();
            if (ldt != null) {
                DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
                return ldt.format(usDateFormatter);
            } else {
                return "";
            }
        };
    }
    @NotNull
    @Contract(pure = true)
    private static ValueProvider<Event, String> participationDeadlineRenderer() {
        return event -> {
            LocalDateTime ldt = event.getParticipationDeadline();
            if (ldt != null) {
                DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
                return ldt.format(usDateFormatter);
            } else {
                return "";
            }
        };
    }
    @NotNull
    @Contract(pure = true)
    private static ValueProvider<Event, String> paymentDeadlineRenderer() {
        return event -> {
            LocalDateTime ldt = event.getPaymentDeadline();
            if (ldt != null) {
                DateTimeFormatter usDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(TranslationHelper.Companion.getCurrentLocale());
                return ldt.format(usDateFormatter);
            } else {
                return "";
            }
        };
    }
    @NotNull
    @Contract(pure = true)
    private static ValueProvider<Event, String> priceRenderer() {
        return event -> event.getPrice() + " €";
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

}
