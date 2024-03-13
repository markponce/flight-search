package com.example.flightsearch.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.search.SearchViewModel
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

object AppViewModelProvider {

//    val Factory = viewModelFactory {
//
//
//
//        initializer {
//            SearchViewModel(
//                flightSearchApplication().container.airportsRepository,
//                flightSearchApplication().container.favoritesRepository,
//                flightSearchApplication().userPreferencesRepository
//            )
//        }
//    }
}


/**
 * Extension function to queries for [Application] object and returns an instance of
 * [FlightSearchApplication].
 */
//fun CreationExtras.flightSearchApplication(): FlightSearchApplication =
//    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)