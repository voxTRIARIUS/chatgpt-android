/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

/*
* Designed and developed by 2022 skydoves (Jaewoong Eum)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
plugins {
  id("skydoves.android.application")
  id("skydoves.android.application.compose")
  id("skydoves.android.hilt")
  id("skydoves.spotless")
  id("kotlin-parcelize")
  id("dagger.hilt.android.plugin")
}

val useReleaseKeystore = rootProject.file("release/release-key.jks").exists()

android {
  namespace = "com.skydoves.chatgpt"
  compileSdk = Configurations.compileSdk

  defaultConfig {
    applicationId = "com.skydoves.chatgpt"
    minSdk = Configurations.minSdk
    targetSdk = Configurations.targetSdk
    versionCode = Configurations.versionCode
    versionName = Configurations.versionName
  }

  packagingOptions {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }

  signingConfigs {
    create("release") {
      if (useReleaseKeystore) {
        storeFile = rootProject.file("release/release-key.jks")
        storePassword = propOrDef("RELEASE_KEYSTORE_PWD", "")
        keyAlias = "getstream"
        keyPassword = propOrDef("RELEASE_KEY_PWD", "")
      }
    }
  }

  buildTypes {
    release {
      signingConfig = signingConfigs["release"]
      isShrinkResources = true
      isMinifyEnabled = true
      proguardFiles("proguard-rules.pro")
    }

    create("benchmark") {
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
      proguardFiles("benchmark-rules.pro")
    }
  }
}

dependencies {
  // core modules
  implementation(project(":core-designsystem"))
  implementation(project(":core-navigation"))
  implementation(project(":core-data"))

  // feature modules
  implementation(project(":feature-chat"))
  implementation(project(":feature-login"))

  // material
  implementation(libs.androidx.appcompat)

  // compose
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.constraintlayout)

  // jetpack
  implementation(libs.androidx.startup)
  implementation(libs.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  kapt(libs.hilt.compiler)

  // image loading
  implementation(libs.landscapist.glide)

  // logger
  implementation(libs.stream.log)

  // firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.crashlytics)
}

if (file("google-services.json").exists()) {
  apply(plugin = libs.plugins.gms.googleServices.get().pluginId)
  apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
  @Suppress("UNCHECKED_CAST")
  val propertyValue = project.properties[propertyName] as T?
  return propertyValue ?: defaultValue
}