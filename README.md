[![Powered By Vaadin on Kotlin](http://vaadinonkotlin.eu/iconography/vok_badge.svg)](http://vaadinonkotlin.eu)
[![Join the chat at https://gitter.im/vaadin/vaadin-on-kotlin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/vaadin/vaadin-on-kotlin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Beverage Buddy App Starter for Vaadin
:coffee::tea::sake::baby_bottle::beer::cocktail::tropical_drink::wine_glass:

This is a [Vaadin-on-Kotlin](http://vaadinonkotlin.eu) example application,
used to demonstrate features of the Vaadin Flow Java framework.
A full-stack app: uses the H2 database instead of a dummy service.

The Starter demonstrates the core Vaadin Flow concepts:
* [Building UIs in Kotlin](https://github.com/mvysny/karibu-dsl) with components
  such as `TextField`, `Button`, `ComboBox`, `DatePicker`, `VerticalLayout` and `Grid` (see `CategoriesList`)
* [Creating forms with `Binder`](https://github.com/vaadin/free-starter-flow/blob/master/documentation/using-binder-in-review-editor-dialog.asciidoc) (`ReviewEditorDialog`)
* Making reusable Components on server side with `KComposite` (`AbstractEditorDialog`)
* [Creating Navigation with the Router API](https://github.com/vaadin/free-starter-flow/blob/master/documentation/using-annotation-based-router-api.asciidoc) (`MainLayout`, `ReviewsList`, `CategoriesList`)
* [Browserless testing](https://github.com/mvysny/karibu-testing): see the
  [test suite package](src/test/kotlin/com/vaadin/starter/beveragebuddy/ui) for the complete test implementation.

This version of Beverage Buddy demoes the possibility of developing a Vaadin
web application purely server-side in the Kotlin language. There is no
JavaScript code in this project. If you'd like to see
a demo on how to create Polymer Templates, please head to the page of the
[original Beverage Buddy App](https://github.com/vaadin/beverage-starter-flow) (in Java).

# Documentation

Please see the [Vaadin Boot](https://github.com/mvysny/vaadin-boot#preparing-environment) documentation
on how you run, develop and package this Vaadin-Boot-based app.
