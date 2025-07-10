package co.gabriel.rickyandmorty.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.Character
import co.gabriel.rickyandmorty.util.Constants.ERROR_PAY

class CheckoutViewModel : ViewModel() {

    val listCharacterModel = MutableLiveData<MutableList<Character>>()
    val navigateBackWithBasket = MutableLiveData<Basket>()
    val showErrorEvent = MutableLiveData<String>()

    fun initialize(basket: Basket) {
        listCharacterModel.postValue(basket.listcharacters.toMutableList())
    }

    fun onGoBackClick(basket: Basket) {
        navigateBackWithBasket.postValue(basket)
    }

    fun onPayClick() {
        showErrorEvent.postValue(ERROR_PAY)
    }

    /**
     * function for testing purposes
     */
    fun checkBasketIsEmpty() {
        if (listCharacterModel.value.isNullOrEmpty()) {
            navigateBackWithBasket.postValue(Basket())
        }
    }

}
