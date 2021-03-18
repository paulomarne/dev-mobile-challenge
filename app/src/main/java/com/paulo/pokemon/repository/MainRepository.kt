package com.paulo.pokemon.repository

import androidx.annotation.WorkerThread
import com.paulo.pokemon.mapper.ErrorResponseMapper
import com.paulo.pokemon.model.Pokemon
import com.paulo.pokemon.model.PokemonErrorResponse
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.persistence.PokemonDao
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MainRepository @Inject constructor(
  private val pokedexClient: PokedexClient,
  private val pokemonDao: PokemonDao
) : Repository {

  @WorkerThread
  fun fetchPokemonList(
    page: Int,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onError: (String?) -> Unit
  ) = flow {
    var pokemons = pokemonDao.getPokemonList(page)
    if (pokemons.isEmpty()) {
      /**
       * fetches a list of [Pokemon] from the network and getting [ApiResponse] asynchronously.
       * @see [suspendOnSuccess](https://github.com/paulo/sandwich#suspendonsuccess-suspendonerror-suspendonexception)
       */
      val response = pokedexClient.fetchPokemonList(page = page)
      response.suspendOnSuccess {
        data.whatIfNotNull { response ->
          pokemons = response.results
          pokemons.forEach { pokemon -> pokemon.page = page }
          pokemonDao.insertPokemonList(pokemons)
          emit(pokemonDao.getAllPokemonList(page))
        }
      }
        // handles the case when the API request gets an error response.
        // e.g., internal server error.
        .onError {
          /** maps the [ApiResponse.Failure.Error] to the [PokemonErrorResponse] using the mapper. */
          map(ErrorResponseMapper) { onError("[Code: $code]: $message") }
        }
        // handles the case when the API request gets an exception response.
        // e.g., network connection error.
        .onException { onError(message) }
    } else {
      emit(pokemonDao.getAllPokemonList(page))
    }
  }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
}
