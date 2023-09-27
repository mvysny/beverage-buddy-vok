package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect

class ReviewTest : DynaTest({
    group("validation") {
        test("smoke") {
            expect(true) { Review(name = "Foo", category = 1L).isValid }
            expect(false) { Review().isValid }
            expect(false) { Review(category = 1L).isValid }
            expect(false) { Review(name = "Foo").isValid }
            expect(false) { Review(name = "F", category = 1L).isValid }
            expect(false) { Review(score = 10, name = "Foo", category = 1L).isValid }
        }
    }
})
