package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("Select * from airport where name like '%' || :queryString || '%' or iata_code like  '%' || :queryString || '%'")
    fun filterAirportsByNameOrCode(queryString: String): Flow<List<Airport>>
}