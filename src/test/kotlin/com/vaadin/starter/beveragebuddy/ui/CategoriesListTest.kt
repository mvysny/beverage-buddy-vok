package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.dynatest.expectList
import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.Bootstrap
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.categories.CategoriesList
import kotlin.test.expect

// since there is no servlet environment, Flow won't auto-detect the @Routes. We need to auto-discover all @Routes
// and populate the RouteRegistry properly.
private val routes = Routes().autoDiscoverViews("com.vaadin.starter.beveragebuddy")

/**
 * Properly configures the app in the test context, so that the app is properly initialized, and the database is emptied before every test.
 */
@DynaTestDsl
fun DynaNodeGroup.usingApp() {
    beforeGroup { Bootstrap().contextInitialized(null) }
    afterGroup { Bootstrap().contextDestroyed(null) }

    beforeEach { MockVaadin.setup(routes) }
    afterEach { MockVaadin.tearDown() }

    // it's a good practice to clear up the db before every test, to start every test with a predefined state.
    fun cleanupDb() { Category.deleteAll(); Review.deleteAll() }
    beforeEach { cleanupDb() }
    afterEach { cleanupDb() }
}

/**
 * Tests the UI. Uses the Browserless Testing approach as provided by the [Karibu Testing](https://github.com/mvysny/karibu-testing) library.
 */
class CategoriesListTest : DynaTest({

    usingApp()

    beforeEach {
        // navigate to the "Categories" list route.
        navigateTo<CategoriesList>()
    }

    test("grid lists all categories") {
        // prepare testing data
        Category(name = "Beers").save()

        // now the "Categories" list should be displayed. Look up the Grid and assert on its contents.
        val grid = _get<Grid<Category>>()
        grid.expectRows(1)
        grid.expectRow(0, "Beers", "0", "Button[caption='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
    }

    test("create new category") {
        _get<Button> { text = "New category (Alt+N)" } ._click()

        // make sure that the "New Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
    }

    test("edit existing category") {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[caption='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        grid._clickRenderer(0, "edit")

        // make sure that the "Edit Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
        expect(cat.name) { _get<TextField> { label = "Category Name" } ._value }
    }

    test("edit existing category via context menu") {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[caption='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        _get<CategoriesList>().gridContextMenu._clickItemWithCaption("Edit (Alt+E)", cat)

        // make sure that the "Edit Category" dialog is opened
        _expectOne<EditorDialogFrame<*>>()
        expect(cat.name) { _get<TextField> { label = "Category Name" } ._value }
    }

    test("delete existing category via context menu") {
        val cat: Category = Category(name = "Beers").apply { save() }
        val grid = _get<Grid<Category>>()
        grid.expectRow(0, "Beers", "0", "Button[caption='Edit', icon='vaadin:edit', @class='category__edit', @theme='tertiary']")
        _get<CategoriesList>().gridContextMenu._clickItemWithCaption("Delete", cat)

        // check that the category has been deleted in the database.
        expectList() { Category.findAll() }
        _get<Grid<Category>>().expectRows(0)
        expectNotifications("Category successfully deleted.")
    }
})
