[![Build Status](https://travis-ci.org/mvysny/beverage-buddy-vok.svg?branch=master)](https://travis-ci.org/mvysny/beverage-buddy-vok)

# Beverage Buddy App Starter for Vaadin 10
:coffee::tea::sake::baby_bottle::beer::cocktail::tropical_drink::wine_glass:

This is a Vaadin 10 [Vaadin-on-Kotlin](http://vaadinonkotlin.eu) example application, used to demonstrate features of the Vaadin Flow Java framework.
A full-stack app: uses Sql2o and H2 instead of a dummy service.

The Starter demonstrates the core Vaadin Flow concepts:
* Building UIs in Kotlin with Components based on [Vaadin Elements](https://vaadin.com/elements/browse), such as `TextField`, `Button`, `ComboBox`, `DatePicker`, `VerticalLayout` and `Grid` (see `CategoriesList`)
* [Creating forms with `Binder`](https://github.com/vaadin/free-starter-flow/blob/master/documentation/using-binder-in-review-editor-dialog.asciidoc) (`ReviewEditorDialog`)
* Making reusable Components on server side with `Composite` (`AbstractEditorDialog`)
* [Creating a Component based on a HTML Template](https://github.com/vaadin/free-starter-flow/blob/master/documentation/polymer-template-based-view.asciidoc) (`ReviewsList`) 
  * This template can be opened and edited with [the Vaadin Designer](https://vaadin.com/designer)
* [Creating Navigation with the Router API](https://github.com/vaadin/free-starter-flow/blob/master/documentation/using-annotation-based-router-api.asciidoc) (`MainLayout`, `ReviewsList`, `CategoriesList`)

## Live Demo

You can find the [Online Beverage Buddy Demo](https://beverage-buddy-vok.herokuapp.com) running on Heroku.

## Prerequisites

The project can be imported into the IDE of your choice, with Java 8 installed, as a Gradle project.

## Running the Project

1. Run using `./gradlew appRun`
2. Wait for the application to start
3. Open [http://localhost:8080/](http://localhost:8080/) to view the application

## Documentation

Brief introduction to the application parts can be found from the `documentation` folder. For Vaadin 10 documentation for Java users, see [Vaadin.com/docs](https://vaadin.com/docs/v10/flow/Overview.html).
