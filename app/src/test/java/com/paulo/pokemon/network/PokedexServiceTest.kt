package com.paulo.pokemon.network

import com.paulo.pokemon.MainCoroutinesRule
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class PokedexServiceTest : ApiAbstract<PokedexService>() {

  private lateinit var service: PokedexService

  @ExperimentalCoroutinesApi
  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun initService() {
    service = createService(PokedexService::class.java)
  }

  @Throws(IOException::class)
  @Test
  fun fetchPokemonListFromNetworkTest() = runBlocking {
    enqueueResponse("/PokemonResponse.json")
    val response = service.fetchPokemonList()
    val responseBody = requireNotNull((response as ApiResponse.Success).data)
    mockWebServer.takeRequest()

    assertThat(responseBody.count, `is`(964))
    assertThat(responseBody.results[0].name, `is`("bulbasaur"))
    assertThat(responseBody.results[0].url, `is`("https://pokeapi.co/api/v2/pokemon/1/"))
  }

  @Throws(IOException::class)
  @Test
  fun fetchPokemonInfoFromNetworkTest() = runBlocking {
    enqueueResponse("/Bulbasaur.json")
    val response = service.fetchPokemonInfo("bulbasaur")
    val responseBody = requireNotNull((response as ApiResponse.Success).data)
    mockWebServer.takeRequest()

    assertThat(responseBody.id, `is`(1))
    assertThat(responseBody.name, `is`("bulbasaur"))
    assertThat(responseBody.height, `is`(7))
    assertThat(responseBody.weight, `is`(69))
    assertThat(responseBody.experience, `is`(64))
  }
}
