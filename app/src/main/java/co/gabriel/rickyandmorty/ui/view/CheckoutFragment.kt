package co.gabriel.rickyandmorty.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.gabriel.rickyandmorty.R
import co.gabriel.rickyandmorty.data.model.Basket
import co.gabriel.rickyandmorty.databinding.FragmentCheckoutBinding
import co.gabriel.rickyandmorty.ui.viewmodel.CheckoutViewModel
import co.gabriel.rickyandmorty.util.Constants.BASKET
import co.gabriel.rickyandmorty.util.Constants.TYPE_VIEW_CHECKOUT

class CheckoutFragment : BaseFragment() {

    private lateinit var viewModel: CheckoutViewModel
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var characterAdapter: CharacterRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        characterAdapter = CharacterRecyclerViewAdapter(
            mutableListOf(), binding.tvTotalPrice, TYPE_VIEW_CHECKOUT
        )

        binding.characterListRecycle.layoutManager = LinearLayoutManager(context)
        binding.characterListRecycle.adapter = characterAdapter

        val basket = arguments.getSerializableCompat(BASKET, Basket::class.java) ?: Basket()

        viewModel.initialize(basket)

        binding.btnGoBack.setOnClickListener {
            viewModel.onGoBackClick(characterAdapter.getBasket())
        }

        binding.btnPay.setOnClickListener {
            viewModel.onPayClick()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onGoBackClick(characterAdapter.getBasket())
        }

        viewModel.listCharacterModel.observe(viewLifecycleOwner) {
            characterAdapter.updateCharacters(it)
        }

        viewModel.navigateBackWithBasket.observe(viewLifecycleOwner) { updatedBasket ->
            val bundle = Bundle().apply { putSerializable(BASKET, updatedBasket) }
            findNavController().navigate(
                R.id.action_checkoutFragment_to_CharacterListFragment, bundle
            )
        }

        viewModel.showErrorEvent.observe(viewLifecycleOwner) {
            showError(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
