/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.binder.Binder

import java.io.Serializable

/**
 * Abstract base class for dialogs adding, editing or deleting items.
 *
 * Subclasses are expected to
 *
 *  * add, during construction, the needed UI components to
 * [formLayout] and bind them using [binder], as well
 * as
 *  * override [confirmDelete] to open the confirmation dialog with
 * the desired message (by calling [openConfirmationDialog]).
 * @param T the type of the item to be added, edited or deleted
 * @property itemType The readable name of the item type
 * @property itemSaver Callback to save the edited item
 * @property itemDeleter Callback to delete the edited item
 * @param itemClass the class of item edited by this dialog
 */
abstract class AbstractEditorDialog<T : Serializable> protected constructor(private val itemType: String,
                                                                            private val itemSaver: (T, Operation) -> Unit, private val itemDeleter: (T) -> Unit,
                                                                            itemClass: Class<T>) : Dialog() {

    private val titleField: H2
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button
    private var registrationForSave: Registration? = null

    /**
     * Gets the form layout, where additional components can be added for
     * displaying or editing the item's properties.
     * @return the form layout
     */
    protected lateinit var formLayout: FormLayout

    /**
     * Gets the binder.
     */
    protected val binder = BeanValidationBinder<T>(itemClass)
    /**
     * Gets the item currently being edited.
     * @return the item currently being edited
     */
    protected var currentItem: T? = null
        private set
    protected var currentOperation: Operation? = null
        private set

    private val confirmationDialog = ConfirmationDialog()

    /**
     * The operations supported by this dialog. Delete is enabled when editing
     * an already existing item.
     */
    enum class Operation(val nameInTitle: String, val nameInText: String,
                         val isDeleteEnabled: Boolean) {
        ADD("Add New", "add", false),
        EDIT("Edit", "edit", true)
    }

    init {
        isCloseOnEsc = true
        isCloseOnOutsideClick = false

        titleField = h2()
        div {
            // form layout wrapper
            addClassName("has-padding")
            formLayout = formLayout {
                setResponsiveSteps(
                        FormLayout.ResponsiveStep("0", 1),
                        FormLayout.ResponsiveStep("50em", 2)
                )
                addClassName("no-padding")
            }
        }
        horizontalLayout {
            // button bar
            className = "buttons"
            saveButton = button("Save") {
                isAutofocus = true
                setPrimary()
            }
            cancelButton = button("Cancel") {
                addClickListener { close() }
            }
            deleteButton = button("Delete") {
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                addClickListener { confirmDelete() }
            }
        }
    }

    /**
     * Opens the given item for editing in the dialog.
     *
     * @param item The item to edit; it may be an existing or a newly created instance
     * @param operation The operation being performed on the item
     */
    fun open(item: T, operation: Operation) {
        currentItem = item
        currentOperation = operation
        titleField.text = operation.nameInTitle + " " + itemType
        if (registrationForSave != null) {
            registrationForSave!!.remove()
        }
        registrationForSave = saveButton.addClickListener { saveClicked(operation) }
        binder.readBean(currentItem)

        deleteButton.isEnabled = operation.isDeleteEnabled
        open()
    }

    private fun saveClicked(operation: Operation) {
        if (binder.writeBeanIfValid(currentItem!!)) {
            itemSaver(currentItem!!, operation)
            close()
        } else {
            val status = binder.validate()
            Notification.show(status.validationErrors.joinToString("; ") { it.errorMessage }, 3000, Notification.Position.BOTTOM_START)
        }
    }

    protected abstract fun confirmDelete()

    /**
     * Opens the confirmation dialog before deleting the current item.
     *
     * The dialog will display the given title and message(s), then call
     * [deleteConfirmed] if the Delete button is clicked.
     *
     * @param title The title text
     * @param message Detail message (optional, may be empty)
     * @param additionalMessage Additional message (optional, may be empty)
     */
    protected fun openConfirmationDialog(title: String, message: String = "",
                                         additionalMessage: String = "") {
        confirmationDialog.open(title, message, additionalMessage, "Delete",
                true, { deleteConfirmed(currentItem!!) })
    }

    protected fun deleteConfirmed(item: T) {
        itemDeleter(item)
        close()
    }
}

/**
 * The editor form which edits beans of type [T]. The UI is contained in [component] and all fields are referenced
 * via [binder]. Nest this into the [EditorDialogFrame].
 */
interface EditorForm<T : Serializable> {
    /**
     * The displayable name of the item type [T].
     */
    val itemType: String
    /**
     * All form fields are registered to this binder.
     */
    val binder: Binder<T>
    /**
     * Contains all form UI fields.
     */
    val component: FormLayout

    /**
     * The operations supported by this dialog. Delete is enabled when editing
     * an already existing item.
     */
    enum class Operation(val nameInTitle: String, val nameInText: String,
                         val isDeleteEnabled: Boolean) {
        ADD("Add New", "add", false),
        EDIT("Edit", "edit", true)
    }
}

/**
 * A dialog frame for dialogs adding, editing or deleting items.
 *
 * Users are expected to:
 *  * Set [form] with a proper implementation.
 *  * Set [onSaveItem] and [onDeleteItem]
 *  * Call [open] to show the dialog.
 * @param T the type of the item to be added, edited or deleted
 * @property form the form itself
 */
class EditorDialogFrame<T : Serializable>(private val form: EditorForm<T>) : Dialog() {

    private val titleField: H2
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button
    private var registrationForSave: Registration? = null

    /**
     * The item currently being edited.
     */
    var currentItem: T? = null
        private set
    var currentOperation: EditorForm.Operation? = null
        private set

    /**
     * Callback to save the edited item. The dialog frame is closed automatically.
     */
    lateinit var onSaveItem: (item: T, op: EditorForm.Operation)->Unit

    /**
     * Callback to delete the edited item. Should open confirmation dialog (or delete the item directly if possible).
     * Note that the frame is not closed automatically and must be closed manually.
     */
    lateinit var onDeleteItem: (item: T)->Unit

    init {
        isCloseOnEsc = true
        isCloseOnOutsideClick = false

        titleField = h2()
        div {
            // form layout wrapper
            addClassName("has-padding")

            add(form.component)
            form.component.apply {
                setResponsiveSteps(
                        FormLayout.ResponsiveStep("0", 1),
                        FormLayout.ResponsiveStep("50em", 2)
                )
                addClassName("no-padding")
            }
        }
        horizontalLayout {
            // button bar
            className = "buttons"
            saveButton = button("Save") {
                isAutofocus = true
                setPrimary()
            }
            cancelButton = button("Cancel") {
                addClickListener { close() }
            }
            deleteButton = button("Delete") {
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                addClickListener { onDeleteItem(currentItem!!) }
            }
        }
    }

    /**
     * Opens given item for editing in this dialog.
     *
     * @param item The item to edit; it may be an existing or a newly created instance
     * @param operation The operation being performed on the item
     */
    fun open(item: T, operation: EditorForm.Operation) {
        currentItem = item
        currentOperation = operation
        titleField.text = operation.nameInTitle + " " + form.itemType
        if (registrationForSave != null) {
            registrationForSave!!.remove()
        }
        registrationForSave = saveButton.addClickListener { saveClicked(operation) }
        form.binder.readBean(currentItem)

        deleteButton.isEnabled = operation.isDeleteEnabled
        open()
    }

    private fun saveClicked(operation: EditorForm.Operation) {
        if (form.binder.writeBeanIfValid(currentItem!!)) {
            onSaveItem(currentItem!!, operation)
            close()
        } else {
            val status = form.binder.validate()
            Notification.show(status.validationErrors.joinToString("; ") { it.errorMessage }, 3000, Notification.Position.BOTTOM_START)
        }
    }
}
