package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.kaributesting.v10.expectList
import com.vaadin.flow.data.provider.Query
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import org.junit.jupiter.api.Test

class ReviewWithCategoryTest : AbstractAppTest() {
    @Test fun smoke() {
        val category = Category(name = "Foo")
        category.save()
        val review = Review(name = "Bar", category = category.id)
        review.save()

        expectList(ReviewWithCategory(review, "Foo")) { ReviewWithCategory.dataProvider.fetch(Query()).toList() }
    }
}
