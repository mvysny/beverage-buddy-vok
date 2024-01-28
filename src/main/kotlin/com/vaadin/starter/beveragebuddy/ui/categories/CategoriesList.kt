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

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.ModifierKey.*
import com.github.mvysny.kaributools.addShortcut
import com.github.vokorm.buildCondition
import com.github.vokorm.exp
import com.vaadin.flow.component.Key.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.*
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider

/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 */
@Route(value = "categories", layout = MainLayout::class)
@PageTitle("Categories List")
class CategoriesList : KComposite() {

    private lateinit var header: H3
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Category>
    // can't retrieve GridContextMenu from Grid: https://github.com/vaadin/vaadin-grid-flow/issues/523
    lateinit var gridContextMenu: GridContextMenu<Category>

    private val editorDialog = CategoryEditorDialog { updateView() }

    private val dataProvider = Category.dataProvider

    private val root = ui {
        verticalLayout(false) {
            content { align(stretch, top) }
            toolbar = toolbarView("New category") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h3()
            grid = grid(dataProvider) {
                isExpand = true
                columnFor(Category::name) {
                    setSortProperty(Category::name.exp)
                    setHeader("Category")
                }
                addColumn { it.getReviewCount() }.setHeader("Beverages")
                addColumn(ComponentRenderer<Button, Category>({ cat -> createEditButton(cat) })).apply {
                    flexGrow = 0; key = "edit"
                }
                element.themeList.add("row-dividers")

                gridContextMenu = gridContextMenu {
                    item("New", { _ -> editorDialog.createNew() })
                    item("Edit (Alt+E)", { cat -> if (cat != null) edit(cat) })
                    item("Delete", { cat -> if (cat != null) deleteCategory(cat) })
                }
            }

            addShortcut(Alt + KEY_E) {
                val category = grid.asSingleSelect().value
                if (category != null) {
                    edit(category)
                }
            }
        }
    }

    init {
        updateView()
    }

    private fun createEditButton(category: Category): Button =
        Button("Edit").apply {
            icon = Icon(VaadinIcon.EDIT)
            addClassName("category__edit")
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            onLeftClick { edit(category) }
        }

    private fun edit(category: Category) {
        editorDialog.edit(category)
    }

    private fun Category.getReviewCount(): String = Review.getTotalCountForReviewsInCategory(id!!).toString()

    private fun updateView() {
        if (toolbar.searchText.isNotBlank()) {
            dataProvider.filter = buildCondition { Category::name likeIgnoreCase "${toolbar.searchText.trim()}%" }
            header.text = "Search for “${toolbar.searchText}”"
        } else {
            dataProvider.filter = null
            header.text = "Categories"
        }
    }

    private fun deleteCategory(category: Category) {
        category.delete()
        Notification.show("Category successfully deleted.", 3000, Notification.Position.BOTTOM_START)
        updateView()
    }
}
