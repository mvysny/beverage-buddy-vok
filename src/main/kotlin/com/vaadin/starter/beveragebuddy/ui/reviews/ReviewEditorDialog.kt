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
package com.vaadin.starter.beveragebuddy.ui.reviews

import eu.vaadinonkotlin.vaadin10.sql2o.dataProvider
import eu.vaadinonkotlin.vaadin10.sql2o.toId
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.AbstractEditorDialog
import java.time.LocalDate

/**
 * A dialog for editing [Review] objects.
 */
class ReviewEditorDialog(saveHandler: (Review, Operation) -> Unit, deleteHandler: (Review) -> Unit)
    : AbstractEditorDialog<Review>("Review", saveHandler, deleteHandler, Review::class.java) {

    private val categoryBox: ComboBox<Category>
    private val scoreBox: ComboBox<String>
    private val lastTasted: DatePicker
    private val beverageName: TextField
    private val timesTasted: TextField

    init {
        formLayout.apply {
            // to propagate the changes made in the fields by the user, we will use binder to bind the field to the Review property.

            beverageName = textField("Beverage name") {
                // no need to have validators here: they are automatically picked up from the bean field.
                bind(binder).trimmingConverter().bind(Review::name)
            }
            timesTasted = textField("Times tasted") {
                pattern = "[0-9]*"
                isPreventInvalidInput = true
                bind(binder).toInt().bind(Review::count)
            }
            categoryBox = comboBox("Choose a category") {
                // we need to show a list of options for the user to choose from. For every option we need to retain at least:
                // 1. the category ID (to bind it to Review::category)
                // 2. the category name (to show it to the user when the combobox's option list is expanded)
                // since the Category class already provides these values, we will simply use that as the data source for the options.
                //
                // now we need to configure the item label generator so that we can extract the name out of Category and display it to the user:
                setItemLabelGenerator { it.name }

                // can't create new Categories here
                isAllowCustomValue = false

                // provide the list of options as a DataProvider, providing instances of Category
                dataProvider = Category.dataProvider

                // bind the combo box to the Review::category field so that changes done by the user are stored.
                bind(binder).toId().bind(Review::category)
            }
            lastTasted = datePicker("Choose the date") {
                max = LocalDate.now()
                min = LocalDate.of(1, 1, 1)
                value = LocalDate.now()
                bind(binder).bind(Review::date)
            }
            scoreBox = comboBox("Mark a score") {
                isAllowCustomValue = false
                setItems("1", "2", "3", "4", "5")
                bind(binder).toInt().bind(Review::score)
            }
        }
    }

    override fun confirmDelete() {
        openConfirmationDialog("""Delete beverage "${currentItem!!.name}"?""")
    }
}
