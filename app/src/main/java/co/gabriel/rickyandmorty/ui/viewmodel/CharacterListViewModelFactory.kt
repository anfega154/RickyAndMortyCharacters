package co.gabriel.rickyandmorty.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.gabriel.rickyandmorty.domain.CharacterRepository

class CharacterListViewModelFactory(private val characterRepository: CharacterRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterListViewModel(
            characterRepository
        ) as T
    }
}