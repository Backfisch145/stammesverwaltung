package com.vcp.hessen.kurhessen.views.components.forms

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vcp.hessen.kurhessen.components.PictureAllowanceCheckBox
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser
import com.vcp.hessen.kurhessen.data.Gender
import com.vcp.hessen.kurhessen.data.User
import com.vcp.hessen.kurhessen.views.components.DatePickerLocalised
import io.github.oshai.kotlinlogging.KotlinLogging


public class UserForm(private val authenticatedUser: AuthenticatedUser) {
    val log = KotlinLogging.logger("UserForm")

    val memberId : IntegerField = membershipIdElement()
    val firstName : TextField = firstNameElement()
    val lastName : TextField = lastNameElement()
    val birthday : DatePicker = birthdayElement()
    val gender : ComboBox<Gender> = genderElement()
    val phone : TextField = phoneElement()
    val email : EmailField = emailElement()
    val intolerances : TextArea = intolerancesElement()
    val eatingHabits : TextArea = eatingHabitsElement()
    val picturesAllowed : PictureAllowanceCheckBox = picturesAllowedElement()
    //val emergencyContact : Component = getEmergencyContactElement()


    fun isValid(): Boolean {
        if (memberId.isInvalid) {
            return false
        }

        if (firstName.isInvalid) {
            return false
        }
        if (lastName.isInvalid) {
            return false
        }
        if (birthday.isInvalid) {
            return false
        }
        if (gender.isInvalid) {
            return false
        }
        if (phone.isInvalid) {
            return false
        }
        if (email.isInvalid) {
            return false
        }
        if (intolerances.isInvalid) {
            return false
        }
        if (eatingHabits.isInvalid) {
            return false
        }

        if (picturesAllowed?.isInvalid != false) {
            return false
        }

        return true
    }

    fun membershipIdElement(): IntegerField {
        val integerField = IntegerField()
        integerField.label = TranslatableText("MembershipNumber").translate()
        integerField.width = "400px"
        integerField.isReadOnly = true
        authenticatedUser.get().ifPresent { u: User ->
            integerField.value = u.membershipId
        }
        return integerField
    }

    fun firstNameElement(): TextField {
        val textField = TextField()
        textField.label = TranslatableText("FirstName").translate()
        textField.width = "400px"
        textField.minLength = 1
        authenticatedUser.get().ifPresent { u: User ->
            textField.value = u.firstName
        }
        textField.isRequired = true
        return textField
    }

    fun lastNameElement(): TextField {
        val textField = TextField()
        textField.label = TranslatableText("LastName").translate()
        textField.width = "400px"
        textField.minLength = 1
        authenticatedUser.get().ifPresent { u: User ->
            textField.value = u.lastName
        }
        textField.isRequired = true
        return textField
    }

    fun phoneElement(): TextField {
        val textField = TextField()
        textField.label = TranslatableText("Phone").translate()
        textField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            textField.value = u.phone
        }
        return textField
    }

    fun emailElement(): EmailField {
        val emailField = EmailField()
        emailField.label = TranslatableText("Email").translate()
        emailField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            emailField.value = u.email?:""
        }
        emailField.isRequired = true
        return emailField
    }

    fun birthdayElement(): DatePicker {
        val datePicker = DatePickerLocalised("Birthday")
        authenticatedUser.get().ifPresent { u: User ->
            datePicker.value = u.dateOfBirth
        }

        return datePicker
    }

    fun genderElement(): ComboBox<Gender> {
        val comboBox = ComboBox<Gender>()
        comboBox.label = TranslatableText("Gender").translate()
        comboBox.width = "min-content"
        comboBox.setItems(Gender.entries)
        comboBox.setItemLabelGenerator { g: Gender -> TranslatableText(g.langKey).translate() }
        authenticatedUser.get().ifPresent { u: User ->
            comboBox.setValue(
                u.gender
            )
        }
        return comboBox
    }

    fun intolerancesElement(): TextArea {
        val textArea = TextArea()
        textArea.label = TranslatableText("Intolerances").translate()
        textArea.placeholder = TranslatableText("IntolerancesPlaceholder").translate()
        textArea.setWidthFull()
        textArea.helperText = "Sachen auf die Ihr Kind allergisch reagiert oder nicht verträgt"
        authenticatedUser.get().ifPresent { u: User ->
            textArea.value = u.intolerances ?: ""
        }
        return textArea
    }

    fun eatingHabitsElement(): TextArea {
        val textArea = TextArea()
        textArea.label = TranslatableText("EatingHabits").translate()
        textArea.placeholder = TranslatableText("EatingHabitsPlaceholder").translate()
        textArea.setWidthFull()
        textArea.helperText = "Sachen die Ihr Kind gerne oder ungerne isst"
        authenticatedUser.get().ifPresent { u: User ->
            textArea.value = u.eatingHabits ?: ""
        }
        return textArea
    }

    fun picturesAllowedElement(): PictureAllowanceCheckBox {
        val pictureAllowance = PictureAllowanceCheckBox()
        authenticatedUser.get().ifPresent { u: User ->
            pictureAllowance.setValue(
                u.isPicturesAllowed
            )
        }
        return pictureAllowance
    }
    fun getEmergencyContactElement(): Component {

        val root = FormLayout()
        root.setWidth("100%")

        val nameField = TextField()
        nameField.label = TranslatableText("Name").translate()
        nameField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            nameField.value = u.userEmergencyContact?.name ?: ""
        }
        root.add(nameField)

        val addressField = TextField()
        addressField.label = TranslatableText("Address").translate()
        addressField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            addressField.value = u.userEmergencyContact?.address ?: ""
        }
        root.add(addressField)

        val phoneField = TextField()
        phoneField.label = TranslatableText("Phone").translate()
        phoneField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            phoneField.value = u.userEmergencyContact?.phone ?: ""
        }
        root.add(phoneField)

        val emailField = EmailField()
        emailField.label = TranslatableText("Email").translate()
        emailField.width = "min-content"
        authenticatedUser.get().ifPresent { u: User ->
            emailField.value = u.userEmergencyContact?.email ?: ""
        }
        root.add(emailField)

        return root
    }
}
