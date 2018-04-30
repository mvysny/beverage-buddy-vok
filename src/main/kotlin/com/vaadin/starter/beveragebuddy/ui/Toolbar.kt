package com.vaadin.starter.beveragebuddy.ui

import com.github.vok.karibudsl.flow.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcons
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class Toolbar(createCaption: String) : Div() {
    var onSearch: (String)->Unit = {}
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
        button(createCaption, Icon("lumo", "plus")) {
            setPrimary()
            addClassName("view-toolbar__button")
            addClickListener { onCreate() }
        }
    }
}

fun (@VaadinDsl HasComponents).toolbarView(createCaption: String, block: (@VaadinDsl Toolbar).() -> Unit = {})
        = init(Toolbar(createCaption), block)
