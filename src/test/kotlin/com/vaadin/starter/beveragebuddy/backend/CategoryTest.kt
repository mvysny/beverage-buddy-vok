package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect

class CategoryTest : DynaTest({
    group("validation") {
        test("smoke") {
            expect(false) { Category().isValid }
            expect(false) { Category(name = "  ").isValid }
            expect(true) { Category(name = "F").isValid }
        }
    }
})
