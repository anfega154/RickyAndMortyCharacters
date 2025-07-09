package co.gabriel.rickyandmorty.data.model

sealed class CharacterListItem {
    data class SectionHeader(val title: String) : CharacterListItem()
    data class CharacterItem(val character: Character) : CharacterListItem()
}
