package com.vcp.hessen.kurhessen.features.usermanagement.compoenents

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vcp.hessen.kurhessen.core.components.DatePickerLocalised
import com.vcp.hessen.kurhessen.core.components.PictureAllowanceCheckBox
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText
import com.vcp.hessen.kurhessen.core.util.Callback
import com.vcp.hessen.kurhessen.data.*
import io.github.oshai.kotlinlogging.KotlinLogging


class MyselfForm(
    private val tribeService: TribeService,
    private val onSave: Callback<User>
) : FormLayout() {
    val log = KotlinLogging.logger("UserForm")

    private val binder = BeanValidationBinder(User::class.java)
    val membershipId: IntegerField = membershipIdElement()
    val tribe: ComboBox<Tribe> = tribeElement()
    val firstName: TextField = TextField().apply {
        label = TranslatableText("FirstName").translate()
//        width = "400px"
//        minLength = 1
        isRequired = true
    }
    val lastName: TextField = TextField().apply {
        label = TranslatableText("LastName").translate()
//        width = "400px"
//        minLength = 1
        isRequired = true
    }
    val dateOfBirth: DatePicker = birthdayElement()
    val gender: ComboBox<Gender> = genderElement()
    val diet: ComboBox<Diet> = dietElement()
    val phone: TextField = phoneElement()
    val email: EmailField = emailElement()
    val address: TextField = addressElement()
    val picturesAllowed: PictureAllowanceCheckBox = picturesAllowedElement()
    val intolerances: TextArea = intolerancesElement()
    val eatingHabits: TextArea = eatingHabitsElement()
    val emergencyContact: EmergencyContactFormComponent = EmergencyContactFormComponent()

    val saveButton: Button = saveButton()

    init {
        this.add(H2(TranslatableText("GeneralInformations").translate()), 2)
        this.add(membershipId)
        this.add(tribe)
        this.add(firstName)
        this.add(lastName)
        this.add(dateOfBirth)
        this.add(gender)
        this.add(phone)
        this.add(email)
        this.add(address, 2)
        this.add(Hr(), 2)
        this.add(H2(TranslatableText("Diet").translate()), 2)
        this.add(diet, 2)
        this.add(intolerances, 1)
        this.add(eatingHabits, 1)
        this.add(Hr(), 2)
        this.add(picturesAllowed, 2)
        this.add(Hr(), 2)
        this.add(H2(TranslatableText("EmergencyContact").translate()), 2)
        this.add(emergencyContact, 2)
        this.add(Hr(), 2)

        val buttonLayout = HorizontalLayout().apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
            add(saveButton)
        }
        binder.bindInstanceFields(this)
        this.add(buttonLayout, 2)
    }


    fun setUser(value: User) {
        binder.bean = value
    }

    fun isValid(): Boolean {
        if (membershipId.isInvalid) {
            return false
        }

        if (firstName.isInvalid) {
            return false
        }
        if (lastName.isInvalid) {
            return false
        }
        if (dateOfBirth.isInvalid) {
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

        if (picturesAllowed.isInvalid != false) {
            return false
        }

        return true
    }

    fun membershipIdElement(): IntegerField {
        val integerField = IntegerField()
        integerField.label = TranslatableText("MembershipNumber").translate()
        integerField.width = "400px"
        integerField.isReadOnly = true

        return integerField
    }

    fun tribeElement(): ComboBox<Tribe> {
        val field = ComboBox<Tribe>()
        field.label = TranslatableText("Tribe").translate()
        field.width = "400px"
        field.isReadOnly = true

        field.setItems(tribeService.findAll())
        field.setItemLabelGenerator(Tribe::getName)

        return field
    }

    fun phoneElement(): TextField {
        val textField = TextField()
        textField.label = TranslatableText("Phone").translate()
        textField.width = "min-content"
        return textField
    }

    fun emailElement(): EmailField {
        val emailField = EmailField()
        emailField.label = TranslatableText("Email").translate()
        emailField.width = "min-content"
        emailField.isRequired = true
        return emailField
    }

    fun addressElement(): TextField {
        val textField = TextField()
        textField.label = TranslatableText("Address").translate()
        textField.width = "min-content"
        return textField
    }

    fun birthdayElement(): DatePicker {
        val datePicker =
            DatePickerLocalised(TranslatableText("Birthday").translate())
        return datePicker
    }

    fun genderElement(): ComboBox<Gender> {
        val comboBox = ComboBox<Gender>()
        comboBox.label = TranslatableText("Gender").translate()
        comboBox.width = "min-content"
        comboBox.setItems(Gender.entries)
        comboBox.setItemLabelGenerator { g: Gender -> TranslatableText(g.langKey).translate() }
        return comboBox
    }

    fun dietElement(): ComboBox<Diet> {
        val comboBox = ComboBox<Diet>()
        comboBox.label = TranslatableText("TypeOfDiet").translate()
        comboBox.width = "min-content"
        comboBox.setItems(Diet.entries)
        comboBox.setItemLabelGenerator { d: Diet -> TranslatableText(d.langKey).translate() }
        return comboBox
    }

    fun intolerancesElement(): TextArea {
        val textArea = TextArea()
        textArea.label = TranslatableText("Intolerances").translate()
        textArea.placeholder = TranslatableText("IntolerancesPlaceholder").translate()
        textArea.setWidthFull()
        textArea.helperText = TranslatableText("IntolerancesHelperText").translate()
        return textArea
    }

    fun eatingHabitsElement(): TextArea {
        val textArea = TextArea()
        textArea.label = TranslatableText("EatingHabits").translate()
        textArea.placeholder = TranslatableText("EatingHabitsPlaceholder").translate()
        textArea.setWidthFull()
        textArea.helperText = TranslatableText("EatingHabitsHelperText").translate()


        return textArea
    }

    fun picturesAllowedElement(): PictureAllowanceCheckBox {
        val pictureAllowance = PictureAllowanceCheckBox()





        return pictureAllowance
    }

    fun getEmergencyContactElement(): Component {
        val root = FormLayout()
        root.width = "100%"

        val nameField = TextField()
        nameField.label = TranslatableText("Name").translate()
        nameField.width = "min-content"


        root.add(nameField)

        val addressField = TextField()
        addressField.label = TranslatableText("Address").translate()
        addressField.width = "min-content"


        root.add(addressField)

        val phoneField = TextField()
        phoneField.label = TranslatableText("Phone").translate()
        phoneField.width = "min-content"



        root.add(phoneField)

        val emailField = EmailField()
        emailField.label = TranslatableText("Email").translate()
        emailField.width = "min-content"

        root.add(emailField)

        return root
    }

    private fun validateAndSave() {
        if (binder.isValid) {
            onSave.call(binder.getBean())
        } else {
            Notification
                .show(TranslatableText("FormFieldsContainErrors").translate())
                .addThemeVariants(NotificationVariant.LUMO_ERROR)
        }
    }

    private fun saveButton(): Button {
        val buttonPrimary = Button()
        buttonPrimary.text = "Save"
        buttonPrimary.width = "min-content"
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        buttonPrimary.isDisableOnClick = true
        buttonPrimary.addClickListener { it ->


            validateAndSave()
        }

        return buttonPrimary
    }

}
