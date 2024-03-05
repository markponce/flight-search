package com.example.flightsearch.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportsRepository
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteWithName
import com.example.flightsearch.data.FavoritesRepository
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            _searchQuery.value = userPreferencesRepository.querySearchString.first()
        }
    }

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport

    fun addToFavoriteRoutes(flight: Flight) {
        viewModelScope.launch {
            if (flight.isFavorite) {
                favoritesRepository.deleteFavoriteRoute(
                    departureCode = flight.depart.code,
                    destinationCode = flight.arrive.code
                )
            } else {
                favoritesRepository.insertFavoriteRoute(flight.toFavorite())
            }
        }
    }

    fun updateSelectedAirport(airport: Airport) {
        _selectedAirport.value = airport
    }

    fun updateSearchQuery(text: String) {
        _searchQuery.value = text
        _selectedAirport.value = null
    }

    val airports = _searchQuery
        .debounce(500)
        .flatMapLatest { searchQuery ->
            userPreferencesRepository.saveQuerySearchString(searchQuery)
            if (searchQuery.isNotEmpty()) {
                airportsRepository.filterAirportsByNameOrCode(searchQuery)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val allFavorites = favoritesRepository.getAllFavoriteRoutes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList<FavoriteWithName>()
    )

    val favoritesBySelectedAirport = _selectedAirport
        .filterNotNull()
        .flatMapLatest { selectedAirport ->
            favoritesRepository.getFavoriteRoutesByDepartureCode(selectedAirport.code)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList<Favorite>()
        )

    val flights = _selectedAirport
        .filterNotNull()
        .debounce(500) // Debounce for 300 milliseconds
        .flatMapLatest { selectedAirport ->
            airportsRepository.filterAirportsByNameOrCode("")
                .map { airports ->
                    airports
                        .filter { airport -> airport.code != selectedAirport.code }
                        .map { airport ->
                            Flight(depart = selectedAirport, arrive = airport)
                        }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList<Flight>()
        )
}

data class Flight(
    val depart: Airport = Airport(),
    val arrive: Airport = Airport(),
    val isFavorite: Boolean = false
)

fun Flight.toFavorite(): Favorite =
    Favorite(departureCode = depart.code, destinationCode = arrive.code)




