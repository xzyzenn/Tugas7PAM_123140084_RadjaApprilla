package com.example.notesapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteDatabase
import com.example.notesapp.data.SettingsDataStore
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val db = NoteDatabase.getInstance(app)
    private val settings = SettingsDataStore(app)
    private val repo = NoteRepository(db.noteDao(), settings)

    val isDarkTheme: StateFlow<Boolean> = settings.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val sortOrder: StateFlow<String> = settings.sortOrder
        .stateIn(viewModelScope, SharingStarted.Eagerly, "DATE")

    fun setDarkTheme(value: Boolean) = viewModelScope.launch {
        settings.setDarkTheme(value)
    }

    fun setSortOrder(value: String) = viewModelScope.launch {
        settings.setSortOrder(value)
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun setQuery(q: String) { _query.value = q }

    val notes: StateFlow<List<Note>> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) repo.notes else repo.search(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteNote(id: Long) = viewModelScope.launch {
        repo.deleteById(id)
    }

    suspend fun getNoteById(id: Long): Note? = repo.getById(id)

    suspend fun saveNote(id: Long, title: String, content: String) {
        val now = System.currentTimeMillis()
        if (id == -1L) {
            repo.insert(Note(title = title, content = content, createdAt = now, updatedAt = now))
        } else {
            val old = repo.getById(id) ?: return
            repo.update(old.copy(title = title, content = content, updatedAt = now))
        }
    }
}