package com.paulo.pokemon.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paulo.pokemon.MainCoroutinesRule
import com.paulo.pokemon.network.PokedexClient
import com.paulo.pokemon.network.PokedexService
import com.paulo.pokemon.persistence.PokemonDao
import com.paulo.pokemon.repository.MainRepository
import com.paulo.pokemon.ui.main.MainViewModel
import com.paulo.pokemon.utils.MockUtil
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.seconds

class MainViewModelTest {

  private lateinit var viewModel: MainViewModel
  private lateinit var mainRepository: MainRepository
  private val pokedexService: PokedexService = mock()
  private val pokdexClient: PokedexClient = PokedexClient(pokedexService)
  private val pokemonDao: PokemonDao = mock()

  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    mainRepository = MainRepository(pokdexClient, pokemonDao)
    viewModel = MainViewModel(mainRepository, SavedStateHandle())
  }

  @Test
  fun fetchPokemonListTest() = runBlocking {
    val mockData = MockUtil.mockPokemonList()
    whenever(pokemonDao.getPokemonList(page_ = 0)).thenReturn(mockData)
    whenever(pokemonDao.getAllPokemonList(page_ = 0)).thenReturn(mockData)

    val fetchedDataFlow = mainRepository.fetchPokemonList(
      page = 0,
      onStart = {},
      onComplete = {},
      onError = {}
    ).test(2.seconds) {
      val item = expectItem()
      Assert.assertEquals(item[0].page, 0)
      Assert.assertEquals(item[0].name, "bulbasaur")
      Assert.assertEquals(item, MockUtil.mockPokemonList())
      expectComplete()
    }

    viewModel.fetchNextPokemonList()

    verify(pokemonDao, atLeastOnce()).getPokemonList(page_ = 0)

    fetchedDataFlow.apply {
      // runBlocking should return Unit
    }
  }
}
