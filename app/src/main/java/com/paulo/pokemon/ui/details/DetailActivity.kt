package com.paulo.pokemon.ui.details

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.bundleNonNull
import com.skydoves.bundler.intentOf
import com.paulo.pokemon.R
import com.paulo.pokemon.databinding.ActivityDetailBinding
import com.paulo.pokemon.extensions.onTransformationEndContainerApplyParams
import com.paulo.pokemon.model.Pokemon
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {

  @Inject
  lateinit var detailViewModelFactory: DetailViewModel.AssistedFactory

  @VisibleForTesting
  val viewModel: DetailViewModel by viewModels {
    DetailViewModel.provideFactory(detailViewModelFactory, pokemonItem.name)
  }

  private val pokemonItem: Pokemon by bundleNonNull(EXTRA_POKEMON)

  override fun onCreate(savedInstanceState: Bundle?) {
    onTransformationEndContainerApplyParams()
    super.onCreate(savedInstanceState)
    binding {
      lifecycleOwner = this@DetailActivity
      pokemon = pokemonItem
      vm = viewModel
    }
  }

  companion object {
    @VisibleForTesting
    const val EXTRA_POKEMON = "EXTRA_POKEMON"

    fun startActivity(transformationLayout: TransformationLayout, pokemon: Pokemon) =
      transformationLayout.context.intentOf<DetailActivity> {
        putExtra(EXTRA_POKEMON to pokemon)
        TransformationCompat.startActivity(transformationLayout, intent)
      }
  }
}
