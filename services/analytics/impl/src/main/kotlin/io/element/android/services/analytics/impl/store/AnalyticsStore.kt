/*
 * Copyright (c) 2021 New Vector Ltd
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

package io.element.android.services.analytics.impl.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Also accessed via reflection by the instrumentation tests @see [im.vector.app.ClearCurrentSessionRule].
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vector_analytics")

/**
 * Local storage for:
 * - user consent (Boolean);
 * - did ask user consent (Boolean);
 * - analytics Id (String).
 */
interface AnalyticsStore {
    val userConsentFlow: Flow<Boolean>
    val didAskUserConsentFlow: Flow<Boolean>
    val analyticsIdFlow: Flow<String>
    suspend fun setUserConsent(newUserConsent: Boolean)
    suspend fun setDidAskUserConsent(newValue: Boolean = true)
    suspend fun setAnalyticsId(newAnalyticsId: String)
    suspend fun reset()
}

@ContributesBinding(AppScope::class)
class DefaultAnalyticsStore @Inject constructor(
    @ApplicationContext private val context: Context
) : AnalyticsStore {
    private val userConsent = booleanPreferencesKey("user_consent")
    private val didAskUserConsent = booleanPreferencesKey("did_ask_user_consent")
    private val analyticsId = stringPreferencesKey("analytics_id")

    override val userConsentFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[userConsent].orFalse() }
        .distinctUntilChanged()

    override val didAskUserConsentFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[didAskUserConsent].orFalse() }
        .distinctUntilChanged()

    override val analyticsIdFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[analyticsId].orEmpty() }
        .distinctUntilChanged()

    override suspend fun setUserConsent(newUserConsent: Boolean) {
        context.dataStore.edit { settings ->
            settings[userConsent] = newUserConsent
        }
    }

    override suspend fun setDidAskUserConsent(newValue: Boolean) {
        context.dataStore.edit { settings ->
            settings[didAskUserConsent] = newValue
        }
    }

    override suspend fun setAnalyticsId(newAnalyticsId: String) {
        context.dataStore.edit { settings ->
            settings[analyticsId] = newAnalyticsId
        }
    }

    override suspend fun reset() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
