package com.example.flightsearch.di

import android.content.Context
import androidx.core.view.ViewCompat.ScrollIndicators
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.flightsearch.data.AirportDao
import com.example.flightsearch.data.AirportsRepository
import com.example.flightsearch.data.FavoriteDao
import com.example.flightsearch.data.FavoritesRepository
import com.example.flightsearch.data.FlightSearchDatabase
import com.example.flightsearch.data.OfflineAirportsRepository
import com.example.flightsearch.data.OfflineFavoritesRepository
import com.example.flightsearch.data.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.text.Typography.dagger

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {


    @Singleton
    @Provides
    fun provideFlightSearchDatabase(@ApplicationContext appContext: Context): FlightSearchDatabase {
        return Room.databaseBuilder(appContext, FlightSearchDatabase::class.java, "flight_search")
            /**
             * Setting this option in your app's database builder means that Room
             * permanently deletes all data from the tables in your database when it
             * attempts to perform a migration with no defined migration path.
             */
            .createFromAsset("database/flight_search.db")
//                    .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    fun provideAirportDao(flightSearchDatabase: FlightSearchDatabase): AirportDao {
        return flightSearchDatabase.airportDao()
    }

    @Provides
    fun provideFavoriteDao(flightSearchDatabase: FlightSearchDatabase): FavoriteDao {
        return flightSearchDatabase.favoriteDao()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class Repository {
    @Binds
    abstract fun bindOfflineFavoritesRepository(offlineFavoritesRepository: OfflineFavoritesRepository): FavoritesRepository

    @Binds
    abstract fun bindOfflineAirportsRepository(offlineAirportsRepository: OfflineAirportsRepository): AirportsRepository
}

//@Module
//@InstallIn(ViewModelComponent::class)
//object DataStore {
//    private const val SEARCH_PREFERENCE_NAME = "search_preference"
//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
//        name = "SEARCH_PREFERENCE_NAME"
//    )
//    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
//        return UserPreferencesRepository(context.dataStore)
//    }
//}