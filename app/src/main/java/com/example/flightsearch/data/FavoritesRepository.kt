package com.example.flightsearch.data

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getAllFavoriteRoutes(): Flow<List<FavoriteWithName>>

    fun getFavoriteRoutesByDepartureCode(code: String): Flow<List<Favorite>>

    suspend fun insertFavoriteRoute(favorite: Favorite)

    suspend fun deleteFavoriteRoute(departureCode: String, destinationCode: String)
}