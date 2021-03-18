@file:Suppress("SpellCheckingInspection")

package com.paulo.pokemon.repository

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.network.PokedexService
import com.paulo.pokemon.MainCoroutinesRule
import com.paulo.pokemon.model.PokemonResponse
import com.paulo.pokemon.persistence.PokemonDao
import com.paulo.pokemon.utils.MockUtil.mockPokemonList
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.time.seconds

class MainRepositoryTest {

  private lateinit var repository: MainRepository
  private lateinit var client: PokedexClient
  private val service: PokedexService = mock()
  private val pokemonDao: PokemonDao = mock()

  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    client = PokedexClient(service)
    repository = MainRepository(client, pokemonDao)
  }

  @Test
  fun fetchPokemonListFromNetworkTest() = runBlocking {
    val mockData = PokemonResponse(count = 984, next = null, previous = null, results = mockPokemonList())
    whenever(pokemonDao.getPokemonList(page_ = 0)).thenReturn(emptyList())
    whenever(pokemonDao.getAllPokemonList(page_ = 0)).thenReturn(mockData.results)
    whenever(service.fetchPokemonList()).thenReturn(ApiResponse.of { Response.success(mockData) })

    repository.fetchPokemonList(
      page = 0,
      onStart = {},
      onComplete = {},
      onError = {}
    ).test(2.seconds) {
      val expectItem = expectItem()[0]
      assertEquals(expectItem.page, 0)
      assertEquals(expectItem.name, "bulbasaur")
      assertEquals(expectItem, mockPokemonList()[0])
      expectComplete()
    }

    verify(pokemonDao, atLeastOnce()).getPokemonList(page_ = 0)
    verify(service, atLeastOnce()).fetchPokemonList()
    verify(pokemonDao, atLeastOnce()).insertPokemonList(mockData.results)
    verifyNoMoreInteractions(service)
  }

  @Test
  fun fetchPokemonListFromDatabaseTest() = runBlocking {
    val mockData = PokemonResponse(count = 984, next = null, previous = null, results = mockPokemonList())
    whenever(pokemonDao.getPokemonList(page_ = 0)).thenReturn(mockData.results)
    whenever(pokemonDao.getAllPokemonList(page_ = 0)).thenReturn(mockData.results)

    val fetchedDataFlow = repository.fetchPokemonList(
      page = 0,
      onStart = {},
      onComplete = {},
      onError = {}
    ).test(2.seconds) {
      val expectItem = expectItem()[0]
      assertEquals(expectItem.page, 0)
      assertEquals(expectItem.name, "bulbasaur")
      assertEquals(expectItem, mockPokemonList()[0])
      expectComplete()
    }

    verify(pokemonDao, atLeastOnce()).getPokemonList(page_ = 0)
    verify(pokemonDao, atLeastOnce()).getAllPokemonList(page_ = 0)

    fetchedDataFlow.apply {
      // runBlocking should return Unit
    }
  }
}
