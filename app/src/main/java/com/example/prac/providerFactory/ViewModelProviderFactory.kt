package com.example.jetpackdemo

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelProviderFactory<T : Any>(private val viewModel: T): ViewModelProvider.Factory {
  @NonNull
  override fun <T : ViewModel> create(@NonNull modelClass:Class<T>):T {
    if (modelClass.isAssignableFrom(viewModel.javaClass))
    {
      return modelClass.cast(viewModel)
    }
    throw IllegalArgumentException("Unknown class name")
  }
}