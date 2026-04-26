package com.example.notesapp.repository

import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteDao
import com.example.notesapp.data.SettingsDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class NoteRepository(
    private val dao: NoteDao,
    private val settings: SettingsDataStore
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: Flow<List<Note>> = settings.sortOrder.flatMapLatest { order ->
        if (order == "TITLE") dao.getAllByTitle() else dao.getAllByDate()
    }

    fun search(query: String): Flow<List<Note>> = dao.search(query)

    suspend fun getById(id: Long): Note? = dao.getById(id)

    suspend fun insert(note: Note) = dao.insert(note)

    suspend fun update(note: Note) = dao.update(note)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}