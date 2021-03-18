package com.paulo.pokemon.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.baserecyclerviewadapter.RecyclerViewPaginator
import com.paulo.pokemon.model.Pokemon
import com.paulo.pokemon.ui.adapter.PokemonAdapter
import com.paulo.pokemon.ui.main.MainViewModel
import com.skydoves.whatif.whatIfNotNullAs
import com.skydoves.whatif.whatIfNotNullOrEmpty

object RecyclerViewBinding {

  @JvmStatic
  @BindingAdapter("adapter")
  fun bindAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter.apply {
      stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
  }

  @JvmStatic
  @BindingAdapter("paginationPokemonList")
  fun paginationPokemonList(view: RecyclerView, viewModel: MainViewModel) {
    RecyclerViewPaginator(
      recyclerView = view,
      isLoading = { viewModel.isLoading },
      loadMore = { viewModel.fetchNextPokemonList() },
      onLast = { false }
    ).run {
      threshold = 8
    }
  }

  @JvmStatic
  @BindingAdapter("adapterPokemonList")
  fun bindAdapterPokemonList(view: RecyclerView, pokemonList: List<Pokemon>?) {
    pokemonList.whatIfNotNullOrEmpty { itemList ->
      view.adapter.whatIfNotNullAs<PokemonAdapter> { adapter ->
        adapter.setPokemonList(itemList)
      }
    }
  }
}
