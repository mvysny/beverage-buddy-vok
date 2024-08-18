package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.kaributesting.v10.expectList
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.expect

class CategoryTest : AbstractAppTest() {
    @Nested inner class validation {
        @Test fun smoke() {
            expect(false) { Category().isValid }
            expect(false) { Category(name = "  ").isValid }
            expect(true) { Category(name = "F").isValid }
        }
    }

    @Nested inner class delete {
        @Test fun smoke() {
            val cat = Category(name = "Foo")
            cat.save()
            cat.delete()
            expectList() { Category.findAll() }
        }
        @Test fun `deleting category fixes foreign keys`() {
            val cat = Category(name = "Foo")
            cat.save()
            val review = Review(name = "Foo", score = 1, date = LocalDate.now(), category = cat.id!!, count = 1)
            review.save()

            cat.delete()
            expectList() { Category.findAll() }
            expect(null) { Review.single().category }
        }
    }

    @Test fun existsWithName() {
        expect(false) { Category.existsWithName("Foo") }
        Category(name = "Foo").save()
        expect(true) { Category.existsWithName("Foo") }
    }
}
