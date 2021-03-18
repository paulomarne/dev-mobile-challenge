@file:Suppress("SpellCheckingInspection")

package com.paulo.pokemon.repository

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.paulo.pokemon.MainCoroutinesRule
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.network.PokedexService
import com.paulo.pokemon.persistence.PokemonInfoDao
import com.paulo.pokemon.utils.MockUtil.mockPokemonInfo
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.time.seconds

class DetailRepositoryTest {

  private lateinit var repository: DetailRepository
  private lateinit var client: PokedexClient
  private val service: PokedexService = mock()
  private val pokemonInfoDao: PokemonInfoDao = mock()

  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    client = PokedexClient(service)
    repository = DetailRepository(client, pokemonInfoDao)
  }

  @Test
  fun fetchPokemonInfoFromNetworkTest() = runBlocking {
    val mockData = mockPokemonInfo()
    whenever(pokemonInfoDao.getPokemonInfo(name_ = "bulbasaur")).thenReturn(null)
    whenever(service.fetchPokemonInfo(name = "bulbasaur")).thenReturn(ApiResponse.of { Response.success(mockData) })

    repository.fetchPokemonInfo(name = "bulbasaur", onComplete = {}, onError = {}).test {
      val expectItem = requireNotNull(expectItem())
      assertEquals(expectItem.id, mockData.id)
      assertEquals(expectItem.name, mockData.name)
      assertEquals(expectItem, mockData)
      expectComplete()
    }

    verify(pokemonInfoDao, atLeastOnce()).getPokemonInfo(name_ = "bulbasaur")
    verify(service, atLeastOnce()).fetchPokemonInfo(name = "bulbasaur")
    verify(pokemonInfoDao, atLeastOnce()).insertPokemonInfo(mockData)
    verifyNoMoreInteractions(service)
  }

  @Test
  fun fetchPokemonInfoFromDatabaseTest() = runBlocking {
    val mockData = mockPokemonInfo()
    whenever(pokemonInfoDao.getPokemonInfo(name_ = "bulbasaur")).thenReturn(mockData)
    whenever(service.fetchPokemonInfo(name = "bulbasaur")).thenReturn(ApiResponse.of { Response.success(mockData) })

    repository.fetchPokemonInfo(name = "bulbasaur", onComplete = {}, onError = {}).test(5.seconds) {
      val expectItem = requireNotNull(expectItem())
      assertEquals(expectItem.id, mockData.id)
      assertEquals(expectItem.name, mockData.name)
      assertEquals(expectItem, mockData)
      expectComplete()
    }

    verify(pokemonInfoDao, atLeastOnce()).getPokemonInfo(name_ = "bulbasaur")
    verifyNoMoreInteractions(service)
  }
}
