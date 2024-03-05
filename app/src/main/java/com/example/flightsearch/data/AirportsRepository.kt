package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface AirportsRepository {
    fun filterAirportsByNameOrCode(queryString: String): Flow<List<Airport>>
}