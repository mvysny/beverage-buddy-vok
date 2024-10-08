package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v23.expectRows
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.backend.ReviewWithCategory
import org.junit.jupiter.api.Test

class ReviewsListTest : AbstractAppTest() {
    @Test fun `no reviews initially`() {
        _get<VirtualList<ReviewWithCategory>>().expectRows(0)
    }

    @Test fun reviewsListed() {
        // prepare testing data
        val cat = Category(name = "Beers")
        cat.save()
        Review(score = 1, name = "Good!", category = cat.id).save()
        _get<VirtualList<ReviewWithCategory>>().expectRows(1)
    }

    @Test fun `'new review' smoke test`() {
        UI.getCurrent().navigate("")
        _get<Button> { text = "New review (Alt+N)" }._click()

        // the dialog should have been opened
        _expectOne<EditorDialogFrame<*>>()

        // this is just a smoke test, so let's close the dialog
        _get<Button> { text = "Cancel" }._click()

        _expectNone<EditorDialogFrame<*>>()
    }
}
