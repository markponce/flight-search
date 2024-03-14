package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineAirportsRepository @Inject  constructor(private val airportDao: AirportDao): AirportsRepository {
    override fun filterAirportsByNameOrCode(queryString: String): Flow<List<Airport>> = airportDao.filterAirportsByNameOrCode(queryString)
}