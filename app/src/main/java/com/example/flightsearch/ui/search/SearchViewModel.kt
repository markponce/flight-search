package com.example.flightsearch.ui.search

import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportsRepository
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoritesRepository
import com.example.flightsearch.data.UserPreferencesRepository
import com.example.flightsearch.data.toFlight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val searchText: String = "",
    val airports: List<Airport> = emptyList(),
    val selectedAirport: Airport? = null,
    val flightsBySelectedAirport: List<Flight> = emptyList(),
    val favoriteFlights: List<Flight> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
//    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val airportsStateFlow: StateFlow<List<Airport>> = uiState.map {
        it.searchText
    }.debounce(500).flatMapLatest { searchText ->
        if (searchText.trim()
                .isNotEmpty()
        ) airportsRepository.filterAirportsByNameOrCode(searchText) else flowOf(
            emptyList()
        )
    }.stateIn(viewModelScope, WhileSubscribed(5_000), emptyList())

    private val favoriteFlightsStateFlow: StateFlow<List<Flight>> =
        favoritesRepository.getAllFavoriteRoutes().map { favoritesWithName ->
            favoritesWithName.map { favWithName ->
                favWithName.toFlight()
            }
        }.stateIn(viewModelScope, WhileSubscribed(5_000), emptyList())


    private val favoriteFlightsBySelectedAirport: StateFlow<List<Flight>> = uiState
        .map { it.selectedAirport }
        .filterNotNull()
        .flatMapLatest { airport ->
            favoritesRepository.getFavoriteRoutesByDepartureCode(airport.code).map { favs ->
                favs.map { fv -> fv.toFlight() }
            }
        }.stateIn(viewModelScope, WhileSubscribed(5_000), emptyList())


    private val flightsBySelectedAirport: StateFlow<List<Flight>> = uiState
        .map {
            it.selectedAirport
        }.filterNotNull()
        .flatMapLatest { airport ->
            airportsRepository.filterAirportsByNameOrCode("").map { airports ->
                airports.map {
                    Flight(depart = airport, arrive = it)
                }
            }
        }.stateIn(viewModelScope, WhileSubscribed(5_000), emptyList())


    init {

        viewModelScope.launch {
            airportsStateFlow.collect { it ->
                _uiState.update { uiState -> uiState.copy(airports = it) }
            }
        }

        viewModelScope.launch {
            flightsBySelectedAirport.collect { it ->
                _uiState.update { uiState -> uiState.copy(flightsBySelectedAirport = it) }
            }
        }

        viewModelScope.launch {
            favoriteFlightsStateFlow.collect { it ->
                _uiState.update { uiState ->
                    uiState.copy(favoriteFlights = it)
                }
            }
        }

        viewModelScope.launch {
            favoriteFlightsBySelectedAirport.collect { it ->
                _uiState.update { uiState ->
                    val new = uiState.flightsBySelectedAirport.map { f ->
                        val f2 = it.find { t -> t.arrive.code == f.arrive.code }
                        if (f2 != null) {
                            f.copy(isFavorite = true, favId = f2.favId)
                        } else {
                            f.copy(isFavorite = false, favId = 0)
                        }
                    }
                    uiState.copy(flightsBySelectedAirport = new)
                }
            }
        }
    }

    fun updateSelectedAirport(airport: Airport?) {
        _uiState.update { uiState -> uiState.copy(selectedAirport = airport) }
    }

    fun toggleAddToFavorites(flight: Flight) {
        viewModelScope.launch {
            if (flight.isFavorite) {
                val fav = flight.toFavorite();
                favoritesRepository.deleteFavoriteRoute(fav)
            } else {
                favoritesRepository.insertFavoriteRoute(favorite = flight.toFavorite())
            }
        }
    }

    init {
        viewModelScope.launch {
            airportsStateFlow.collect { airports ->
                // Update airports in the UI state
                _uiState.update { uiState ->
                    uiState.copy(airports = airports)
                }
            }
        }
    }

    fun updateSearchQuery(text: String) {
        _uiState.update { uiState ->
            uiState.copy(searchText = text, selectedAirport = null)
        }
    }
}

data class Flight(
    val depart: Airport = Airport(),
    val arrive: Airport = Airport(),
    val isFavorite: Boolean = false,
    val favId: Int = 0,
)

fun Flight.toFavorite(): Favorite =
    Favorite(departureCode = depart.code, destinationCode = arrive.code, id = favId)




