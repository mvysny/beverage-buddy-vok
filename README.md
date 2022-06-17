[![Powered By Vaadin on Kotlin](http://vaadinonkotlin.eu/iconography/vok_badge.svg)](http://vaadinonkotlin.eu)
[![Join the chat at https://gitter.im/vaadin/vaadin-on-kotlin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/vaadin/vaadin-on-kotlin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Heroku](https://heroku-badge.herokuapp.com/?app=beverage-buddy-vok&style=flat&svg=1)](https://beverage-buddy-vok.herokuapp.com/)

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

## Live Demo

You can find the [Online Beverage Buddy Demo](https://beverage-buddy-vok.herokuapp.com) running on Heroku.

# Preparing Environment

The Vaadin build requires node.js and npm. Vaadin Gradle plugin will install those
for you automatically (handy for the CI); alternatively you can install those to your OS:

* Windows: [node.js Download site](https://nodejs.org/en/download/) - use the .msi 64-bit installer
* Linux: `sudo apt install npm`

Also make sure that you have Java 8 (or higher) JDK installed.

## Getting Started

To quickly start the app, just type this into your terminal:

```bash
git clone https://github.com/mvysny/beverage-buddy-vok
cd beverage-buddy-vok
./gradlew appRun
```

Gradle will automatically download an embedded servlet container (Jetty) and will run your app in it. Your app will be running on
[http://localhost:8080](http://localhost:8080).

Since the build system is a Gradle file written in Kotlin, we suggest you
use [Intellij IDEA](https://www.jetbrains.com/idea/download)
to edit the project files. The Community edition is enough to run the server
via Gretty's `./gradlew appRun`. The Ultimate edition will allow you to run the
project in Tomcat - this is the recommended
option for a real development.

## Supported Modes

Runs in Vaadin npm mode, using the [Vaadin Gradle Plugin](https://github.com/vaadin/vaadin-gradle-plugin).

Both the [development and production modes](https://vaadin.com/docs/v14/flow/production/tutorial-production-mode-basic.html) are supported.
To prepare for development mode, just run:

```bash
./gradlew clean vaadinPrepareFrontend
```

To build in production mode, just run:

```bash
./gradlew clean build -Pvaadin.productionMode
```

If you don't have node installed in your CI environment, Gradle Vaadin plugin will download node.js for you automatically:

```bash
./gradlew clean build -Pvaadin.productionMode
```

# Workflow

To compile the entire project in production mode, run `./gradlew -Pvaadin.productionMode`.

To run the application in development mode, run `./gradlew appRun` and open [http://localhost:8080/](http://localhost:8080/).

To produce a deployable production-mode WAR:
- run `./gradlew -Pvaadin.productionMode`
- You will find the WAR file in `build/libs/*.war`
- To revert your environment back to development mode, just run `./gradlew` or `./gradlew vaadinPrepareFrontend`
  (omit the `-Pvaadin.productionMode`) switch.

This will allow you to quickly start the example app and allow you to do some basic modifications.

## Dissection of project files

Let's look at all files that this project is composed of, and what are the points where you'll add functionality:

| Files | Meaning
| ----- | -------
| [build.gradle.kts](build.gradle.kts) | [Gradle](https://gradle.org/) build tool configuration files. Gradle is used to compile your app, download all dependency jars and build a war file
| [gradlew](gradlew), [gradlew.bat](gradlew.bat), [gradle/](gradle) | Gradle runtime files, so that you can build your app from command-line simply by running `./gradlew`, without having to download and install Gradle distribution yourself.
| [.travis.yml](.travis.yml) | Configuration file for [Travis-CI](http://travis-ci.org/) which tells Travis how to build the app. Travis watches your repo; it automatically builds your app and runs all the tests after every commit.
| [Procfile](Procfile) | Tells [Heroku](https://www.heroku.com/) hosting service how to run your app in a cloud. See below on how to deploy your app on Heroku for free.
| [.gitignore](.gitignore) | Tells [Git](https://git-scm.com/) to ignore files that can be produced from your app's sources - be it files produced by Gradle, Intellij project files etc.
| [src/main/resources/](src/main/resources) | A bunch of static files not compiled by Kotlin in any way; see below for explanation.
| [simplelogger.properties](src/main/resources/logback.xml) | We're using [Slf4j](https://www.slf4j.org/) for logging and this is the configuration file for Slf4j
| [db/migration/](src/main/resources/db/migration) | Database upgrade instructions for the [Flyway](https://flywaydb.org/) framework. Database is upgraded on every server boot, to ensure it's always up-to-date. See the [Migration Naming Guide](https://flywaydb.org/documentation/migrations#naming) for more details.
| [webapp/](src/main/webapp) | contains static webapp resources, such as potential Polymer templates, components, the global app CSS file, etc. The CSS file references the Vaadin Lumo theme and configures it by the means of CSS variables. Polymer templates are not used in this project.
| [frontend/styles/shared-styles.html](src/main/webapp/frontend/styles/shared-styles.html) | The CSS styles applied to your web app. Vaadin by default uses [Vaadin Lumo Theme](https://vaadin.com/themes/lumo); you can tweak the Lumo theme by the means of setting CSS variables.
| [src/main/kotlin/](src/main/kotlin) | The main Kotlin sources of your web app. You'll be mostly editing files located in this folder.
| [Bootstrap.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/Bootstrap.kt) | When Servlet Container (such as Tomcat) starts your app, it will run the `Bootstrap.contextInitialized()` function before any calls to your app are made. We need to bootstrap the Vaadin-on-Kotlin framework, in order to have support for the database; then we'll run Flyway migration scripts, to make sure that the database is up-to-date. After that's done, your app is ready to be serving client browsers.
| [MainLayout.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/MainLayout.kt) | The main view of the app, it defines how the UI looks like and how the components are nested into one another. The UI is defined by the means of so-called DSL; see [Karibu-DSL examples](https://github.com/mvysny/karibu-dsl#how-to-write-dsls-for-vaadin-8-and-vaadin8-v7-compat) for more examples.
| [CategoriesList.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/categories/CategoriesList.kt) | An example view which is constructed entirely server-side. Demonstrates the use of the Vaadin Grid component.
| [ReviewsList.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/reviews/ReviewsList.kt) | An example view which demoes the possibility of the `Grid` to act as a scrolling vertical list component lazily-loading items as they scroll into view (akin to Android `ListView`). This is achieved by simply having one-column grid with a `ComponentRenderer`.
| [ConfirmationDialog.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/ConfirmationDialog.kt) | An example of a Yes-No dialog built entirely server-side.
| [AbstractEditorDialog.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/AbstractEditorDialog.kt), [CategoryEditorDialog.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/categories/CategoryEditorDialog.kt), [ReviewEditorDialog.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/reviews/ReviewEditorDialog.kt) | Forms editing particular database entities, implemented as a dialogs.
| [Toolbar.kt](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/Toolbar.kt) | An example of a reusable component
| [backend/](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/backend) | Demonstrates the use of the [VoK-ORM](https://github.com/mvysny/vok-orm) framework to represent database rows as objects
| [RestService.kt/](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/backend/RestService.kt) | Demonstrates the possibility of having REST endpoints. See the class sources for details on how to access those endpoints.
| [DemoData.kt/](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/backend/DemoData.kt) | Pre-populates the database with some example data.
| [Category.kt/](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/backend/Category.kt), [Review.kt/](src/main/kotlin/com/vaadin/starter/beveragebuddy/ui/backend/Review.kt) | Two entities. Category simply lists a list of beverage categories such as 'Beer'. Review lists reviews made for a particular beverage; it references the beverage category as a foreign key into the Category table.

## More Documentation

For Vaadin documentation for Java users, see [Vaadin.com/docs](https://vaadin.com/docs/v14/flow/Overview.html).

For Vaadin-on-Kotlin documentation, head to [Vaadin-on-Kotlin](http://vaadinonkotlin.eu).

# Development with Intellij IDEA Ultimate

The easiest way (and the recommended way) to develop VoK-based web applications is to use Intellij IDEA Ultimate.
It includes support for launching your project in any servlet container (Tomcat is recommended)
and allows you to debug the code, modify the code and hot-redeploy the code into the running Tomcat
instance, without having to restart Tomcat.

1. First, download Tomcat and register it into your Intellij IDEA properly: https://www.jetbrains.com/help/idea/2017.1/defining-application-servers-in-intellij-idea.html
2. Then just open this project in Intellij, simply by selecting `File / Open...` and click on the
   `build.gradle` file. When asked, select "Open as Project".
2. You can then create a launch configuration which will launch the `web` module as `exploded` in Tomcat with Intellij: just
   scroll to the end of this tutorial: https://kotlinlang.org/docs/tutorials/httpservlets.html
3. Start your newly created launch configuration in Debug mode. This way, you can modify the code
   and press `Ctrl+F9` to hot-redeploy the code. This only redeploys java code though, to
   redeploy resources just press `Ctrl+F10` and select "Update classes and resources"

## Using Intellij IDEA Community

Intellij Community is free and can be used for the development as well. To launch your app in IDEA Community, just click the rightmost
`Gradle` tool button, then navigate to `beverage-buddy-vok / Tasks / gretty / appRun`, right-click
`appRun` and select the first option "Run beverage-buddy-vok".
