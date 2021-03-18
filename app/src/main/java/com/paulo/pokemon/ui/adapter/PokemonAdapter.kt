package com.paulo.pokemon.ui.adapter

import android.os.SystemClock
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.skydoves.bindables.binding
import com.paulo.pokemon.R
import com.paulo.pokemon.databinding.ItemPokemonBinding
import com.paulo.pokemon.model.Pokemon
import com.paulo.pokemon.ui.details.DetailActivity

class PokemonAdapter : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

  private val items: MutableList<Pokemon> = mutableListOf()
  private var onClickedAt = 0L

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
    val binding = parent.binding<ItemPokemonBinding>(R.layout.item_pokemon)
    return PokemonViewHolder(binding).apply {
      binding.root.setOnClickListener {
        val position = bindingAdapterPosition.takeIf { it != NO_POSITION }
          ?: return@setOnClickListener
        val currentClickedAt = SystemClock.elapsedRealtime()
        if (currentClickedAt - onClickedAt > binding.transformationLayout.duration) {
          DetailActivity.startActivity(binding.transformationLayout, items[position])
          onClickedAt = currentClickedAt
        }
      }
    }
  }

  fun setPokemonList(pokemonList: List<Pokemon>) {
    val previousItemSize = items.size
    items.clear()
    items.addAll(pokemonList)
    notifyItemRangeChanged(previousItemSize, pokemonList.size)
  }

  override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
    holder.binding.apply {
      pokemon = items[position]
      executePendingBindings()
    }
  }

  override fun getItemCount() = items.size

  class PokemonViewHolder(val binding: ItemPokemonBinding) :
    RecyclerView.ViewHolder(binding.root)
}
