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
package com.vaadin.starter.beveragebuddy.ui.categories

import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v10.trimmingConverter
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.ConfirmationDialog
import com.vaadin.starter.beveragebuddy.ui.EditorForm
import com.vaadin.starter.beveragebuddy.ui.EditorDialogFrame

/**
 * A form for editing [Category] objects.
 * @property itemDeleter Callback to delete the edited item
 */
class CategoryEditorForm(val category: Category) : EditorForm<Category> {
    private val isEditing get() = category.id != null
    override val itemType: String get() = "Category"
    override val binder: Binder<Category> = BeanValidationBinder(Category::class.java)
    override val component = FormLayout().apply {
        textField("Category Name") {
            bind(binder)
                    .trimmingConverter()
                    .withValidator(StringLengthValidator(
                            "Category name must contain at least 3 printable characters",
                            3, null))
                    .withValidator({ name -> isNameUnique(name) }, "Category name must be unique")
                    .bind(Category::name)
        }
    }

    private fun isNameUnique(name: String?): Boolean {
        if (name == null || name.isBlank()) return true
        if (category.name == name && isEditing) return true
        return !Category.existsWithName(name)
    }
}

/**
 * Opens dialogs for editing [Category] objects.
 * @property itemSaver Callback to save the edited item
 * @property itemDeleter Callback to delete the edited item
 */
class CategoryEditorDialog(private val itemSaver: (Category, EditorForm.Operation) -> Unit,
                           private val itemDeleter: (Category) -> Unit) {
    private fun maybeDelete(frame: EditorDialogFrame<Category>, item: Category) {
        val reviewCount = Review.getTotalCountForReviewsInCategory(item.id!!).toInt()
        if (reviewCount == 0) {
            frame.close()
            itemDeleter(item)
        } else {
            val additionalMessage = "Deleting the category will mark the associated reviews as “undefined”. You may link the reviews to other categories on the edit page."
            ConfirmationDialog().open("Delete Category “${item.name}”?",
                    "There are $reviewCount reviews associated with this category.",
                    additionalMessage, "Delete", true) {
                frame.close()
                itemDeleter(item)
            }
        }
    }

    fun createNew() {
        val category = Category()
        val frame = EditorDialogFrame(CategoryEditorForm(category))
        frame.onSaveItem = itemSaver
        frame.onDeleteItem = { item -> maybeDelete(frame, item) }
        frame.open(Category(), EditorForm.Operation.ADD)
    }

    fun edit(category: Category) {
        val frame = EditorDialogFrame(CategoryEditorForm(category))
        frame.onSaveItem = itemSaver
        frame.onDeleteItem = { item -> maybeDelete(frame, item) }
        frame.open(category, EditorForm.Operation.EDIT)
    }
}
