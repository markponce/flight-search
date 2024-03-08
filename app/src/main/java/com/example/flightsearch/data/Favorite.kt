package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.flightsearch.ui.search.Flight

@Entity("favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "departure_code")
    val departureCode: String = "",

    @ColumnInfo(name = "destination_code")
    val destinationCode: String = "",
)


fun Favorite.toFlight(): Flight {
    return Flight(
        depart = Airport(code = departureCode),
        arrive = Airport(code = destinationCode),
        isFavorite = true,
        favId = id
    )
}


data class FavoriteWithName(
    val id: Int = 0,
    @ColumnInfo(name = "departure_code")
    val departureCode: String = "",
    val departureName: String = "",
    @ColumnInfo(name = "destination_code")
    val destinationCode: String = "",
    val destinationName: String = "",
)

fun FavoriteWithName.toFlight(): Flight {
    return Flight(
        depart = Airport(code = departureCode, name = departureName),
        arrive = Airport(code = destinationCode, name = destinationName),
        isFavorite = true,
        favId = id,
    )
}