package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.ui.categories.CategoriesList
import com.vaadin.starter.beveragebuddy.ui.categories.CategoryEditorDialog
import kotlin.test.expect

/**
 * Tests the UI. Uses the Browserless Testing approach as provided by the [Karibu Testing](https://github.com/mvysny/karibu-testing) library.
 */
class CategoryEditorDialogTest : DynaTest({

    usingApp()

    beforeEach {
        // navigate to the "Categories" list route.
        navigateTo<CategoriesList>()
    }

    test("create new category") {
        CategoryEditorDialog {} .createNew()

        // make sure that the "New Category" dialog is opened
        _get<EditorDialogFrame<*>>()

        // do the happy flow: fill in the form with valid values and click "Save"
        _get<TextField> { caption = "Category Name" } .value = "Beer"
        _get<Button> { caption = "Create" } ._click()
        expectNotifications("Category successfully added.")

        _expectNone<EditorDialogFrame<*>>()     // expect the dialog to close
        expect("Beer") { Category.one!!.name }
    }
})
