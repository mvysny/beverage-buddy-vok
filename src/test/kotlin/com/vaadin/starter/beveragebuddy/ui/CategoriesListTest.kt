package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.ui.categories.CategoriesList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.expect

/**
 * Tests the UI. Uses the Browserless Testing approach as provided by the [Karibu Testing](https://github.com/mvysny/karibu-testing) library.
 */
class CategoriesListTest : AbstractAppTest() {
    @BeforeEach fun navigate() {
        // navigate to the "Categories" list route.
        navigateTo<CategoriesList>()
    }

    @Test fun `grid lists all categories`() {
        // prepare testing data
        Category(name = "Beers").save()

        // now the "Categories" list should be displayed. Look up the Grid and assert on its contents.
        val grid = _get<Grid<Category>>()
        grid.expectRows(1)
        grid.expectRow(0, "Beers", "0", "Button[text='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
    }

    @Test fun `create new category`() {
        _get<Button> { text = "New category (Alt+N)" } ._click()

        // make sure that the "New Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
    }

    @Test fun `edit existing category`() {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[text='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        grid._clickRenderer(0, "edit")

        // make sure that the "Edit Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
        expect(cat.name) { _get<TextField> { label = "Category Name" } ._value }
    }

    @Test fun `edit existing category via context menu`() {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[text='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        _get<CategoriesList>().gridContextMenu._clickItemWithCaption("Edit (Alt+E)", cat)

        // make sure that the "Edit Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
        expect(cat.name) { _get<TextField> { label = "Category Name" } ._value }
    }

    @Test fun `delete existing category via context menu`() {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[text='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        _get<CategoriesList>().gridContextMenu._clickItemWithCaption("Delete", cat)

        // check that the category has been deleted in the database.
        expectList() { Category.findAll() }
        _get<Grid<Category>>().expectRows(0)
        expectNotifications("Category successfully deleted.")
    }
}
