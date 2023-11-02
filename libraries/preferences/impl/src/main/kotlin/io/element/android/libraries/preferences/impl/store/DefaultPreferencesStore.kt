/*
 * Copyright (c) 2023 New Vector Ltd
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

package io.element.android.libraries.preferences.impl.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import chat.schildi.lib.preferences.ScPreferencesStore
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.features.preferences.api.store.PreferencesStore
import io.element.android.libraries.core.bool.orTrue
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.core.meta.BuildType
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "elementx_preferences")

private val richTextEditorKey = booleanPreferencesKey("richTextEditor")
private val developerModeKey = booleanPreferencesKey("developerMode")
private val customElementCallBaseUrlKey = stringPreferencesKey("elementCallBaseUrl")

@ContributesBinding(AppScope::class)
class DefaultPreferencesStore @Inject constructor(
    @ApplicationContext context: Context,
    private val buildMeta: BuildMeta,
) : PreferencesStore {
    private val store = context.dataStore

    private val scPreferencesStore = ScPreferencesStore(context)

    override suspend fun setRichTextEditorEnabled(enabled: Boolean) {
        store.edit { prefs ->
            prefs[richTextEditorKey] = enabled
        }
    }

    override fun isRichTextEditorEnabledFlow(): Flow<Boolean> {
        return store.data.map { prefs ->
            // enabled by default
            prefs[richTextEditorKey].orTrue()
        }
    }

    override suspend fun setDeveloperModeEnabled(enabled: Boolean) {
        store.edit { prefs ->
            prefs[developerModeKey] = enabled
        }
    }

    override fun isDeveloperModeEnabledFlow(): Flow<Boolean> {
        return store.data.map { prefs ->
            // disabled by default on release and nightly, enabled by default on debug
            prefs[developerModeKey] ?: (buildMeta.buildType == BuildType.DEBUG)
        }
    }

    override fun getScPreferenceStore(): ScPreferencesStore = scPreferencesStore

    override suspend fun setCustomElementCallBaseUrl(string: String?) {
        store.edit { prefs ->
            if (string != null) {
                prefs[customElementCallBaseUrlKey] = string
            } else {
                prefs.remove(customElementCallBaseUrlKey)
            }
        }
    }

    override fun getCustomElementCallBaseUrlFlow(): Flow<String?> {
        return store.data.map { prefs ->
            prefs[customElementCallBaseUrlKey]
        }
    }

    override suspend fun reset() {
        store.edit { it.clear() }
        scPreferencesStore.reset()
    }
}
