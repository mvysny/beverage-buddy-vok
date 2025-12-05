package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.kaributesting.v10.expectList
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.provider.QuerySortOrder
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import org.junit.jupiter.api.Test
import kotlin.test.expect

class ReviewWithCategoryTest : AbstractAppTest() {
    @Test fun smoke() {
        val category = Category(name = "Foo")
        category.save()
        val review = Review(name = "Bar", category = category.id)
        review.save()

        expectList(ReviewWithCategory(review, "Foo")) { ReviewWithCategory.dataProvider.fetch(Query()).toList() }
        expect(1) { ReviewWithCategory.dataProvider.size(Query()) }
        val query = Query<ReviewWithCategory, Condition>(
            0,
            30,
            listOf(QuerySortOrder(Review.NAME.toExternalString(), SortDirection.ASCENDING)),
            null,
            null
        )
        expectList(
            ReviewWithCategory(
                review,
                "Foo"
            )
        ) { ReviewWithCategory.dataProvider.fetch(query).toList() }
    }
}
