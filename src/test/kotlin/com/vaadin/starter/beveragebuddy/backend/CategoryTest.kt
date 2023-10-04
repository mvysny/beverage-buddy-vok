package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import com.vaadin.starter.beveragebuddy.ui.usingApp
import java.time.LocalDate
import kotlin.test.expect

class CategoryTest : DynaTest({
    usingApp()

    group("validation") {
        test("smoke") {
            expect(false) { Category().isValid }
            expect(false) { Category(name = "  ").isValid }
            expect(true) { Category(name = "F").isValid }
        }
    }

    group("delete") {
        test("smoke") {
            val cat = Category(name = "Foo")
            cat.save()
            cat.delete()
            expectList() { Category.findAll() }
        }
        test("deleting category fixes foreign keys") {
            val cat = Category(name = "Foo")
            cat.save()
            val review = Review(name = "Foo", score = 1, date = LocalDate.now(), category = cat.id!!, count = 1)
            review.save()

            cat.delete()
            expectList() { Category.findAll() }
            expect(null) { Review.single().category }
        }
    }

    test("existsWithName()") {
        expect(false) { Category.existsWithName("Foo") }
        Category(name = "Foo").save()
        expect(true) { Category.existsWithName("Foo") }
    }
})
