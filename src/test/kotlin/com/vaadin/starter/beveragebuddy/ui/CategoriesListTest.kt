package com.vaadin.starter.beveragebuddy.ui

import com.github.karibu.testing.*
import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import com.github.vokorm.findAll
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.Bootstrap
import com.vaadin.starter.beveragebuddy.backend.Category
import kotlin.test.expect

/**
 * Tests the UI. Uses the Serverless testing approach as provided by the [Karibu Testing](https://github.com/mvysny/karibu-testing) library.
 */
class CategoriesListTest : DynaTest({
    beforeGroup { Bootstrap().contextInitialized(null) }
    afterGroup { Bootstrap().contextDestroyed(null) }

    // since there is no servlet environment, Flow won't auto-detect the @Routes. We need to auto-discover all @Routes
    // and populate the RouteRegistry properly.
    beforeEach { MockVaadin.setup(autoDiscoverViews("com.vaadin.starter")) }

    // it's a good practice to clear up the db before every test, to start every test with a predefined state.
    fun cleanupDb() { Category.deleteAll() }
    beforeEach { cleanupDb() }
    afterEach { cleanupDb() }

    test("grid lists all persons") {
        // prepare testing data
        Category(name = "Beers").save()
        // navigate to the "Categories" list route.
        UI.getCurrent().navigateTo("categories")

        // now the "Categories" list should be displayed. Look up the Grid and assert on its contents.
        val grid = _get<Grid<*>>()
        expect(1) { grid.dataProvider._size() }
    }

    test("create new category") {
        UI.getCurrent().navigateTo("categories")
        _get<Button> { caption = "New category" } ._click()

        // make sure that the "New Category" dialog is opened
        _get<CategoryEditorDialog>()

        // do the happy flow: fill in the form with valid values and click "Save"
        _get<TextField> { caption = "Category Name" } .value = "Beer"
        _get<Button> { caption = "Save" } ._click()

        _find<CategoryEditorDialog> { count = 0..0 }   // expect the dialog to close
        expectList("Beer") { Category.findAll().map { it.name } }
    }
})
