package com.paulo.pokemon.di

import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.persistence.PokemonDao
import com.paulo.pokemon.persistence.PokemonInfoDao
import com.paulo.pokemon.repository.DetailRepository
import com.paulo.pokemon.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

  @Provides
  @ViewModelScoped
  fun provideMainRepository(
    pokedexClient: PokedexClient,
    pokemonDao: PokemonDao
  ): MainRepository {
    return MainRepository(pokedexClient, pokemonDao)
  }

  @Provides
  @ViewModelScoped
  fun provideDetailRepository(
    pokedexClient: PokedexClient,
    pokemonInfoDao: PokemonInfoDao
  ): DetailRepository {
    return DetailRepository(pokedexClient, pokemonInfoDao)
  }
}
