package co.gabriel.rickyandmorty.ui.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.gabriel.rickyandmorty.R
import co.gabriel.rickyandmorty.core.doubleToCurrency
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.Character
import co.gabriel.rickyandmorty.data.model.CharacterListItem
import co.gabriel.rickyandmorty.databinding.CharacterItemBinding
import co.gabriel.rickyandmorty.util.CharacterPriceCalculator
import co.gabriel.rickyandmorty.util.Constants.TYPE_VIEW_CHECKOUT
import com.bumptech.glide.Glide

class CharacterRecyclerViewAdapter(
    private var items: MutableList<CharacterListItem>,
    private var tvTotalPrice: TextView,
    private var typeView: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CHARACTER = 1
    }

    private var basket = Basket()
    private val allCharacters = mutableListOf<Character>()


    init {
        allCharacters.addAll(
            items.filterIsInstance<CharacterListItem.CharacterItem>().map { it.character }
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CharacterListItem.SectionHeader -> VIEW_TYPE_HEADER
            is CharacterListItem.CharacterItem -> VIEW_TYPE_CHARACTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_header, parent, false)
                SectionHeaderViewHolder(view)
            }
            else -> {
                val binding = CharacterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CharacterViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CharacterListItem.SectionHeader -> (holder as SectionHeaderViewHolder).bind(item)
            is CharacterListItem.CharacterItem -> (holder as CharacterViewHolder).bind(item.character, position)
        }
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.sectionHeader)
        fun bind(header: CharacterListItem.SectionHeader) {
            headerTitle.text = header.title
        }
    }

    inner class CharacterViewHolder(binding: CharacterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image: ImageView = binding.characterImage
        val name: TextView = binding.characterName
        val price: TextView = binding.tvPrice
        val quantity: TextView = binding.tvCurrentQuantity
        val btnAdd: Button = binding.buttonAdd
        val btnRemove: Button = binding.buttonRemove
        val btnDelete: ImageView = binding.buttonDelete
        val statusOrPercentage: TextView = binding.characterStatusOrPercentage

        fun bind(character: Character, position: Int) {
            name.text = character.name
            quantity.text = character.quantity.toString()
            statusOrPercentage.text = CharacterPriceCalculator.statusOrPercentage(character)

            activateDeleteButton(character, this)

            Glide.with(itemView)
                .load(character.image)
                .thumbnail(0.1f)
                .into(image)

            btnAdd.setOnClickListener {
                ++character.quantity
                updateFields(this, position)
            }

            btnRemove.setOnClickListener {
                if (character.quantity > 0) {
                    --character.quantity
                    updateFields(this, position)
                }
            }

            btnDelete.setOnClickListener {
                // Encuentra índice real
                val realPosition = adapterPosition
                items.removeAt(realPosition)
                updateTotal()
                notifyItemRemoved(realPosition)
            }

            price.text = doubleToCurrency(CharacterPriceCalculator.calculatePrice(character))
            updateTotal()
        }
    }

    private fun updateFields(holder: CharacterViewHolder, position: Int) {
        val characterItem = items[position] as CharacterListItem.CharacterItem
        val character = characterItem.character
        holder.quantity.text = character.quantity.toString()
        holder.price.text = doubleToCurrency(CharacterPriceCalculator.calculatePrice(character))
        activateDeleteButton(character, holder)
        updateTotal()
    }

    private fun activateDeleteButton(character: Character, holder: CharacterViewHolder) {
        if (character.quantity == 1 && typeView == TYPE_VIEW_CHECKOUT) {
            holder.btnRemove.visibility = View.GONE
            holder.btnDelete.visibility = View.VISIBLE
        } else {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.GONE
        }
    }

    private fun updateTotal() {
        val total = items
            .filterIsInstance<CharacterListItem.CharacterItem>()
            .sumOf { CharacterPriceCalculator.calculatePrice(it.character) }

        tvTotalPrice.text = doubleToCurrency(total)
        basket.listcharacters = items
            .filterIsInstance<CharacterListItem.CharacterItem>()
            .map { it.character }
            .filter { it.quantity > 0 }
            .toMutableList()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCharacters(newCharacters: MutableList<Character>) {
        allCharacters.clear()
        allCharacters.addAll(newCharacters)
        this.items = buildSectionedList(allCharacters)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val filtered = if (query.isBlank()) {
            allCharacters
        } else {
            allCharacters.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        this.items = buildSectionedList(filtered)
        notifyDataSetChanged()
        updateTotal()
    }

    fun hasResults(): Boolean {
        return items.any { it is CharacterListItem.CharacterItem }
    }

    private fun buildSectionedList(characters: List<Character>): MutableList<CharacterListItem> {
        val grouped = characters.groupBy {
            it.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }.toSortedMap()

        val sectionedList = mutableListOf<CharacterListItem>()
        grouped.forEach { (letter, charList) ->
            sectionedList.add(CharacterListItem.SectionHeader(letter))
            charList.sortedBy { it.name }.forEach {
                sectionedList.add(CharacterListItem.CharacterItem(it))
            }
        }
        return sectionedList
    }

    fun getBasket(): Basket = basket
}
