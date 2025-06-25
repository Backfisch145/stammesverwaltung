package com.vcp.hessen.kurhessen.features.events;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.features.events.data.Event;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.features.usermanagement.domain.UserService;
import com.vcp.hessen.kurhessen.views.MainLayout;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Slf4j
@Route(value = "events/:eventID", layout = MainLayout.class)
@PreAuthorize("hasAnyAuthority('EVENT_INSERT', 'EVENT_UPDATE')")
@Uses(Icon.class)
public class AddEventView extends Composite<VerticalLayout> implements HasDynamicTitle, BeforeEnterObserver {


    private final String EVENT_ID = "eventID";
    public static final String EVENT_ROUTE_TEMPLATE = "events/%s";

    private final EventService eventService;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private EventForm form;

    @Override
    public String getPageTitle() {
        return new TranslatableText("EventDetailView").translate();
    }



    public AddEventView(AuthenticatedUser authenticatedUser, EventService eventService, UserService userService) {
        this.authenticatedUser = authenticatedUser;
        this.eventService = eventService;
        this.userService = userService;


        log.info("AddEventView.AddEventView");
        form = new EventForm(this.userService, event -> {
            Event e = eventService.update(event);
            form.setEvent(e);
        });

        getContent().removeAll();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn2.setAlignItems(Alignment.START);

        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        getContent().add(layoutColumn2);
        layoutColumn2.add(form);
        getContent().add(layoutRow2);
    }



    @Override
    public void beforeEnter(@NotNull BeforeEnterEvent beforeEvent) {

        Optional<String> eventIdStr = beforeEvent.getRouteParameters().get(EVENT_ID);
        System.out.println("eventIdStr = " + eventIdStr.toString());
        log.info("eventIdStr = " + eventIdStr.toString());

        if (eventIdStr.isEmpty()) {
            return;
        }

        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        if ("new".equals(eventIdStr.get())) {
            if (!user.hasPermission("EVENT_INSERT")) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
            form.setEvent(new Event());
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.get());
        } catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }



        Optional<Event> eventOptional = eventService.getComplete(eventId);
        if (eventOptional.isPresent() && !eventOptional.get().isUserAllowedToSee(user)) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }


        log.info("OptionalEvent is present: " + eventOptional.isPresent());
        form.setEvent(eventOptional.orElseGet(Event::new));
    }

}
