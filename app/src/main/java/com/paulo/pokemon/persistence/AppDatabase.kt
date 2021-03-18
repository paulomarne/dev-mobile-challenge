package com.paulo.pokemon.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paulo.pokemon.model.Pokemon
import com.paulo.pokemon.model.PokemonInfo

@Database(entities = [Pokemon::class, PokemonInfo::class], version = 1, exportSchema = true)
@TypeConverters(value = [TypeResponseConverter::class])
abstract class AppDatabase : RoomDatabase() {

  abstract fun pokemonDao(): PokemonDao
  abstract fun pokemonInfoDao(): PokemonInfoDao
}
