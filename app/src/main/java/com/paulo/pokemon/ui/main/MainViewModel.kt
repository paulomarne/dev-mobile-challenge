package com.paulo.pokemon.ui.main

import androidx.annotation.MainThread
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.skydoves.bindables.bindingProperty
import com.paulo.pokemon.base.LiveCoroutinesViewModel
import com.paulo.pokemon.model.Pokemon
import com.paulo.pokemon.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val mainRepository: MainRepository,
  private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

  private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)
  val pokemonListLiveData: LiveData<List<Pokemon>>

  @get:Bindable
  var toastMessage: String? by bindingProperty(null)
    private set

  @get:Bindable
  var isLoading: Boolean by bindingProperty(false)
    private set

  init {
    Timber.d("init MainViewModel")

    pokemonListLiveData = pokemonFetchingIndex.asLiveData().switchMap { page ->
      mainRepository.fetchPokemonList(
        page = page,
        onStart = { isLoading = true },
        onComplete = { isLoading = false },
        onError = { toastMessage = it }
      ).asLiveDataOnViewModelScope()
    }
  }

  @MainThread
  fun fetchNextPokemonList() {
    if (!isLoading) {
      pokemonFetchingIndex.value++
    }
  }
}
