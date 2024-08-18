package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.kaributesting.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.ui.reviews.ReviewEditorDialog
import org.junit.jupiter.api.Test
import kotlin.test.expect

class ReviewEditorDialogTest : AbstractAppTest() {
    @Test fun smoke() {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
    }

    @Test fun `'cancel' closes the dialog`() {
        ReviewEditorDialog({}).createNew()
        _get<Button> { text = "Cancel" }._click()
        _expectNone<EditorDialogFrame<*>>()
    }

    @Test fun `simple validation failure`() {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
        _get<Button> { text = "Create" } ._click()

        _expectOne<EditorDialogFrame<*>>()
        expectNotifications("There are errors in the form")
        _get<TextField> { label = "Beverage name"} ._expectInvalid("must not be blank")
    }

    @Test fun `create review without setting a category fails`() {
        ReviewEditorDialog({}).createNew()
        _expectOne<EditorDialogFrame<*>>()
        _get<TextField> { label = "Beverage name" } ._value = "FooBar"
        _get<Button> { text = "Create" } ._click()

        _expectOne<EditorDialogFrame<*>>()
        // no review has been created
        expectList() { Review.findAll() }
    }

    @Test fun `create new review`() {
        val cat = Category(name = "Beers")
        cat.save()

        _get<Button> { text = "New review (Alt+N)" }._click()

        _expectOne<EditorDialogFrame<*>>()
        _get<TextField> { label = "Beverage name" }._value = "Test"
        _get<IntegerField> { label = "Times tasted" }._value = 1
        _get<ComboBox<Category>> { label = "Choose a category" }._value = cat
        _get<ComboBox<String>> { label = "Mark a score" } .selectByLabel("3")

        _get<Button> { text = "Create" } ._click()
        expectNotifications("Beverage successfully added.")

        _expectNone<EditorDialogFrame<*>>()     // expect the dialog to close
        val review = Review.single()
        expect("Test") { review.name }
        expect(3) { review.score }
        expect(cat.id) { review.category }
        expect(1) { review.count }
    }
}
