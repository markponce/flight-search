package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineAirportsRepository(private val airportDao: AirportDao): AirportsRepository {
    override fun filterAirportsByNameOrCode(queryString: String): Flow<List<Airport>> = airportDao.filterAirportsByNameOrCode(queryString)
}