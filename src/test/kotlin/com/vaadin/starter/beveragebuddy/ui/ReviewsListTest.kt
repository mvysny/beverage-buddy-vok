package com.vaadin.starter.beveragebuddy.ui

import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.dynatest.DynaTest
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.starter.beveragebuddy.ui.reviews.ReviewEditorDialog

class ReviewsListTest : DynaTest({

    usingApp()

    test("'new review' smoke test") {
        UI.getCurrent().navigate("")
          _get<Button> { caption = "New review" } ._click()

        // the dialog should have been opened
        _get<ReviewEditorDialog>()

        // this is just a smoke test, so let's close the dialog
        _get<Button> { caption = "Cancel" } ._click()

        _expectNone<ReviewEditorDialog>()
    }
})
