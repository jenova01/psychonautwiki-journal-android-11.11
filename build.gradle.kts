// Arquivo: build.gradle.kts (raiz do projeto)
// Declara plugins de nível raiz e tarefas comuns

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0") // Atualizado para 8.6.0
    }
}

plugins {
    // Plugin do Android (application) - versão 8.6.0 estável
    id("com.android.application") version "8.6.0" apply false
    // Plugin Kotlin Android - versão compatível com Compose (1.9.22)
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Plugin Hilt - Injeção de Dependência (Dagger Hilt 2.48)
    id("com.google.dagger.hilt.android") version "2.48" apply false
    // Plugin KAPT do Kotlin (mesma versão do Kotlin)
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
}

// Repositórios podem ser definidos aqui também, mas usamos settings.gradle.kts para isso

// Tarefa de limpeza comum
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}