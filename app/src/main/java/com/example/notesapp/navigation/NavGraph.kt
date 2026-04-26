package com.example.notesapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.notesapp.ui.*

@Composable
fun NavGraph(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val notes by viewModel.notes.collectAsState()
    val query by viewModel.query.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    NavHost(navController, startDestination = "list") {
        composable("list") {
            NoteListScreen(
                notes = notes,
                query = query,
                onQueryChange = viewModel::setQuery,
                onNoteClick = { navController.navigate("detail/$it") },
                onAddClick = { navController.navigate("detail/-1") },
                onDeleteClick = viewModel::deleteNote,
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable(
            "detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { back ->
            val noteId = back.arguments?.getLong("noteId") ?: -1L
            NoteDetailScreen(
                noteId = noteId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                isDarkTheme = isDark,
                sortOrder = sortOrder,
                onDarkThemeChange = { viewModel.setDarkTheme(it) },
                onSortOrderChange = { viewModel.setSortOrder(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}