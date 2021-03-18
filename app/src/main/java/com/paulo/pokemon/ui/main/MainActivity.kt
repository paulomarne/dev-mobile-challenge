package com.paulo.pokemon.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import com.skydoves.bindables.BindingActivity
import com.paulo.pokemon.R
import com.paulo.pokemon.databinding.ActivityMainBinding
import com.paulo.pokemon.ui.adapter.PokemonAdapter
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

  @VisibleForTesting
  val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    onTransformationStartContainer()
    super.onCreate(savedInstanceState)
    binding {
      lifecycleOwner = this@MainActivity
      adapter = PokemonAdapter()
      vm = viewModel
    }
  }
}
