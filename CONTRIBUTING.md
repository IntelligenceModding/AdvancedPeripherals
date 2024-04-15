# Contributing to Advanced Peripherals
Any contributions to Advanced Peripherals are welcome. Supporting us with contributions speeds up the development process of Advanced Peripherals and the release of new versions. This document will guide you through the process of contributing to Advanced Peripherals.

If you want to contribute to the documentation, you can do this via the [documentation repository][docs-repo].
You can find the contribution guidelines in the readme of the documentation repository.

If you've any other questions, [ask the community via discord][discord] or [open an issue][new-issue].

## Table of Contents
- [Reporting issues](#reporting-issues)
- [Translations](#translations)
- [Setting up a development environment](#setting-up-a-development-environment)
- [Developing Advanced Peripherals](#developing-advanced-peripherals)
  - [Testing](#testing)
- [Writing documentation](#writing-documentation)

## Reporting issues
Reporting issues is the easiest way to contribute to Advanced Peripherals. Please read our [guideline](https://docs.intelligence-modding.de/guides/how_to_report/) on how to report issues

## Translations
Translations are managed through [crowdin], a service which allows anyone to contribute translations to Advanced Peripherals.

## Setting up a development environment

**PLEASE READ If you want to add features or fix bugs that exist in multiple versions, please make the changes for the oldest fully supported versions, which you can find [here](https://docs.advanced-peripherals.de/#version-support). That would be 1.19.2 at the time the article was written.**

If you want to contribute to Advanced Peripherals, you'll need to set up a development environment. This can be a tedious process for first-time contributors, but it's worth it in the long run. This guide will walk you through the process of setting up a development environment.

- Make sure you have the following installed:
  - Java Development Kit (JDK) installed. This can be downloaded from [Adoptium].
  - [Git](https://git-scm.com/).
  - If you want to work on documentation, I highly recommend an editor like [Visual Studio Code][vsc].

You can also download the [IntelliJ IDEA][idea] IDE, which is what we recommend if you work on some bigger changes. It's not required, but it does make things a lot easier. Intellij IDEA combines the first two steps and also manages gradle for you. So starting the game is as easy as clicking the run button.

- Download AP's source code:
  ```
  git clone https://github.com/IntelligenceModding/AdvancedPeripherals
  cd AdvancedPeripherals
  ```
  - If you're using IntelliJ IDEA, you can import the project by selecting `Get from version control` and then typing
    in the github repository url `https://github.com/IntelligenceModding/AdvancedPeripherals/`.

- Build AP with `./gradlew build` (or double clicking the build button in Intellij IDEA). This will be very slow the first time it runs, as it needs to download all the dependencies. 
- Subsequent builds will be much faster.

- You can now begin making changes to the project. If you're using IntelliJ IDEA, you can run the game by clicking on the `runClient` button in the gradle window.
  If you're not using IntelliJ IDEA, you can run the game by running `./gradlew runClient`.

If you're having trouble setting up a development environment, [ask the community via discord][discord] or [open an issue][new-issue].

## Developing Advanced Peripherals

### Testing
We currently do not have code tests for Advanced Peripherals.
However, we currently work on adding tests to Advanced Peripherals. You can contribute to this by adding tests to the project on the [dev/tests](https://github.com/IntelligenceModding/AdvancedPeripherals/tree/dev/tests) branch. If you want to contribute to this, please contact us on [discord][discord].


## Writing documentation
The documentation is written in markdown, build via mkdocs and is hosted on [docs.intelligence-modding.de][docs].
If you want to contribute to the documentation, you can do this via the [documentation repository][docs-repo].
Everything you need to know is stated in the readme of the documentation repository.



[new-issue]: https://github.com/IntelligenceModding/AdvancedPeripherals/issues/new/choose "Create a new issue"
[Adoptium]: https://adoptium.net/temurin/releases?version=17 "Download OpenJDK 17"
[vsc]: https://code.visualstudio.com/ "Visual Studio Code"
[docs-repo]: https://github.com/IntelligenceModding/Advanced-Peripherals-Documentation "Advanced Peripherals Documentation Repository"
[checkstyle]: https://checkstyle.org/
[idea]: https://www.jetbrains.com/de-de/products/compare/?product=idea&product=idea-ce "IntelliJ IDEA Community Edition"
[discord]: https://discord.intelligence-modding.de/ "Join the Discord"
[crowdin]: https://crowdin.com/project/advanced-peripherals "Advanced Peripherals on Crowdin"
[docs]: https://docs.intelligence-modding.de "Advanced Peripherals Documentation"
