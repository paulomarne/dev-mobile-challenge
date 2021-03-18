package com.paulo.pokemon.ui.details

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skydoves.bindables.bindingProperty
import com.paulo.pokemon.base.LiveCoroutinesViewModel
import com.paulo.pokemon.model.PokemonInfo
import com.paulo.pokemon.repository.DetailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

class DetailViewModel @AssistedInject constructor(
  detailRepository: DetailRepository,
  @Assisted private val pokemonName: String
) : LiveCoroutinesViewModel() {

  val pokemonInfoLiveData: LiveData<PokemonInfo?>

  @get:Bindable
  var toastMessage: String? by bindingProperty(null)
    private set

  @get:Bindable
  var isLoading: Boolean by bindingProperty(true)
    private set

  init {
    Timber.d("init DetailViewModel")

    pokemonInfoLiveData = detailRepository.fetchPokemonInfo(
      name = pokemonName,
      onComplete = { isLoading = false },
      onError = { toastMessage = it }
    ).asLiveDataOnViewModelScope()
  }

  @dagger.assisted.AssistedFactory
  interface AssistedFactory {
    fun create(pokemonName: String): DetailViewModel
  }

  companion object {
    fun provideFactory(
      assistedFactory: AssistedFactory,
      pokemonName: String
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return assistedFactory.create(pokemonName) as T
      }
    }
  }
}
