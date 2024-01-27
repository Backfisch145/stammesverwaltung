package com.vcp.hessen.kurhessen.views.veranstaltungen;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vcp.hessen.kurhessen.data.Role;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;
import com.vcp.hessen.kurhessen.data.event.Event;
import com.vcp.hessen.kurhessen.data.event.EventParticipant;
import com.vcp.hessen.kurhessen.data.event.EventRepository;
import com.vcp.hessen.kurhessen.data.event.EventRole;
import com.vcp.hessen.kurhessen.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.views.components.forms.EventForm;
import com.vcp.hessen.kurhessen.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@PageTitle("New Event")
@Route(value = "events/:eventID?/:action?", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class AddEventView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final String EVENT_ID = "eventID";
    private final String ACTION_ID = "action";
    public final String EVENT_ROUTE_TEMPLATE = "events/%s/%s";

    private final EventRepository eventRepository;
    private final AuthenticatedUser authenticatedUser;
    private final EventForm form;

    enum Action {
        CREATE,EDIT,SHOW;

        public static Action valueOfIgnoreCase(String value) {
            return Arrays.stream(Action.values()).filter( action ->
                    action.name().equalsIgnoreCase(value)
            ).findFirst().orElseGet(null);
        }
    }

    public AddEventView(EventRepository eventRepository, AuthenticatedUser authenticatedUser, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.authenticatedUser = authenticatedUser;
        form = new EventForm(userRepository, eventRepository);

        createGui();
    }

    public void createGui() {
        getContent().removeAll();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn2.setAlignItems(Alignment.START);

        h3.setText(new TranslatableText("CreateNewEvent").translate());
        h3.setWidth("100%");


        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(form);
        getContent().add(layoutRow2);
    }


    @Override
    public void beforeEnter(@NotNull BeforeEnterEvent beforeEvent) {
        Optional<Integer> eventId = beforeEvent.getRouteParameters().get(EVENT_ID).map(Integer::parseInt);
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        Optional<Event> event = Optional.empty();
        Action action = Action.SHOW;

        if (eventId.isPresent()) {
            event = eventRepository.findById(eventId.get());

            if (event.isPresent()) {
                if (!user.hasRole(Role.ADMIN) && !event.get().isUserParticipant(user)) {
                    throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
                }
            }

            Optional<Action> optionalAction = beforeEvent.getRouteParameters().get(ACTION_ID).map(Action::valueOfIgnoreCase);
            if (optionalAction.isPresent()) {
                if (user.hasRole(Role.ADMIN)) {
                    action = optionalAction.get();
                } else {
                    if (event.isPresent()) {
                        EventParticipant userParticipant = event.get().getEventParticipation(user);
                        if (userParticipant != null && EventRole.ORGANISER == userParticipant.getEventRole()) {
                            action = optionalAction.get();
                        }
                    }
                }
            }

            if (Action.SHOW == action) {
                form.setReadOnly(true);
            }
        }
        form.init(event.orElseGet(Event::new));
    }

}
