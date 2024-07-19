/*
 * Copyright (c) 2024 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chat.schildi.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import chat.schildi.lib.preferences.ScPrefs
import chat.schildi.lib.preferences.value

object DynamicColorPreferences {
    val isDynamicColorEnabled: Boolean
        @Composable
        get() = ScPrefs.SC_DYNAMICCOLORS.value()
}

@Composable
fun scSemColorOverride(color: androidx.compose.ui.graphics.Color): androidx.compose.ui.graphics.Color? {
    val context = LocalContext.current
    val isLight = !isSystemInDarkTheme()

    return if (DynamicColorPreferences.isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicColorScheme = if (isLight) dynamicLightColorScheme(context) else dynamicDarkColorScheme(context)
        color
    } else {
        null
    }
}
//@Composable
//fun scSemColorOverride(colorScheme: ColorScheme): Color? {
//    return if (DynamicColorPreferences.isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        val context = LocalContext.current
//        val dynamicColorScheme = if (isSystemInDarkTheme()) {
//            dynamicDarkColorScheme(context)
//        } else {
//            dynamicLightColorScheme(context)
//        }
//        dynamicColorScheme()
//    } else {
//        null
//    }
//}

