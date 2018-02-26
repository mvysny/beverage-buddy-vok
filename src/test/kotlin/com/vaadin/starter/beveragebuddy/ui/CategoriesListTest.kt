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
import org.junit.*
import kotlin.test.expect

class CategoriesListTest : DynaTest({
    beforeGroup { Bootstrap().contextInitialized(null) }
    afterGroup { Bootstrap().contextDestroyed(null) }

    beforeEach { MockVaadin.setup(autoDiscoverViews("com.vaadin.starter")) }
    fun cleanupDb() {
        Category.deleteAll()
    }
    beforeEach { cleanupDb() }
    afterEach { cleanupDb() }

    test("GridListsAllPersons") {
        Category(name = "Beers").save()
        UI.getCurrent().navigateTo("categories")

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
