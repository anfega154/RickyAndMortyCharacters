package co.gabriel.rickyandmorty.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import co.gabriel.rickyandmorty.R
import co.gabriel.rickyandmorty.core.provideCharacterRepository
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.data.model.ScreenState
import co.gabriel.rickyandmorty.databinding.CharacterListFragmentBinding
import co.gabriel.rickyandmorty.ui.viewmodel.CharacterListViewModel
import co.gabriel.rickyandmorty.ui.viewmodel.CharacterListViewModelFactory
import co.gabriel.rickyandmorty.util.Constants.BASKET
import co.gabriel.rickyandmorty.util.Constants.ERROR_BASKET_EMPTY
import co.gabriel.rickyandmorty.util.Constants.TYPE_VIEW_CHARACTER
import co.gabriel.rickyandmorty.data.model.Character
import android.text.Editable
import android.text.TextWatcher

class CharacterListFragment : BaseFragment() {

    private lateinit var viewModel: CharacterListViewModel
    private var _binding: CharacterListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var characterAdapter: CharacterRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CharacterListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (binding.etSearch.text.toString() != query) {
                binding.etSearch.setText(query)
            }
            characterAdapter.filter(query ?: "")
        }

        setupSearchLubbaAndDub()
        setupBasketButton()
        clearSearch()

        val basket = arguments.getSerializableCompat(BASKET, Basket::class.java) ?: Basket()

        viewModel.findCharacters(basket = basket)

        viewModel.screenState.observe(viewLifecycleOwner, ::renderState)
    }

    private fun setupViewModel() {
        val factory = CharacterListViewModelFactory(requireContext().provideCharacterRepository())
        viewModel = ViewModelProvider(this, factory)[CharacterListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterRecyclerViewAdapter(
            mutableListOf(),
            binding.tvTotalPrice,
            TYPE_VIEW_CHARACTER
        )
        binding.characterListRecycle.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearchLubbaAndDub() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                viewModel.searchQuery.value = query
                characterAdapter.filter(query)
                binding.tvNoResults.visibility = if (characterAdapter.hasResults()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setupBasketButton() {
        binding.btnBasket.setOnClickListener {
            val basket = characterAdapter.getBasket()
            if (basket.listcharacters.isNotEmpty()) {
                val bundle = Bundle().apply { putSerializable(BASKET, basket) }
                findNavController().navigate(
                    R.id.action_CharacterListFragment_to_checkoutFragment, bundle
                )
            } else {
                showError(ERROR_BASKET_EMPTY)
            }
        }
    }

    private fun renderState(screenState: ScreenState<List<Character>>) {
        when (screenState) {
            is ScreenState.Render -> screenState.data?.let {
                characterAdapter.updateCharacters(it.toMutableList())
                binding.tvNoResults.visibility =
                    if (characterAdapter.hasResults()) View.GONE else View.VISIBLE
            }

            is ScreenState.Error -> showError(screenState.message)
            is ScreenState.Loading -> showLoading()
        }
    }

    private fun clearSearch() {
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

