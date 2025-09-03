package com.example.solidconversion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.solidconversion.model.BlogItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.skCompiler.generatedModel.BlogItemRemoteDataSource
import org.skCompiler.generatedModel.BlogItemRepository


class BlogItemViewModel(
    private val repository: BlogItemRepository,
    private val remoteDataSource: BlogItemRemoteDataSource
): ViewModel() {

    private var _allItems: MutableStateFlow<List<BlogItem>> = MutableStateFlow(listOf())
    val allItems: StateFlow<List<BlogItem>> get() = _allItems

    private val _BlogItem = MutableStateFlow<BlogItem?>(null)
    val BlogItem: StateFlow<BlogItem?> = _BlogItem

    init {
        this.viewModelScope.launch {
            val newList = mutableListOf<BlogItem>()
            try{
                if (remoteDataSource.remoteAccessible()) {
                    newList += remoteDataSource.fetchRemoteItemList()
                }
                repository.allBlogItemsAsFlow.collect { list ->
                    newList += list
                }
                _allItems.value = newList.distinctBy { it.id }
            } catch (e: NullPointerException) {
                Log.e("BlogViewModel", "Error loading RDF model: ${e.message}")
                _allItems.value = emptyList()
            } catch (e: Exception) {
                Log.e("BlogViewModel", "Unexpected error: ${e.message}")
                _allItems.value = emptyList()
            }
        }
    }

    fun remoteIsAvailable(): Boolean {
        return remoteDataSource.remoteAccessible()
    }

    fun setRemoteRepositoryData(
        accessToken: String,
        signingJwk: String,
        webId: String,
        expirationTime: Long,
    ) {
        remoteDataSource.signingJwk = signingJwk
        remoteDataSource.webId = webId
        remoteDataSource.expirationTime = expirationTime
        remoteDataSource.accessToken = accessToken
    }

    fun updateWebId(webId: String) {
        viewModelScope.launch {
            // Keep the new webId (or reset if bad)
            try {
                repository.insertWebId(webId)
            } catch (e: Exception) {
                repository.resetModel()
            }

            // Fetch remote snapshot
            val remote = if (remoteDataSource.remoteAccessible())
                remoteDataSource.fetchRemoteItemList()
            else
                emptyList()

            // Fetch snapshot of local items
            val local = repository.allBlogItemsAsFlow.firstOrNull() ?: emptyList()

            // Merge & de-duplicate
            val merged = (remote + local)
                .distinctBy { it.id }

            // Overwrite local cache so dupes don't appear visually
            repository.overwriteModelWithList(merged)

            // Update UI
            _allItems.value = merged

            // Push merged back to remote
            remoteDataSource.updateRemoteItemList(merged)
        }
    }


    suspend fun fetchRemoteList() {
        // Pull remote and local snapshots
        val remote = remoteDataSource.fetchRemoteItemList()
        val local = repository.allBlogItemsAsFlow.firstOrNull() ?: emptyList()

        // Merge & de-duplicate
        val merged = (remote + local)
            .distinctBy { it.id }

        // Overwrite local cache so future reads never re-introduce dupes
        repository.overwriteModelWithList(merged)

        // Updates UI
        _allItems.value = merged
    }


    suspend fun insert(item: BlogItem) {
        val tempList = mutableListOf<BlogItem>()
        viewModelScope.launch {
            repository.insert(item)
            // not sure if this is the right way to do it...
            repository.allBlogItemsAsFlow.collect { list ->
                tempList += list
            }
        }
        viewModelScope.launch {
            _allItems.value = tempList
            remoteDataSource.updateRemoteItemList(tempList)
        }
    }

    suspend fun insertMany(list: List<BlogItem>) {
        viewModelScope.launch {
            repository.insertMany(list)
            repository.allBlogItemsAsFlow.collect { list ->
                _allItems.value = list
            }
        }
    }

    fun delete(item: BlogItem) {
        viewModelScope.launch {
            // Deletes blog item from local cache
            repository.deleteByUri(item.id)

            // Pulls updated cache list
            val remaining: List<BlogItem> =
                repository.allBlogItemsAsFlow.firstOrNull() ?: emptyList()

            // Updates list of current blog items
            _allItems.value = remaining

            // Sync that local list to the Pod
            remoteDataSource.updateRemoteItemList(remaining)
        }
    }


    suspend fun updateRemote() {
        viewModelScope.launch {
            repository.allBlogItemsAsFlow.collect { list ->
                _allItems.value = list
            }.also {
                remoteDataSource.updateRemoteItemList(_allItems.value)
            }
        }
    }

    suspend fun update(item: BlogItem) {
        withContext(Dispatchers.IO) {
            repository.update(item)
        }

        val list = repository.allBlogItemsAsFlow.first()

        withContext(Dispatchers.Main) {
            _allItems.value = list
        }

        withContext(Dispatchers.IO) {
            remoteDataSource.updateRemoteItemList(list)
        }
    }


    private fun merge(remote: List<BlogItem>, local: List<BlogItem>): List<BlogItem> =
        (remote + local).distinctBy { it.id }

    fun loadBlogById(id: String) {
        viewModelScope.launch {
            // Try local-first
            repository.getBlogItemLiveData(id).firstOrNull()?.let {
                _BlogItem.value = it
                return@launch
            }

            // Fallback to in-memory merged list
            val fromMerged = _allItems.value.find { it.id == id }
            if (fromMerged != null) {
                _BlogItem.value = fromMerged
            } else if (remoteDataSource.remoteAccessible()) {
                // as a last resort, pull fresh remote list into merged
                val remote = remoteDataSource.fetchRemoteItemList()
                val local = repository.allBlogItemsAsFlow.firstOrNull() ?: emptyList()
                val merged = merge(remote, local)
                _allItems.value = merged
                _BlogItem.value = merged.find { it.id == id }
            } else {
                _BlogItem.value = null
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SolidConversionApplication)
                val itemRepository = application.repository
                val itemRemoteDataSource = BlogItemRemoteDataSource(externalScope = CoroutineScope(SupervisorJob() + Dispatchers.Default))
                BlogItemViewModel(itemRepository, itemRemoteDataSource)
            }
        }
    }
}