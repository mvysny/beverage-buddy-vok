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
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.ConfirmationDialog
import com.vaadin.starter.beveragebuddy.ui.EditDialog
import com.vaadin.starter.beveragebuddy.ui.EditorDialogFrame

/**
 * A dialog for editing [Category] objects.
 */
class CategoryEditorDialog(private val itemSaver: (Category, EditDialog.Operation)->Unit, private val itemDeleter: (Category)->Unit) {
    private val frame = EditorDialogFrame<Category>()
    private val binder: Binder<Category> = BeanValidationBinder(Category::class.java)

    init {
        frame.formLayout.apply {
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
        frame.dialog = object: EditDialog<Category> {
            override val itemType: String get() = "Category"
            override val binder: Binder<Category> get() = this@CategoryEditorDialog.binder

            override fun saveItem(item: Category, op: EditDialog.Operation) {
                itemSaver(item, op)
                frame.close()
            }

            override fun delete(item: Category) {
                deleteImpl(item)
            }
        }
    }

    private fun isNameUnique(name: String?): Boolean {
        if (name == null || name.isBlank()) return true
        if (frame.currentItem?.name == name && frame.currentOperation == EditDialog.Operation.EDIT) return true
        return !Category.existsWithName(name)
    }

    private fun deleteImpl(item: Category) {
        val reviewCount = Review.getTotalCountForReviewsInCategory(item.id!!).toInt()
        if (reviewCount == 0) {
            itemDeleter(item)
            frame.close()
        } else {
            val additionalMessage = "Deleting the category will mark the associated reviews as “undefined”. You may link the reviews to other categories on the edit page."
            ConfirmationDialog().open("Delete Category “${item.name}”?",
                "There are $reviewCount reviews associated with this category.",
                additionalMessage, "Delete", true) {
                itemDeleter(item)
                frame.close()
            }
        }
    }

    fun createNew() {
        frame.open(Category(), EditDialog.Operation.ADD)
    }

    fun edit(category: Category) {
        frame.open(category, EditDialog.Operation.EDIT)
    }
}
