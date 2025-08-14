package com.example.gradientgradetracker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UiKeys {
    val SELECTED_TAB = intPreferencesKey("selected_tab")
    val LAST_ROUTE = stringPreferencesKey("last_route")
    val EXPANDED_SUBJECTS = stringPreferencesKey("expanded_subjects_json")
    val SELECTED_PERIOD_BY_SUBJECT = stringPreferencesKey("selected_period_map_json")
}

data class UiState(
    val selectedTab: Int,
    val lastRoute: String,
    val expandedSubjectIdsJson: String,
    val selectedPeriodBySubjectJson: String
)

class UiStateStore(private val ds: DataStore<Preferences>) {
    val uiFlow: Flow<UiState> = ds.data.map { p ->
        UiState(
            selectedTab = p[UiKeys.SELECTED_TAB] ?: 0,
            lastRoute = p[UiKeys.LAST_ROUTE] ?: "Home",
            expandedSubjectIdsJson = p[UiKeys.EXPANDED_SUBJECTS] ?: "[]",
            selectedPeriodBySubjectJson = p[UiKeys.SELECTED_PERIOD_BY_SUBJECT] ?: "{}"
        )
    }

    suspend fun setSelectedTab(i: Int) = ds.edit { it[UiKeys.SELECTED_TAB] = i }
    suspend fun setLastRoute(route: String) = ds.edit { it[UiKeys.LAST_ROUTE] = route }
    suspend fun setExpandedSubjects(json: String) = ds.edit { it[UiKeys.EXPANDED_SUBJECTS] = json }
    suspend fun setSelectedPeriods(json: String) = ds.edit { it[UiKeys.SELECTED_PERIOD_BY_SUBJECT] = json }
}


