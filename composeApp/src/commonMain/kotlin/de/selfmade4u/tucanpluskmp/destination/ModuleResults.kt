package de.selfmade4u.tucanpluskmp.destination

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import de.selfmade4u.tucanpluskmp.AppDatabase
import de.selfmade4u.tucanpluskmp.DetailedDrawerExample
import de.selfmade4u.tucanpluskmp.FakeDataStore
import de.selfmade4u.tucanpluskmp.MyLoadingIndicator
import de.selfmade4u.tucanpluskmp.Notifier
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TucanUrl
import de.selfmade4u.tucanpluskmp.connector.AuthenticatedResponse
import de.selfmade4u.tucanpluskmp.connector.ModuleGrade
import de.selfmade4u.tucanpluskmp.connector.Semester
import de.selfmade4u.tucanpluskmp.connector.Semesterauswahl
import de.selfmade4u.tucanpluskmp.database.ModuleResultEntity
import de.selfmade4u.tucanpluskmp.database.ModuleResults
import de.selfmade4u.tucanpluskmp.database.getCached
import de.selfmade4u.tucanpluskmp.database.refreshModuleResults
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import tucanpluskmp.composeapp.generated.resources.Res
import tucanpluskmp.composeapp.generated.resources.menu_24px

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModuleResultsComposable(backStack: NavBackStack<NavKey> = NavBackStack(), dataStore: DataStore<Settings?> = FakeDataStore, database: AppDatabase, isLoading: MutableState<Boolean> = mutableStateOf(false)) {
    val notifier: Notifier = koinInject();
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (getCached(database).first() == null) {
            refreshModuleResults(notifier, dataStore, database)
        }
    }
    val modules by getCached(database).collectAsStateWithLifecycle(null)
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    DetailedDrawerExample(backStack, "Modulergebnisse") { innerPadding ->
        PullToRefreshBox(isRefreshing, onRefresh = {
            isRefreshing = true
            scope.launch {
                refreshModuleResults(notifier, dataStore, database)
                isRefreshing = false
            }
        }, state = state, indicator = {
            MyLoadingIndicator(state, isRefreshing)
        }, modifier = Modifier.padding(innerPadding)) {
            RenderModuleResults(modules)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun RenderModuleResults(modules: ModuleResults?) {
    Column(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        //LongBasicDropdownMenu()
        when (val value = modules) {
            null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = "Loading" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularWavyProgressIndicator() }
            }
            value -> {
                value.moduleResults.forEach { module ->
                    key(module.id) {
                        ModuleComposable(module)
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
fun ModuleComposable(
    module: ModuleResultEntity = ModuleResultEntity(
        42,
        Semesterauswahl(1, 2025, Semester.Wintersemester),
        "id",
        "Tin one ewfwf wefwe ewfw efw efwe wfwe fewfwe fweline",
        ModuleGrade.NOCH_NICHT_GESETZT,
        1,
        TucanUrl.RESULTDETAILS(42),
        TucanUrl.GRADEOVERVIEWModule(42)
    )
) {
    // https://developer.android.com/develop/ui/compose/layouts/basics
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(module.name)
            Text(module.id, fontSize = 10.sp, color = Color.Gray)
        }
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.End) {
            Text("${module.credits} CP")
            Text(module.grade.stringified)
        }
    }
}
