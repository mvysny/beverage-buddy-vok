package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.kaributesting.v23.expectRows
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.backend.ReviewWithCategory
import com.vaadin.starter.beveragebuddy.ui.reviews.ReviewEditorDialog
import kotlin.test.expect

class ReviewEditorDialogTest : DynaTest({
    usingApp()

    test("smoke") {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
    }

    test("'cancel' closes the dialog") {
        ReviewEditorDialog({}).createNew()
        _get<Button> { caption = "Cancel" }._click()
        _expectNone<EditorDialogFrame<*>>()
    }

    test("simple validation failure") {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
        _get<Button> { caption = "Create" } ._click()

        _expectOne<EditorDialogFrame<*>>()
        expectNotifications("There are errors in the form")
        _get<TextField> { caption = "Beverage name"} ._expectInvalid("must not be blank")
    }

    test("create review without setting a category fails") {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
        _get<TextField> { caption = "Beverage name" } ._value = "FooBar"
        _get<Button> { caption = "Create" } ._click()

        _expectOne<EditorDialogFrame<*>>()
        // no review has been created
        expectList() { Review.findAll() }
    }

    test("create new review") {
        val cat = Category(name = "Beers")
        cat.save()

        _get<Button> { caption = "New review (Alt+N)" }._click()

        _expectOne<EditorDialogFrame<*>>()
        _get<TextField> { caption = "Beverage name" }._value = "Test"
        _get<IntegerField> { caption = "Times tasted" }._value = 1
        _get<ComboBox<Category>> { caption = "Choose a category" }._value = cat
        _get<ComboBox<String>> { caption = "Mark a score" } .selectByLabel("3")

        _get<Button> { caption = "Create" } ._click()
        expectNotifications("Beverage successfully added.")

        _expectNone<EditorDialogFrame<*>>()     // expect the dialog to close
        val review = Review.one!!
        expect("Test") { review.name }
        expect(3) { review.score }
        expect(cat.id) { review.category }
        expect(1) { review.count }
    }
})
