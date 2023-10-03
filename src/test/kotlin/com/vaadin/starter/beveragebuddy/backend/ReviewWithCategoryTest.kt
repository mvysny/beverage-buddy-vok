package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import com.vaadin.starter.beveragebuddy.ui.usingApp

class ReviewWithCategoryTest : DynaTest({
    usingApp()

    test("smoke") {
        val category = Category(name = "Foo")
        category.save()
        val review = Review(name = "Bar", category = category.id)
        review.save()

        expectList(ReviewWithCategory(review, "Foo")) { ReviewWithCategory.dataLoader.fetch() }
    }
})
