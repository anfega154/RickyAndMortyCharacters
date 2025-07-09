package co.gabriel.rickyandmorty.util

import co.gabriel.rickyandmorty.data.model.Character

object CharacterPriceCalculator {
    fun calculatePrice(character: Character): Double {
        return when (character.status) {
            Constants.ALIVE -> ((Constants.PERCENTAGE_20 + 1) * Constants.FIXEDPRICE) * character.quantity
            Constants.DEAD -> (Constants.PERCENTAGE_80 * Constants.FIXEDPRICE) * character.quantity
            Constants.UNKNOWN -> Constants.FIXEDPRICE * character.quantity.toDouble()
            else -> Constants.FIXEDPRICE * character.quantity.toDouble()
        }
    }

    fun statusOrPercentage(character: Character): String {
        return when (character.status) {
            Constants.ALIVE -> "+${Constants.PERCENTAGE_20_STATUS}"
            Constants.DEAD -> "-${Constants.PERCENTAGE_20_STATUS}"
            Constants.UNKNOWN -> Constants.PERCENTAGE_0_STATUS
            else -> Constants.PERCENTAGE_0_STATUS
        }
    }
}
