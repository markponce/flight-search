package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flightsearch.ui.search.Flight


@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String = "",

    @ColumnInfo(name = "iata_code") val code: String = "",

    @ColumnInfo(name = "passengers") val passengerCount: Long = 0L
)


object FakeData {
    fun getAirports(): List<Airport> {
        return listOf<Airport>(
            Airport(
                id = 1,
                name = "Francisco Sá Carneiro Airport",
                code = "OPO",
                passengerCount = 5_053_134L
            ),
            Airport(
                id = 2, name = "Stockholm Arlanda Airport", code = "ARN", passengerCount = 7494765
            ),
            Airport(
                id = 3, name = "Warsaw Chopin Airport", code = "WAW", passengerCount = 18860000
            ),
            Airport(
                id = 4, name = "Marseille Provence Airport", code = "MRS", passengerCount = 10151743
            ),
//            Airport(id = 5, name = "Milan Bergamo Airport", code = "BGY", passengerCount = 3833063),
//            Airport(
//                id = 6,
//                name = "Vienna International Airport",
//                code = "VIE",
//                passengerCount = 7812938
//            ),
//            Airport(
//                id = 7,
//                name = "Sheremetyevo - A.S. Pushkin international airport",
//                code = "SVO",
//                passengerCount = 49933000
//            ),
//            Airport(id = 8, name = "Dublin Airport", code = "DUB", passengerCount = 32907673),
//            Airport(id = 9, name = "Sofia Airport", code = "SOF", passengerCount = 3364151),
//            Airport(id = 10, name = "Copenhagen Airport", code = "CPH", passengerCount = 9179654),
        )
    }

    fun getFlights(): List<Flight> {
        val airports = getAirports().filter { it -> it.id != 1 }

        return airports.map { airport ->
            Flight(
                depart = FakeData.getAirports()[0], arrive = airport
            )
        }
    }
}
