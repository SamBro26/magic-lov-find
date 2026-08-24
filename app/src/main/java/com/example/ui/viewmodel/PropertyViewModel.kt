package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.PropertyItem
import com.example.data.repository.PropertyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FilterUiState(
    val activeTabIndex: Int = 0, // 0 = عقارات معروضة, 1 = طلبات البحث
    val searchQuery: String = "",
    val selectedWilaya: String = "",
    val selectedCommune: String = "",
    val selectedCategory: String = "",
    val selectedTransaction: String = "",
    val favoritesOnly: Boolean = false,
    val isFilterSheetOpen: Boolean = false,
    val isCreateAdDialogOpen: Boolean = false,
    val selectedPropertyForDetails: PropertyItem? = null
)

class PropertyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PropertyRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PropertyRepository(database.propertyDao())
    }

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val propertyList: StateFlow<List<PropertyItem>> = combine(
        _uiState
    ) { stateArray ->
        val state = stateArray[0]
        state
    }.flatMapLatest { state ->
        val isOffer = state.activeTabIndex == 0
        repository.getFilteredProperties(
            isOffer = isOffer,
            query = state.searchQuery,
            wilayaFilter = state.selectedWilaya,
            communeFilter = state.selectedCommune,
            categoryFilter = state.selectedCategory,
            transactionFilter = state.selectedTransaction
        )
    }.combine(_uiState) { list, state ->
        if (state.favoritesOnly) {
            list.filter { it.isFavorite }
        } else {
            list
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setTabIndex(index: Int) {
        _uiState.value = _uiState.value.copy(activeTabIndex = index)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setSelectedWilaya(wilaya: String) {
        _uiState.value = _uiState.value.copy(
            selectedWilaya = wilaya,
            selectedCommune = "" // reset commune if wilaya changes
        )
    }

    fun setSelectedCommune(commune: String) {
        _uiState.value = _uiState.value.copy(selectedCommune = commune)
    }

    fun setSelectedCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setSelectedTransaction(transaction: String) {
        _uiState.value = _uiState.value.copy(selectedTransaction = transaction)
    }

    fun toggleFavoritesOnly() {
        _uiState.value = _uiState.value.copy(favoritesOnly = !_uiState.value.favoritesOnly)
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedWilaya = "",
            selectedCommune = "",
            selectedCategory = "",
            selectedTransaction = "",
            favoritesOnly = false
        )
    }

    fun setFilterSheetOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterSheetOpen = isOpen)
    }

    fun setCreateAdDialogOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateAdDialogOpen = isOpen)
    }

    fun selectPropertyForDetails(property: PropertyItem?) {
        _uiState.value = _uiState.value.copy(selectedPropertyForDetails = property)
    }

    fun toggleFavorite(property: PropertyItem) {
        viewModelScope.launch {
            repository.toggleFavorite(property.id, property.isFavorite)
        }
    }

    fun createProperty(
        isOffer: Boolean,
        title: String,
        description: String,
        priceDzd: Long,
        priceCentimesDisplay: String,
        wilaya: String,
        commune: String,
        category: String,
        transactionType: String,
        rooms: String,
        area: Int,
        phone: String,
        publisherName: String,
        publisherType: String,
        featuresList: String
    ) {
        viewModelScope.launch {
            val newProperty = PropertyItem(
                isOffer = isOffer,
                title = title,
                description = description,
                priceDzd = priceDzd,
                priceDisplayCentimes = priceCentimesDisplay,
                wilaya = wilaya,
                commune = commune,
                category = category,
                transactionType = transactionType,
                rooms = rooms,
                areaSqMeters = area,
                phone = phone,
                publisherName = publisherName,
                publisherType = publisherType,
                featuresList = featuresList,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )
            repository.addProperty(newProperty)
            _uiState.value = _uiState.value.copy(isCreateAdDialogOpen = false)
        }
    }
}
