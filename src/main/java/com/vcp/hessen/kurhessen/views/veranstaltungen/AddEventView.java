package com.vcp.hessen.kurhessen.views.veranstaltungen;

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
import com.vcp.hessen.kurhessen.data.Role;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;
import com.vcp.hessen.kurhessen.data.event.Event;
import com.vcp.hessen.kurhessen.data.event.EventParticipantRepository;
import com.vcp.hessen.kurhessen.data.event.EventRepository;
import com.vcp.hessen.kurhessen.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.views.MainLayout;
import com.vcp.hessen.kurhessen.views.components.forms.EventForm;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Slf4j
@Route(value = "events/:eventID", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class AddEventView extends Composite<VerticalLayout> implements HasDynamicTitle, BeforeEnterObserver {
    private final String EVENT_ID = "eventID";
    public static final String EVENT_ROUTE_TEMPLATE = "events/%s";

    private final EventRepository eventRepository;
    private final AuthenticatedUser authenticatedUser;
    private final EventForm form;

    @Override
    public String getPageTitle() {
        return new TranslatableText("EventDetailView").translate();
    }



    public AddEventView(AuthenticatedUser authenticatedUser, EventRepository eventRepository, UserRepository userRepository, EventParticipantRepository eventParticipantRepository) {
        this.authenticatedUser = authenticatedUser;
        this.eventRepository = eventRepository;
        form = new EventForm(userRepository, eventRepository, eventParticipantRepository);

        createGui();
    }

    public void createGui() {
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

        if (eventIdStr.isEmpty()) {
            return;
        }

        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        if ("new".equals(eventIdStr.get())) {
            if (!user.hasRole(Role.MODERATOR) && !user.hasRole(Role.ADMIN)) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
            form.init(new Event());
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.get());
        } catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }


        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent() && !event.get().isUserAllowedToSee(user)) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

        log.info("OptionalEvent is present: " + event.isPresent());
        form.init(event.orElseGet(Event::new));
    }

}
