package com.paulo.pokemon.viewmodel

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paulo.pokemon.MainCoroutinesRule
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.network.PokedexService
import com.paulo.pokemon.persistence.PokemonInfoDao
import com.paulo.pokemon.repository.DetailRepository
import com.paulo.pokemon.ui.details.DetailViewModel
import com.paulo.pokemon.utils.MockUtil
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.seconds

class DetailViewModelTest {

  private lateinit var viewModel: DetailViewModel
  private lateinit var detailRepository: DetailRepository
  private val pokedexService: PokedexService = mock()
  private val pokdexClient: PokedexClient = PokedexClient(pokedexService)
  private val pokemonInfoDao: PokemonInfoDao = mock()

  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    detailRepository = DetailRepository(pokdexClient, pokemonInfoDao)
    viewModel = DetailViewModel(detailRepository, "bulbasaur")
  }

  @Test
  fun fetchPokemonInfoTest() = runBlocking {
    val mockData = MockUtil.mockPokemonInfo()
    whenever(pokemonInfoDao.getPokemonInfo(name_ = "bulbasaur")).thenReturn(mockData)

    val fetchedDataFlow = detailRepository.fetchPokemonInfo(
      name = "bulbasaur",
      onComplete = { },
      onError = { }
    ).test(2.seconds) {
      val item = requireNotNull(expectItem())
      Assert.assertEquals(item.id, mockData.id)
      Assert.assertEquals(item.name, mockData.name)
      Assert.assertEquals(item, mockData)
      expectComplete()
    }

    verify(pokemonInfoDao, atLeastOnce()).getPokemonInfo(name_ = "bulbasaur")

    fetchedDataFlow.apply {
      // runBlocking should return Unit
    }
  }
}
