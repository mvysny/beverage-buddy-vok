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

import com.github.vok.karibudsl.flow.content
import com.github.vok.karibudsl.flow.div
import com.github.vok.karibudsl.flow.h3
import com.github.vokorm.getById
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.icon.VaadinIcons
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.polymertemplate.EventHandler
import com.vaadin.flow.component.polymertemplate.ModelItem
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.starter.beveragebuddy.backend.Review
import com.vaadin.starter.beveragebuddy.backend.ReviewWithCategory
import com.vaadin.starter.beveragebuddy.ui.AbstractEditorDialog
import com.vaadin.starter.beveragebuddy.ui.MainLayout
import com.vaadin.starter.beveragebuddy.ui.Toolbar
import com.vaadin.starter.beveragebuddy.ui.toolbarView
import kotlin.streams.toList


/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 *
 * Implemented using a simple template.
 */
@Route(value = "", layout = MainLayout::class)
@PageTitle("Review List")
@HtmlImport("frontend://reviews-list.html")
class ReviewsList : VerticalLayout() {

    private val toolbar: Toolbar
    private val header: H3
    private val reviews: Div
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
        reviews = div {
            addClassName("reviews")
        }
        updateList()
    }

    private fun save(review: Review, operation: AbstractEditorDialog.Operation) {
        review.save()
        updateList()
        Notification.show("Beverage successfully ${operation.nameInText}ed.", 3000, Notification.Position.BOTTOM_START)
    }

    private fun delete(review: Review) {
        review.delete()
        updateList()
        Notification.show("Beverage successfully deleted.", 3000, Notification.Position.BOTTOM_START)
    }

    private fun updateList() {
        val reviews = Review.findReviews(toolbar.searchText)
        if (toolbar.searchText.isBlank()) {
            header.text = "Reviews"
            header.add(Span("${reviews.size} in total"))
        } else {
            header.text = "Search for “${toolbar.searchText}”"
            if (!reviews.isEmpty()) {
                header.add(Span("${reviews.size} results"))
            }
        }

        this.reviews.removeAll()
        for (review in reviews) {
            this.reviews.add(ReviewItem(review).apply {
                onEdit = { openForm(Review.getById(review.id!!), AbstractEditorDialog.Operation.EDIT) }
            })
        }
    }

    private fun openForm(review: Review,operation: AbstractEditorDialog.Operation) {
        reviewForm.open(review, operation)
    }
}


class ReviewItem(val review: ReviewWithCategory) : Div() {

    var onEdit: ()->Unit = {}
    init {
        addClassName("review")
        add(createRatingDiv())
        add(createDetailsDiv())
        add(createDateDiv())
        add(createButton())
    }

    private fun createButton(): Button {
        val button = Button("Edit")
        button.setIcon(VaadinIcons.EDIT.create())
        button.setClassName("review__edit")
        button.getElement().setAttribute("theme", "tertiary")
        button.addClickListener({ onEdit() })
        return button
    }

    private fun createDateDiv(): Div {
        val date = Div()
        date.setClassName("review__date")
        val lastTasted = H5("Last tasted")
        val dateP = Paragraph(review.date.toString())
        date.add(lastTasted, dateP)
        return date
    }

    private fun createRatingDiv(): Div {
        val ratingDiv = Div()
        ratingDiv.addClassName("review__rating")
        val score = Paragraph()
        score.className = "review__score"
        score.text = review.score.toString()
        score.element.setAttribute("data-score", review.score.toString())
        val count = Paragraph()
        count.className = "review__count"
        count.text = "" + review.count
        val timesTasted = Span("times tasted")
        count.add(timesTasted)
        ratingDiv.add(score, count)
        return ratingDiv
    }

    private fun createDetailsDiv(): Div {
        val detailsDiv = Div()
        detailsDiv.addClassName("review__details")
        val h4 = H4(review.name)
        h4.addClassName("review__name")
        detailsDiv.add(h4)
        val category = Paragraph()
        category.className = "review__category"
        if (review.category != null) {
            category.element.setAttribute("theme", "badge small")
            category.element.style.set("--category", review.category.toString())
            category.text = review.categoryName
        } else {
            category.element.setAttribute("style", "--category: -1;")
            category.text = "Undefined"
        }
        detailsDiv.add(category)
        return detailsDiv
    }
}
