import com.ncorti.ktfmt.gradle.TrailingCommaManagementStrategy
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.FailOnSeverity
import org.gradle.kotlin.dsl.withType

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.androidx.room3)
  alias(libs.plugins.koin.compiler)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktfmt)
}

android {
  namespace = "com.kronos.nuzlink"
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    applicationId = "com.kronos.nuzlink"
    minSdk = 31
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      optimization {
        enable = true
        packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
      }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
  }

  testOptions { unitTests.all { it.useJUnitPlatform() } }
}

kotlin {
  compilerOptions {
    languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
  }
}

room3 { schemaDirectory("$projectDir/schemas") }

detekt {
  config.setFrom("$rootDir/detekt.yml")
  buildUponDefaultConfig = true
  allRules = false
  source.setFrom("src/main/kotlin", "src/main/java")
  failOnSeverity = FailOnSeverity.Error
}

ktfmt {
  googleStyle()
  removeUnusedImports = true
  maxWidth = 140
  trailingCommaManagementStrategy = TrailingCommaManagementStrategy.COMPLETE
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

tasks.withType<Detekt>().configureEach {
  exclude("**/generated/**")
  exclude("**/test/**")
  exclude("**/androidTest/**")
}

dependencies {
  // AndroidX
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.activity.compose)

  // Compose
  implementation(platform(libs.compose.bom))
  implementation(libs.bundles.compose)

  // Nav3
  implementation(libs.bundles.nav3)

  // Koin
  implementation(platform(libs.koin.bom))
  implementation(libs.bundles.koin)

  // Room
  implementation(libs.bundles.room3)
  ksp(libs.androidx.room3.compiler)

  // Utils
  implementation(libs.pokekotlin)
  implementation(libs.bundles.kotlinx.serialization)

  // Detekt
  detektPlugins(libs.bundles.detekt.plugins)

  // Testing
  testImplementation(libs.junit)
  testImplementation(libs.bundles.kotest)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.room3.testing)
  androidTestImplementation(libs.androidx.junit)
  debugImplementation(libs.compose.ui.test.manifest)
  debugImplementation(libs.compose.ui.tooling)
}
