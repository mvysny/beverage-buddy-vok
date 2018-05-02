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

import com.github.vok.framework.sql2o.vaadin.VokDataProvider
import com.github.vok.framework.sql2o.vaadin.dataProvider
import com.github.vok.framework.sql2o.vaadin.withFilter
import com.github.vok.karibudsl.flow.*
import com.github.vokorm.getById
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcons
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.AbstractEditorDialog
import com.vaadin.starter.beveragebuddy.ui.MainLayout
import com.vaadin.starter.beveragebuddy.ui.Toolbar
import com.vaadin.starter.beveragebuddy.ui.toolbarView

/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 */
@Route(value = "categories", layout = MainLayout::class)
@PageTitle("Categories List")
class CategoriesList : VerticalLayout() {

    private val header: H3
    private val toolbar: Toolbar
    private val grid: Grid<Category>

    private val form = CategoryEditorDialog(
        { category, operation -> saveCategory(category, operation) },
        { deleteCategory(it) })

    init {
        isPadding = false; content { align(stretch, top) }
        toolbar = toolbarView("New category") {
            onSearch = { updateView() }
            onCreate = { form.open(Category(null, ""), AbstractEditorDialog.Operation.ADD) }
        }
        header = h3()
        grid = grid {
            isExpand = true
            addColumnFor(Category::name) {
                setHeader("Category")
            }
            addColumn({ it.getReviewCount() }).setHeader("Beverages")
            addColumn(ComponentRenderer<Button, Category>({ cat -> createEditButton(cat) })).flexGrow = 0
            themes.add("row-dividers")
            asSingleSelect().addValueChangeListener {
                if (it.value != null) {  // deselect fires yet another selection event, this time with null Category.
                    selectionChanged(it.value.id!!)
                    selectionModel.deselect(it.value)
                }
            }
        }

        updateView()
    }

    private fun createEditButton(category: Category): Button =
        Button("Edit").apply {
            icon = Icon(VaadinIcons.EDIT)
            addClassName("review__edit")
            themes.add("tertiary")
            addClickListener { _ -> form.open(category, AbstractEditorDialog.Operation.EDIT) }
        }

    private fun selectionChanged(categoryId: Long) {
        form.open(Category.getById(categoryId), AbstractEditorDialog.Operation.EDIT)
    }

    private fun Category.getReviewCount(): String = Review.getTotalCountForReviewsInCategory(id!!).toString()

    private fun updateView() {
        var dp: VokDataProvider<Category> = Category.dataProvider
        if (!toolbar.searchText.isBlank()) {
            dp = dp.withFilter { Category::name ilike "%${toolbar.searchText}%" }
            header.text = "Search for “${toolbar.searchText}”"
        } else {
            header.text = "Categories"
        }
        grid.dataProvider = dp
    }

    private fun saveCategory(category: Category, operation: AbstractEditorDialog.Operation) {
        category.save()
        Notification.show("Category successfully ${operation.nameInText}ed.", 3000, Notification.Position.BOTTOM_START)
        updateView()
    }

    private fun deleteCategory(category: Category) {
        category.delete()
        Notification.show("Category successfully deleted.", 3000, Notification.Position.BOTTOM_START)
        updateView()
    }
}
