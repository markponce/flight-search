package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFavoritesRepository(private val favoriteDao: FavoriteDao): FavoritesRepository {
    override fun getAllFavoriteRoutes(): Flow<List<FavoriteWithName>> = favoriteDao.getAllFavoriteRoutes()

    override fun getFavoriteRoutesByDepartureCode(code: String): Flow<List<Favorite>>   =favoriteDao.getFavoriteRoutesByDepartureCode(code)
    override suspend fun insertFavoriteRoute(favorite: Favorite)  = favoriteDao.insertFavoriteRoute(favorite)
    override suspend fun deleteFavoriteRoute(departureCode: String, destinationCode: String) = favoriteDao.deleteFavoriteRoute(departureCode, destinationCode)
}