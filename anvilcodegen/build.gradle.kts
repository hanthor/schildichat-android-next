/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(projects.anvilannotations)
    api(libs.anvil.compiler.api)
    implementation(libs.anvil.compiler.utils)
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation(libs.dagger)
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.0")
}
