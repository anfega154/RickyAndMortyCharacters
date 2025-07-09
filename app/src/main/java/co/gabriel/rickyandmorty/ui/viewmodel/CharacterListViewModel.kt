package co.gabriel.rickyandmorty.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gabriel.rickyandmorty.data.Result
import co.gabriel.rickyandmorty.data.model.Action
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.Character
import co.gabriel.rickyandmorty.data.model.ScreenState
import co.gabriel.rickyandmorty.domain.CharacterRepository
import co.gabriel.rickyandmorty.util.Constants.DEFAULT_PAGE
import co.gabriel.rickyandmorty.util.Constants.ERROR_FETCHING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterListViewModel(private val characterRepository: CharacterRepository) : ViewModel() {

    val screenState = MutableLiveData<ScreenState<List<Character>>>()

    fun findCharacters(page: Int = DEFAULT_PAGE, basket: Basket? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            screenState.postValue(ScreenState.Loading())
            when (val result = characterRepository.fetchAllCharactersByPage(page)) {
                is Result.Success -> {
                    val mergedList = mergeWithBasket(result.data, basket)
                    screenState.postValue(ScreenState.Render(mergedList, Action.RENDER_CHARACTERS))
                }

                is Result.Error -> {
                    screenState.postValue(ScreenState.Error("$ERROR_FETCHING $page", emptyList()))
                }
            }
        }
    }

    private fun mergeWithBasket(list: List<Character>, basket: Basket?): MutableList<Character> {
        if (basket == null || basket.listcharacters.isEmpty()) return list.toMutableList()
        val combined = (basket.listcharacters + list).distinctBy { it.id }
        return combined.groupBy { it.id }
            .map { it.value.maxByOrNull { c -> basket.listcharacters.contains(c) }!! }
            .toMutableList()
    }
}