// Configura repositórios e define os módulos do projeto

pluginManagement {
    // Repositórios onde o Gradle irá procurar plugins (Kotlin DSL recomenda gradlePluginPortal, Google, Maven Central)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // Evita que módulos usem repositórios não declarados aqui
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    // Repositórios de dependências (usados pelo projeto todo)
    repositories {
        google()
        mavenCentral()
    }
}

// Define o nome do projeto (podemos usar o nome do app sem espaços)
rootProject.name = "PsychonautWikiJournal"

// Inclui o módulo do aplicativo (subprojeto 'app')
include(":app") 