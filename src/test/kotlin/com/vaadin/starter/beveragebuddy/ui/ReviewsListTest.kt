package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.kaributesting.v23.expectRows
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.backend.ReviewWithCategory
import kotlin.test.expect

class ReviewsListTest : DynaTest({

    usingApp()

    test("no reviews initially") {
        _get<VirtualList<ReviewWithCategory>>().expectRows(0)
    }

    test("reviews listed") {
        // prepare testing data
        val cat = Category(name = "Beers")
        cat.save()
        Review(score = 1, name = "Good!", category = cat.id).save()
        _get<VirtualList<ReviewWithCategory>>().expectRows(1)
    }

    test("'new review' smoke test") {
        UI.getCurrent().navigate("")
        _get<Button> { caption = "New review (Alt+N)" }._click()

        // the dialog should have been opened
        _expectOne<EditorDialogFrame<*>>()

        // this is just a smoke test, so let's close the dialog
        _get<Button> { caption = "Cancel" }._click()

        _expectNone<EditorDialogFrame<*>>()
    }

    test("'new review' dialog") {
        val cat = Category(name = "Beers")
        cat.save()

        _get<Button> { caption = "New review (Alt+N)" }._click()

        _expectOne<EditorDialogFrame<*>>()
        _get<TextField> { caption = "Beverage name" }._value = "Test"
        _get<IntegerField> { caption = "Times tasted" }._value = 1
        _get<ComboBox<Category>> { caption = "Choose a category" }._value = cat
        _get<ComboBox<String>> { caption = "Mark a score" } .selectByLabel("3")

        _get<Button> { caption = "Save" } ._click()
        expectNotifications("Beverage successfully added.")

        _expectNone<EditorDialogFrame<*>>()     // expect the dialog to close
        _get<VirtualList<ReviewWithCategory>>().expectRows(1)
        val review = Review.findAll().single()
        expect("Test") { review.name }
        expect(3) { review.score }
        expect(cat.id) { review.category }
        expect(1) { review.count }
    }
})
