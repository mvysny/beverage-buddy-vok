package com.vaadin.starter.beveragebuddy.ui

import com.github.vok.karibudsl.flow.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcons
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

/**
 * A toolbar with a search box and a "Create New" button. Don't forget to provide proper listeners
 * for [onSearch] and [onCreate].
 * @param createCaption the caption of the "Create New" button.
 */
class Toolbar(createCaption: String) : Div() {
    /**
     * Fired when the text in the search text field changes.
     */
    var onSearch: (String)->Unit = {}
    /**
     * Fired when the "Create new" button is clicked.
     */
    var onCreate: ()->Unit = {}
    private val searchField: TextField
    /**
     * Current search text. Never null, trimmed, may be blank.
     */
    val searchText: String get() = searchField.value.trim()
    init {
        addClassName("view-toolbar")
        searchField = textField {
            prefixComponent = Icon(VaadinIcons.SEARCH)
            addClassName("view-toolbar__search-field")
            placeholder = "Search"
            addValueChangeListener { onSearch(searchText) }
            valueChangeMode = ValueChangeMode.EAGER
        }
        button(createCaption, Icon(VaadinIcons.PLUS)) {
            setPrimary()
            addClassName("view-toolbar__button")
            addClickListener { onCreate() }
        }
    }
}

fun (@VaadinDsl HasComponents).toolbarView(createCaption: String, block: (@VaadinDsl Toolbar).() -> Unit = {})
        = init(Toolbar(createCaption), block)
