package com.vcp.hessen.kurhessen.views.components.forms;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;
import com.vcp.hessen.kurhessen.data.event.*;
import com.vcp.hessen.kurhessen.i18n.TranslatableText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.vcp.hessen.kurhessen.views.components.DatePickerLocalised;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transaction;
import kotlin.jvm.internal.Intrinsics;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@RolesAllowed("MODERATOR")
public final class EventForm extends FormLayout {

    /**
     * Do not edit root element, only used to reset on Cancel
     * */
    private Event rootEvent = null;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    @NotNull
    private final IntegerField eventId;
    @NotNull
    private final TextField name;
    @NotNull
    private final TextField address;
    @NotNull
    private final DatePicker startingTime;
    @NotNull
    private final DatePicker endingTime;
    @NotNull
    private final DatePicker participationDeadline;
    private final DatePicker paymentDeadline;
    @NotNull
    private final NumberField price;
    @NotNull
    private final MultiSelectComboBox<User> organisers;
    @NotNull
    private final MultiSelectComboBox<User> participants;
    @NotNull
    private final Button saveButton;
    @NotNull
    private final Button editButton;
    @NotNull
    private final Button cancelButton;

    private final H3 titel = new H3();

    public enum Mode {
        CREATE,EDIT,SHOW;
    }


    public EventForm(UserRepository userRepository, EventRepository eventRepository, EventParticipantRepository eventParticipantRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventParticipantRepository = eventParticipantRepository;

        eventId = this.idElement();
        name = this.nameElement();
        startingTime = this.startingTimeElement();
        endingTime = this.endingTimeElement();
        address = this.addressElement();
        participationDeadline = this.participationDeadlineElement();
        paymentDeadline = this.paymentDeadlineElement();
        price = this.priceElement();
        organisers = this.organisersElement();
        participants = this.participantsElement();
        saveButton = this.saveButton();
        editButton = this.editButton();
        cancelButton = this.cancelButton();

        startingTime.addValueChangeListener(e -> endingTime.setMin(e.getValue()));

        titel.setText(new TranslatableText("CreateNewEvent").translate());
        titel.setWidth("100%");

        this.setWidthFull();
        this.add(this.titel, 2);
        this.add(this.eventId);
        this.add(this.name);
        this.add(this.startingTime);
        this.add(this.endingTime);
        this.add(this.address);
        this.add(this.participationDeadline);
        this.add(new Hr(), 2);
        this.add(this.paymentDeadline);
        this.add(this.price);
        this.add(new Hr(), 2);
        this.add(this.organisers);
        this.add(this.participants);
        this.add(this.saveButton);
        this.add(this.editButton);
        this.add(this.cancelButton);
    }
    public void init(@NotNull Event event) {
        Intrinsics.checkNotNullParameter(event, "event");

        rootEvent = event;

        this.eventId.setValue(event.getId());
        if (event.getName() != null) {
            this.name.setValue(event.getName());
        }
        if (event.getStartingTime() != null) {
            this.startingTime.setValue(event.getStartingTime().toLocalDate());
        }
        if (event.getEndingTime() != null) {
            this.endingTime.setValue(event.getEndingTime().toLocalDate());
        }
        if (event.getPrice() != null) {
            this.price.setValue(event.getPrice());
        }
        if (event.getParticipationDeadline() != null) {
            this.participationDeadline.setValue(event.getParticipationDeadline().toLocalDate());
        }
        if (event.getPaymentDeadline() != null) {
            this.paymentDeadline.setValue(event.getPaymentDeadline().toLocalDate());
        }


        List<User> currentParticipants = new ArrayList<>();
        List<User> currentOrganisators = new ArrayList<>();

        event.getParticipants().forEach(participant -> {
            if (participant.getUser() != null) {
                if (participant.getEventRole() == EventRole.ORGANISER) {
                    currentOrganisators.add(participant.getUser());
                } else {
                    currentParticipants.add(participant.getUser());
                }
            }
        });

        this.organisers.select(currentOrganisators);
        this.participants.select(currentParticipants);

        setMode(Mode.SHOW);
    }
    @NotNull
    public Event toEvent() {
        Event event = new Event();
        this.applyForm(event);
        return event;
    }

    public void applyForm(@NotNull Event event) {
        Intrinsics.checkNotNullParameter(event, "event");
        event.setId(this.eventId.getValue());
        event.setName(this.name.getValue());
        event.setVersion(this.rootEvent.getVersion());

        if (this.startingTime.getValue() != null) {
            event.setStartingTime(LocalDateTime.of(this.startingTime.getValue(), LocalTime.MIN));
        }
        if (this.endingTime.getValue() != null) {
            event.setEndingTime(LocalDateTime.of(this.endingTime.getValue(), LocalTime.MAX));
        }
        if (this.participationDeadline.getValue() != null) {
            event.setParticipationDeadline(LocalDateTime.of(this.participationDeadline.getValue(), LocalTime.MIN));
        }
        if (this.paymentDeadline.getValue() != null) {
            event.setPaymentDeadline(LocalDateTime.of(this.paymentDeadline.getValue(), LocalTime.MIN));
        }

        event.setPrice(this.price.getValue());
        event.setAddress(this.address.getValue());


        Set<User> updatedParticipants = new HashSet<>();
        for (EventParticipant existingParticipant : event.getParticipants()) {
            if (this.participants.getValue().contains(existingParticipant.getUser())) {
                existingParticipant.setEventRole(EventRole.PARTICIPANT);
                updatedParticipants.add(existingParticipant.getUser());
            }
            if (this.organisers.getValue().contains(existingParticipant.getUser())) {
                existingParticipant.setEventRole(EventRole.ORGANISER);
                updatedParticipants.add(existingParticipant.getUser());
            }
        }

        this.participants.getValue().stream()
                .filter(u -> !updatedParticipants.contains(u))
                .forEach(u ->
                        event.getParticipants().add(
                                new EventParticipant(
                                        EventParticipationStatus.INVITED,
                                        EventRole.PARTICIPANT,
                                        u,
                                        event
                                )
                        )
                );

        this.organisers.getValue().stream()
                .filter(u -> !updatedParticipants.contains(u))
                .forEach(u ->
                        event.getParticipants().add(
                                new EventParticipant(
                                        EventParticipationStatus.INVITED,
                                        EventRole.ORGANISER,
                                        u,
                                        event
                                )
                        )
                );

    }

    public boolean isValid() {
        if (this.eventId.isInvalid()) {
            return false;
        } else if (this.name.isInvalid()) {
            return false;
        } else if (this.address.isInvalid()) {
            return false;
        } else if (this.startingTime.isInvalid()) {
            return false;
        } else if (this.endingTime.isInvalid()) {
            return false;
        } else if (this.participationDeadline.isInvalid()) {
            return false;
        } else if (this.paymentDeadline.isInvalid()) {
            return false;
        } else if (this.price.isInvalid()) {
            return false;
        } else if (this.organisers.isInvalid()) {
            return false;
        } else {
            return !this.participants.isInvalid();
        }
    }

    public void setMode(@NotNull Mode mode) {

        if (mode == Mode.SHOW) {
            titel.setText(new TranslatableText("Event").translate());
            eventId.setReadOnly(true);
            name.setReadOnly(true);
            startingTime.setReadOnly(true);
            endingTime.setReadOnly(true);
            address.setReadOnly(true);
            participationDeadline.setReadOnly(true);
            paymentDeadline.setReadOnly(true);
            price.setReadOnly(true);
            organisers.setReadOnly(true);
            participants.setReadOnly(true);

            startingTime.setMin(startingTime.getValue());
            endingTime.setMin(endingTime.getValue());

            saveButton.setVisible(false);
            editButton.setVisible(true);
            cancelButton.setVisible(false);
            return;
        }

        if (mode == Mode.CREATE || mode == Mode.EDIT) {

            if (mode == Mode.EDIT) {
                titel.setText(new TranslatableText("EditEvent").translate());
            } else {
                titel.setText(new TranslatableText("CreateNewEvent").translate());
            }

            eventId.setReadOnly(true);
            name.setReadOnly(false);
            startingTime.setReadOnly(false);
            endingTime.setReadOnly(false);
            address.setReadOnly(false);
            participationDeadline.setReadOnly(false);
            paymentDeadline.setReadOnly(false);
            price.setReadOnly(false);
            organisers.setReadOnly(false);
            participants.setReadOnly(false);

            startingTime.setMin(LocalDate.now());
            endingTime.setMin(LocalDate.now());

            saveButton.setVisible(true);
            editButton.setVisible(false);
            cancelButton.setVisible(true);
        }


    }

    @NotNull
    public IntegerField getEventId() {
        return this.eventId;
    }

    @NotNull
    public TextField getName() {
        return this.name;
    }

    @NotNull
    public DatePicker getStartingTime() {
        return this.startingTime;
    }

    @NotNull
    public DatePicker getEndingTime() {
        return this.endingTime;
    }

    @NotNull
    public DatePicker getParticipationDeadline() {
        return this.participationDeadline;
    }

    @NotNull
    public NumberField getPrice() {
        return this.price;
    }

    @NotNull
    public MultiSelectComboBox<User> getOrganisers() {
        return this.organisers;
    }

    @NotNull
    public MultiSelectComboBox<User> getParticipants() {
        return this.participants;
    }


    private IntegerField idElement() {
        IntegerField integerField = new IntegerField();
        integerField.setLabel((new TranslatableText("EventId")).translate());
        integerField.setWidth("400px");
        integerField.setReadOnly(true);
        return integerField;
    }

    private TextField nameElement() {
        TextField textField = new TextField();
        textField.setLabel((new TranslatableText("Name")).translate());
        textField.setWidth("400px");
        textField.setMinLength(1);
        textField.setRequired(true);
        return textField;
    }
    private TextField addressElement() {
        TextField textField = new TextField();
        textField.setLabel((new TranslatableText("Address")).translate());
        textField.setWidth("400px");
        textField.setMinLength(1);
        return textField;
    }

    private DatePicker startingTimeElement() {
        DatePickerLocalised datePicker = new DatePickerLocalised();
        datePicker.setLabel((new TranslatableText("From")).translate());
        datePicker.setMin(LocalDate.now());
        datePicker.setLocale(TranslatableText.Companion.getCurrentLocale());
        return datePicker;
    }

    private DatePicker endingTimeElement() {
        DatePickerLocalised datePicker = new DatePickerLocalised();
        datePicker.setLabel((new TranslatableText("To")).translate());
        datePicker.setMin(LocalDate.now());
        datePicker.setLocale(TranslatableText.Companion.getCurrentLocale());
        return datePicker;
    }

    private DatePicker participationDeadlineElement() {
        DatePickerLocalised datePicker = new DatePickerLocalised();
        datePicker.setLabel((new TranslatableText("DeadlineParticipation")).translate());
        datePicker.setMin(LocalDate.now());
        datePicker.setLocale(TranslatableText.Companion.getCurrentLocale());
        return datePicker;
    }

    private DatePicker paymentDeadlineElement() {
        DatePickerLocalised datePicker = new DatePickerLocalised();
        datePicker.setLabel((new TranslatableText("PaymentUntil")).translate());
        datePicker.setMin(LocalDate.now());
        datePicker.setLocale(TranslatableText.Companion.getCurrentLocale());
        return datePicker;
    }

    private NumberField priceElement() {
        NumberField numberField = new NumberField();
        numberField.setLabel((new TranslatableText("Price")).translate());
        numberField.setWidth("min-content");
        numberField.setMin(0.0);
        return numberField;
    }

    private MultiSelectComboBox<User> organisersElement() {
        MultiSelectComboBox<User> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel((new TranslatableText("Organisers")).translate());

        comboBox.setItems(userRepository.findAll());
        comboBox.setItemLabelGenerator( u -> u.getFirstName() + " " + u.getLastName());
        comboBox.setWidthFull();
        return comboBox;
    }

    private MultiSelectComboBox<User> participantsElement() {
        MultiSelectComboBox<User> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel((new TranslatableText("Participants")).translate());
        comboBox.setItems(userRepository.findAll());
        comboBox.setItemLabelGenerator( u -> u.getFirstName() + " " + u.getLastName());
        comboBox.setWidthFull();
        return comboBox;
    }

    @NotNull
    private Button saveButton() {
        Button buttonPrimary = new Button();
        buttonPrimary.setText("Save");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonPrimary.setDisableOnClick(true);
        buttonPrimary.addClickListener(event -> {

            try {
                Event e = this.toEvent();
                log.info("event to be saved: " + e);



//                Set<EventParticipant> formParticipants = new HashSet<>(e.getParticipants());

//                e.getParticipants().clear();
//                e = eventRepository.save(e);
//                e.getParticipants().addAll(formParticipants);

                this.init(eventRepository.save(e));

                Notification notification = Notification.show(new TranslatableText("EventSaved").translate());
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                setMode(Mode.SHOW);
            } catch (Exception e){
                log.error("could not save Event", e);
                Notification notification =Notification.show(new TranslatableText("ErrorWhileSavingEvent").translate());
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } finally {
                buttonPrimary.setEnabled(true);
            }

        });

        return buttonPrimary;
    }

    @NotNull
    private Button editButton() {
        Button editBtn = new Button();
        editBtn.setText(new TranslatableText("Edit").translate());
        editBtn.setWidth("min-content");
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        editBtn.setDisableOnClick(true);
        editBtn.addClickListener(event -> {
            setMode(Mode.EDIT);
            editBtn.setEnabled(true);
        });

        return editBtn;
    }
    @NotNull
    private Button cancelButton() {
        Button cancelBtn = new Button();
        cancelBtn.setText(new TranslatableText("Cancel").translate());
        cancelBtn.setWidth("min-content");
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancelBtn.setDisableOnClick(true);
        cancelBtn.addClickListener(event -> {
            init(rootEvent);
            cancelBtn.setEnabled(true);
        });

        return cancelBtn;
    }

    @NotNull
    @Override
    public String toString() {
        return "EventForm{" +
                "eventId=" + eventId.getValue() +
                ", name=" + name.getValue() +
                ", startingTime=" + startingTime.getValue() +
                ", endingTime=" + endingTime.getValue() +
                ", participationDeadline=" + participationDeadline.getValue() +
                ", price=" + price.getValue() +
                ", organisers=" + organisers.getValue().size() +
                ", participants=" + participants.getValue().size() +
                '}';
    }
}
