package com.vaadin.starter.beveragebuddy.ui

import com.github.vok.karibudsl.flow.*
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

/**
 * A toolbar with a search box and a "Create New" button. Don't forget to provide proper listeners
 * for [onSearch] and [onCreate].
 * @param createCaption the caption of the "Create New" button.
 */
class Toolbar(createCaption: String) : Composite<Div>() {
    // can't extend Div directly because of https://youtrack.jetbrains.com/issue/KT-24239
    /**
     * Fired when the text in the search text field changes.
     */
    var onSearch: (String)->Unit = {}
    /**
     * Fired when the "Create new" button is clicked.
     */
    var onCreate: ()->Unit = {}
    private lateinit var searchField: TextField
    /**
     * Current search text. Never null, trimmed, may be blank.
     */
    val searchText: String get() = searchField.value.trim()

    private val contents = Div().apply {
        addClassName("view-toolbar")
        searchField = textField {
            prefixComponent = Icon(VaadinIcon.SEARCH)
            addClassName("view-toolbar__search-field")
            placeholder = "Search"
            addValueChangeListener { onSearch(searchText) }
            valueChangeMode = ValueChangeMode.EAGER
        }
        button(createCaption, Icon(VaadinIcon.PLUS)) {
            setPrimary()
            addClassName("view-toolbar__button")
            addClickListener { onCreate() }
        }
    }

    override fun initContent(): Div = contents
}

fun (@VaadinDsl HasComponents).toolbarView(createCaption: String, block: (@VaadinDsl Toolbar).() -> Unit = {})
        = init(Toolbar(createCaption), block)
