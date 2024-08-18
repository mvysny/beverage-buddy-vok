package com.vaadin.starter.beveragebuddy

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10.Routes
import com.vaadin.starter.beveragebuddy.backend.Category
import com.vaadin.starter.beveragebuddy.backend.Review
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach

/**
 * Properly configures the app in the test context, so that the app is properly initialized, and the database is emptied before every test.
 */
abstract class AbstractAppTest {
    companion object {
        // since there is no servlet environment, Flow won't auto-detect the @Routes. We need to auto-discover all @Routes
        // and populate the RouteRegistry properly.
        private val routes = Routes().autoDiscoverViews("com.vaadin.starter.beveragebuddy")

        @BeforeAll @JvmStatic fun startApp() { Bootstrap().contextInitialized(null) }
        @AfterAll @JvmStatic fun stopApp() { Bootstrap().contextDestroyed(null) }
    }

    @BeforeEach fun setupVaadin() { MockVaadin.setup(routes) }
    @AfterEach fun teardownVaadin() { MockVaadin.tearDown() }

    // it's a good practice to clear up the db before every test, to start every test with a predefined state.
    @BeforeEach @AfterEach fun cleanupDb() { Category.deleteAll(); Review.deleteAll() }
}
