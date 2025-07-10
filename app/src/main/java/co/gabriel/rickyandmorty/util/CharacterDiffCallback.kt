package co.gabriel.rickyandmorty.util

import androidx.recyclerview.widget.DiffUtil
import co.gabriel.rickyandmorty.data.model.CharacterListItem

class CharacterDiffCallback(
    private val oldList: List<CharacterListItem>,
    private val newList: List<CharacterListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem is CharacterListItem.CharacterItem && newItem is CharacterListItem.CharacterItem) {
            oldItem.character.id == newItem.character.id
        } else if (oldItem is CharacterListItem.SectionHeader && newItem is CharacterListItem.SectionHeader) {
            oldItem.title == newItem.title
        } else false
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
