package com.vcp.hessen.kurhessen.views.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vcp.hessen.kurhessen.i18n.TranslatableText;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DateTimePickerLocalised extends DateTimePicker {

    public DateTimePickerLocalised() {
        this.setLocale(TranslatableText.Companion.getCurrentLocale());

        DatePicker.DatePickerI18n localisedPicker = new DatePicker.DatePickerI18n();
        localisedPicker.setMonthNames(List.of(
                new TranslatableText("January").translate(),
                new TranslatableText("February").translate(),
                new TranslatableText("March").translate(),
                new TranslatableText("April").translate(),
                new TranslatableText("May").translate(),
                new TranslatableText("June").translate(),
                new TranslatableText("July").translate(),
                new TranslatableText("August").translate(),
                new TranslatableText("September").translate(),
                new TranslatableText("October").translate(),
                new TranslatableText("November").translate(),
                new TranslatableText("December").translate()
                )
        );
        localisedPicker.setWeekdays(List.of(
                new TranslatableText("Sunday").translate(),
            new TranslatableText("Monday").translate(),
            new TranslatableText("Tuesday").translate(),
            new TranslatableText("Wednesday").translate(),
            new TranslatableText("Thursday").translate(),
            new TranslatableText("Friday").translate(),
            new TranslatableText("Saturday").translate()
        ));
        localisedPicker.setWeekdaysShort(
                List.of(
                        new TranslatableText("SundayShort").translate(),
                        new TranslatableText("MondayShort").translate(),
                        new TranslatableText("TuesdayShort").translate(),
                        new TranslatableText("WednesdayShort").translate(),
                        new TranslatableText("ThursdayShort").translate(),
                        new TranslatableText("FridayShort").translate(),
                        new TranslatableText("SaturdayShort").translate()
                ));
        localisedPicker.setToday(new TranslatableText("Today").translate());
        localisedPicker.setCancel(new TranslatableText("Cancel").translate());
        localisedPicker.setFirstDayOfWeek(1);
        this.setDatePickerI18n(localisedPicker);

    }


}
