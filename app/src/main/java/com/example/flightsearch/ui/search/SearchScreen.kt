package com.example.flightsearch.ui.search

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.FlightSearchApp
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.FakeData
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.toFlight
import com.example.flightsearch.ui.AppViewModelProvider
import com.example.flightsearch.ui.theme.FlightSearchTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(id = R.string.app_name))
            })
        },
    ) { innerPadding ->
        SearchScreenBody(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenBody(modifier: Modifier = Modifier) {

    val searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by searchViewModel.uiState.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = uiState.searchText,
            onValueChange = { searchViewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.selectedAirport == null) {
            LazyColumn(modifier = Modifier) {
                items(items = uiState.airports) { airport ->
                    Row(modifier = Modifier.clickable {
                        searchViewModel.updateSelectedAirport(airport)
                    }) {
                        Text(text = airport.code)
                        Text(text = " ")
                        Text(text = airport.name)
                    }
                }
            }
        }
//
        if (uiState.selectedAirport != null) {
            FlightCardList(
                title = "Flights from ${uiState.selectedAirport?.code}",
                flights = uiState.flightsBySelectedAirport,
                onFavoriteTap = searchViewModel::toggleAddToFavorites

            )
        }
//
        if (uiState.favoriteFlights.isNotEmpty() && uiState.searchText.isEmpty()) {
            FlightCardList(
                flights = uiState.favoriteFlights,
                onFavoriteTap = searchViewModel::toggleAddToFavorites
            )
        }
//
//
    }
}


fun Flight.formatFlightString(airport: Airport): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(airport.code)
        }
        append(" ")
        append(airport.name)
    }
}

@Composable
fun FlightCardList(
    flights: List<Flight>,
    modifier: Modifier = Modifier,
    title: String = "Favorite Routes",
    onFavoriteTap: (flight: Flight) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(text = title )
        LazyColumn(modifier = Modifier) {
            items(items = flights) { flight ->
                FlightCard(
                    flight = flight,
                    onFavoriteTap = onFavoriteTap,
//                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }

}

@Composable
fun FlightCard(
    flight: Flight,
    modifier: Modifier = Modifier,
    onFavoriteTap: (flight: Flight) -> Unit = {}
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "DEPART" + " " + flight.favId)
                Text(text = flight.formatFlightString(flight.depart))
                Text(text = "ARRIVE")
                Text(text = flight.formatFlightString(flight.arrive))
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (flight.isFavorite) Color(0xFF934B01) else Color(0xFF74777E),
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
//                    Toast.makeText(context, "click!!", Toast.LENGTH_SHORT).show()
                        onFavoriteTap(flight)
                    }
            )

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FlightCardListPreview() {
    FlightSearchTheme {
        FlightCardList(
            flights = FakeData.getFlights(),
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
        )
    }
}

//InventoryList(
//itemList = itemList,
//onItemClick = { onItemClick(it.id) },
//modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
//)


@Preview(showBackground = false, showSystemUi = false)
@Composable
fun FlightCardPreview() {
    FlightSearchTheme {
        val airports = FakeData.getAirports()
        val flight = Flight(
            depart = airports[0], arrive = airports[1], isFavorite = true
        )
        FlightCard(
            flight = flight
        )
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun FlightSearchAppPreview() {
//    FlightSearchTheme {
//        // A surface container using the 'background' color from the theme
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            FlightSearchApp()
//        }
//    }
//}