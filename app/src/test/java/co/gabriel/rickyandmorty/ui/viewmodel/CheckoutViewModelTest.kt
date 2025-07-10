package co.gabriel.rickyandmorty.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.Character
import co.gabriel.rickyandmorty.util.Constants.ERROR_PAY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CheckoutViewModelTest {

    private lateinit var checkoutViewModel: CheckoutViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        checkoutViewModel = CheckoutViewModel()
    }

    @Test
    fun `initialize should set listCharacterModel with non-empty list`() {
        val characters = mutableListOf(Character(id = "1", name = "Yenifer", image = "image", status = "status"))
        val basket = Basket(characters)

        checkoutViewModel.initialize(basket)

        assertEquals(characters, checkoutViewModel.listCharacterModel.value)
    }

    @Test
    fun `initialize should set listCharacterModel with empty list`() {
        val basket = Basket(mutableListOf())

        checkoutViewModel.initialize(basket)

        assertTrue(checkoutViewModel.listCharacterModel.value!!.isEmpty())
    }

    @Test
    fun `onGoBackClick should post navigateBackWithBasket`() {
        val characters = mutableListOf(Character(id = "1", name = "Yenifer", image = "image", status = "status"))
        val basket = Basket(characters)

        checkoutViewModel.onGoBackClick(basket)

        assertEquals(basket, checkoutViewModel.navigateBackWithBasket.value)
    }

    @Test
    fun `onPayClick should post showErrorEvent`() {
        checkoutViewModel.onPayClick()

        assertEquals(ERROR_PAY, checkoutViewModel.showErrorEvent.value)
    }

    @Test
    fun `checkBasketIsEmpty should navigate back if list is empty`() {
        checkoutViewModel.listCharacterModel.postValue(mutableListOf())

        checkoutViewModel.checkBasketIsEmpty()

        assertNotNull(checkoutViewModel.navigateBackWithBasket.value)
        assertTrue(checkoutViewModel.navigateBackWithBasket.value!!.listcharacters.isEmpty())
    }
}
