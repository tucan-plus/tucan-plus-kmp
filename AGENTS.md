You can read specified files using tool get_file_text_by_path.
You can use tool replace_text_in_file to modify files.

Write an extractor in `composeApp/src/commonMain/kotlin/de/selfmade4u/tucanpluskmp/connector/ExamResultsConnector.kt` for the files in `composeApp/src/commonTest/resources/exam-results` e.g. `composeApp/src/commonTest/resources/exam-results/000000015086000.html` similar to the existing extractor `composeApp/src/commonMain/kotlin/de/selfmade4u/tucanpluskmp/connector/ModuleResultsConnector.kt` and their files in `composeApp/src/commonTest/resources/module-results` e.g. `composeApp/src/commonTest/resources/module-results/000000015086000.html`

You can test whether your code compiles with the tool build_project. You can get errors with tool get_file_problems.

You can run the test by executing the execute_run_configuration tool with the GeneratedExamResultsTest run configuration to verify your implementation. The test implementation is in `composeApp/src/commonTest/kotlin/de/selfmade4u/tucanpluskmp/GeneratedExamResultsTest.kt`