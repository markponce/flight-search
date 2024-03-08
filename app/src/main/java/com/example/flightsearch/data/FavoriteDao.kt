package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query(
        "Select " +
                "a.*," +
                "b.name departureName," +
                "c.name destinationName " +
                "from favorite a " +
                "left join airport b on b.iata_code = a.departure_code " +
                "left join airport c on c.iata_code = a.destination_code"
    )
    fun getAllFavoriteRoutes(): Flow<List<FavoriteWithName>>

    @Query("select * from favorite a where a.departure_code = :code")
    fun getFavoriteRoutesByDepartureCode(code: String): Flow<List<Favorite>>

    @Insert
    suspend fun insertFavoriteRoute(favorite: Favorite)

//    @Query("delete from favorite where departure_code = :departureCode and destination_code =:destinationCode")
    @Delete
    suspend fun deleteFavoriteRoute(favorite: Favorite)

}