/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.starter.beveragebuddy.ui.reviews

import com.github.mvysny.karibudsl.v10.*
import com.github.vokorm.getById
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.backend.ReviewWithCategory
import com.vaadin.starter.beveragebuddy.backend.setFilterText
import com.vaadin.starter.beveragebuddy.ui.AbstractEditorDialog
import com.vaadin.starter.beveragebuddy.ui.MainLayout
import com.vaadin.starter.beveragebuddy.ui.Toolbar
import com.vaadin.starter.beveragebuddy.ui.toolbarView
import eu.vaadinonkotlin.vaadin10.VokDataProvider

/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 */
@Route(value = "", layout = MainLayout::class)
@PageTitle("Review List")
class ReviewsList : VerticalLayout() {

    private val toolbar: Toolbar
    private val header: H3
    private val reviewsGrid: Grid<ReviewWithCategory>
    private val reviewForm = ReviewEditorDialog(
        { review, operation -> save(review, operation) },
        { this.delete(it) })

    init {
        isPadding = false; content { align(stretch, top) }
        toolbar = toolbarView("New review") {
            onSearch = { updateList() }
            onCreate = { openForm(Review(), AbstractEditorDialog.Operation.ADD) }
        }
        header = h3 {
            setId("header")
        }
        reviewsGrid = grid {
            isExpand = true
            addClassName("reviews")
            themes.add("no-row-borders no-border")
            addColumn(ComponentRenderer<ReviewItem, ReviewWithCategory>({ review ->
                val item = ReviewItem(review)
                item.onEdit = { openForm(Review.getById(review.id!!), AbstractEditorDialog.Operation.EDIT) }
                item
            }))
        }
        updateList()
    }

    private fun save(review: Review, operation: AbstractEditorDialog.Operation) {
        review.save()
        // unfortunately the Grid is not updated, because of a bug: https://github.com/vaadin/vaadin-grid-flow/issues/175
        updateList()
        Notification.show("Beverage successfully ${operation.nameInText}ed.", 3000, Notification.Position.BOTTOM_START)
    }

    private fun delete(review: Review) {
        review.delete()
        updateList()
        Notification.show("Beverage successfully deleted.", 3000, Notification.Position.BOTTOM_START)
    }

    private fun updateList() {
        val dp: VokDataProvider<ReviewWithCategory> = ReviewWithCategory.dataProvider
        dp.setFilterText(toolbar.searchText)
        val size: Int = dp.size(Query())
        if (toolbar.searchText.isBlank()) {
            header.text = "Reviews"
            header.add(Span("$size in total"))
        } else {
            header.text = "Search for “${toolbar.searchText}”"
            header.add(Span("$size results"))
        }
        reviewsGrid.dataProvider = dp
    }

    private fun openForm(review: Review, operation: AbstractEditorDialog.Operation) {
        reviewForm.open(review, operation)
    }
}

/**
 * Shows a single row stripe with information about a single [ReviewWithCategory].
 */
class ReviewItem(val review: ReviewWithCategory) : Composite<Div>() {
    // can't extend Div directly because of https://youtrack.jetbrains.com/issue/KT-24239
    /**
     * Fired when this item is to be edited (the "Edit" button is pressed by the User).
     */
    var onEdit: () -> Unit = {}

    private val content = Div().apply {
        addClassName("review")
        div {
            addClassName("review__rating")
            p(review.score.toString()) {
                className = "review__score"
                element.setAttribute("data-score", review.score.toString())
            }
            p(review.count.toString()) {
                className = "review__count"
                span("times tasted")
            }
        }
        div {
            addClassName("review__details")
            h4(review.name) {
                addClassName("review__name")
            }
            p {
                className = "review__category"
                if (review.category != null) {
                    themes.add("badge small")
                    element.style.set("--category", review.category.toString())
                    text = review.categoryName
                } else {
                    element.style.set("--category", "-1")
                    text = "Undefined"
                }
            }
        }
        div {
            className = "review__date"
            h5("Last tasted")
            p(review.date.toString())
        }
        button("Edit") {
            icon = VaadinIcon.EDIT.create()
            className = "review__edit"
            themes.add("tertiary")
            onLeftClick { onEdit() }
        }
    }

    override fun initContent(): Div = content
}

fun (@VaadinDsl HasComponents).p(text: String = "", block: (@VaadinDsl Paragraph).() -> Unit = {}) = init(Paragraph(text), block)
