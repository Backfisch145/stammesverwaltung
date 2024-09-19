package com.vcp.hessen.kurhessen.views.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.i18n.TranslationHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

@Slf4j
public class DatePickerLocalised extends DatePicker {
    private String title;

    public DatePickerLocalised(String title) {
        this.title = title;
        this.setLabel(this.title);
        this.setLocale(TranslationHelper.Companion.getCurrentLocale());

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
        this.setI18n(localisedPicker);

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.setLabel(new TranslatableText(this.title).translate());
    }
}
