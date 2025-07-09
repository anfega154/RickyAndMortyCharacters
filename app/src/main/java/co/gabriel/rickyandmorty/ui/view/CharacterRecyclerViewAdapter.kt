package co.gabriel.rickyandmorty.ui.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.gabriel.rickyandmorty.core.doubleToCurrency
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.Character
import co.gabriel.rickyandmorty.databinding.CharacterItemBinding
import co.gabriel.rickyandmorty.util.CharacterPriceCalculator
import co.gabriel.rickyandmorty.util.Constants.TYPE_VIEW_CHECKOUT
import com.bumptech.glide.Glide

class CharacterRecyclerViewAdapter(
    private var characters: MutableList<Character>,
    private var tvTotalPrice: TextView,
    private var typeView: String
) : RecyclerView.Adapter<CharacterRecyclerViewAdapter.ViewHolder>() {

    private var basket = Basket()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CharacterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = characters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characters[position]
        holder.name.text = character.name
        holder.quantity.text = character.quantity.toString()
        holder.statusOrPercentage.text = CharacterPriceCalculator.statusOrPercentage(character)

        activateDeleteButton(character, holder)

        Glide.with(holder.itemView)
            .load(character.image)
            .thumbnail(0.1f)
            .into(holder.image)

        holder.btnAdd.setOnClickListener {
            ++character.quantity
            updateFields(holder, position)
        }

        holder.btnRemove.setOnClickListener {
            if (character.quantity > 0) {
                --character.quantity
                updateFields(holder, position)
            }
        }

        holder.btnDelete.setOnClickListener {
            characters.removeAt(position)
            updateCharacters(characters)
            updateTotal()
        }

        holder.price.text = doubleToCurrency(CharacterPriceCalculator.calculatePrice(character))
        updateTotal()
    }

    private fun updateFields(holder: ViewHolder, position: Int) {
        val character = characters[position]
        holder.quantity.text = character.quantity.toString()
        holder.price.text = doubleToCurrency(CharacterPriceCalculator.calculatePrice(character))
        activateDeleteButton(character, holder)
        updateTotal()
    }

    private fun activateDeleteButton(character: Character, holder: ViewHolder) {
        if (character.quantity == 1 && typeView == TYPE_VIEW_CHECKOUT) {
            holder.btnRemove.visibility = View.GONE
            holder.btnDelete.visibility = View.VISIBLE
        } else {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.GONE
        }
    }

    private fun updateTotal() {
        val total = characters.sumOf { CharacterPriceCalculator.calculatePrice(it) }
        tvTotalPrice.text = doubleToCurrency(total)
        basket.listcharacters = characters.filter { it.quantity > 0 }.toMutableList()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCharacters(characters: MutableList<Character>) {
        this.characters = characters
        notifyDataSetChanged()
    }

    fun getBasket(): Basket = basket

    inner class ViewHolder(binding: CharacterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image: ImageView = binding.characterImage
        val name: TextView = binding.characterName
        val price: TextView = binding.tvPrice
        val quantity: TextView = binding.tvCurrentQuantity
        val btnAdd: Button = binding.buttonAdd
        val btnRemove: Button = binding.buttonRemove
        val btnDelete: ImageView = binding.buttonDelete
        val statusOrPercentage: TextView = binding.characterStatusOrPercentage
    }
}
