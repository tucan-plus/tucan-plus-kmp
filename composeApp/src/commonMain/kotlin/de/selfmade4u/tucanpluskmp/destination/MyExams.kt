package de.selfmade4u.tucanpluskmp.destination

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import de.selfmade4u.tucanpluskmp.AppDatabase
import de.selfmade4u.tucanpluskmp.DetailedDrawerExample
import de.selfmade4u.tucanpluskmp.FakeDataStore
import de.selfmade4u.tucanpluskmp.MyLoadingIndicator
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TucanUrl
import de.selfmade4u.tucanpluskmp.connector.Semester
import de.selfmade4u.tucanpluskmp.connector.Semesterauswahl
import de.selfmade4u.tucanpluskmp.data.MyExams
import de.selfmade4u.tucanpluskmp.database.getCached
import de.selfmade4u.tucanpluskmp.database.refreshModuleResults
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyExamsComposable(backStack: NavBackStack<NavKey> = NavBackStack(), dataStore: DataStore<Settings?> = FakeDataStore, database: AppDatabase, isLoading: MutableState<Boolean> = mutableStateOf(false)) {
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (MyExams.getCached(database).first() == null) {
            MyExams.refresh(dataStore, database)
        }
    }
    val modules by MyExams.getCached(database).collectAsStateWithLifecycle(null)
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    DetailedDrawerExample(backStack, "Meine Prüfungen") { innerPadding ->
        PullToRefreshBox(isRefreshing, onRefresh = {
            isRefreshing = true
            scope.launch {
                MyExams.refresh(dataStore, database)
                isRefreshing = false
            }
        }, state = state, indicator = {
            MyLoadingIndicator(state, isRefreshing)
        }, modifier = Modifier.padding(innerPadding)) {
            RenderMyExams(modules)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun RenderMyExams(exams: List<MyExams.MyExam>?) {
    Column(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        //LongBasicDropdownMenu()
        when (exams) {
            null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = "Loading" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularWavyProgressIndicator() }
            }
            exams -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    exams.forEach { exam ->
                        key(exam.id) {
                            MyExamComposable(exam)
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
fun MyExamComposable(
    exam: MyExams.MyExam = MyExams.MyExam(
        "42",
        "id",
        "Tin one ewfwf wefwe ewfw efw efwe wfwe fewfwe fweline",
        Semesterauswahl(1, 2025, Semester.Wintersemester),
        TucanUrl.COURSEDETAILS(42, 42),
        TucanUrl.MODULEDETAILS(42),
    "date",
    )
) {
    // https://developer.android.com/develop/ui/compose/layouts/basics
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(exam.name)
            Text(exam.date)
        }
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.End) {
            Text(exam.id, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
