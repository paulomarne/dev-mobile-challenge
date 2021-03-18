package com.paulo.pokemon.repository

import androidx.annotation.WorkerThread
import com.paulo.pokemon.mapper.ErrorResponseMapper
import com.paulo.pokemon.model.PokemonErrorResponse
import com.paulo.pokemon.model.PokemonInfo
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.persistence.PokemonInfoDao
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
import javax.inject.Inject

class DetailRepository @Inject constructor(
  private val pokedexClient: PokedexClient,
  private val pokemonInfoDao: PokemonInfoDao
) : Repository {

  @WorkerThread
  fun fetchPokemonInfo(
    name: String,
    onComplete: () -> Unit,
    onError: (String?) -> Unit
  ) = flow<PokemonInfo?> {
    val pokemonInfo = pokemonInfoDao.getPokemonInfo(name)
    if (pokemonInfo == null) {
      /**
       * fetches a [PokemonInfo] from the network and getting [ApiResponse] asynchronously.
       * @see [suspendOnSuccess](https://github.com/paulo/sandwich#suspendonsuccess-suspendonerror-suspendonexception)
       */
      val response = pokedexClient.fetchPokemonInfo(name = name)
      response.suspendOnSuccess {
        data.whatIfNotNull { response ->
          pokemonInfoDao.insertPokemonInfo(response)
          emit(response)
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
      emit(pokemonInfo)
    }
  }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
}
