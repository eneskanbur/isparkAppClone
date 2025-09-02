package com.knbrgns.isparkappclone.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.databinding.FragmentHomeBinding
import com.knbrgns.isparkappclone.view.adapter.CampaignAdapter
import com.knbrgns.isparkappclone.view.adapter.NewsAdapter
import com.knbrgns.isparkappclone.view.viewmodel.HomeViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.knbrgns.isparkappclone.MainActivity
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.view.dialog.ErrorDialog
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var isNewsSelected = true
    private var newsAdapter: NewsAdapter? = null
    private var campaignAdapter: CampaignAdapter? = null
    private lateinit var user : com.knbrgns.isparkappclone.model.User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupTabButtons()
        setupButtons()
        setupUser()
        viewModel.initialize()
    }

    private fun setupUser() {
        lifecycleScope.launch {
            user = viewModel.getUser(FirebaseAuth.getInstance().currentUser!!.uid)!!
            (activity as? MainActivity)?.let { mainActivity ->
                val headerView = mainActivity.binding.navView.getHeaderView(0)

                val nameTextView = headerView.findViewById<TextView>(R.id.textViewUserName)
                val emailTextView = headerView.findViewById<TextView>(R.id.textViewUserEmail)

                nameTextView.text = user.name
                emailTextView.text = user.email
            }
        }
    }

    private fun setupButtons() {
        binding.btnParkHistory.setOnClickListener {
            ErrorDialog.Builder(requireContext())
                .setCancelable(true)
                .setOnConfirm {  }
                .setErrorTitle("İşlem Başarısız")
                .setErrorMessage("Bu işlem henüz uygulanmamaktadır.")
                .build()
                .show()
        }
    }

    private fun setupTabButtons() {
        binding.btnNews.setOnClickListener {
            if (!isNewsSelected) {
                isNewsSelected = true
                updateTabButtonStates()

                if (newsAdapter != null) {
                    showNewsInRecyclerView()
                } else {
                    viewModel.getNews()
                }
            }
        }

        binding.btnCampaigns.setOnClickListener {
            if (isNewsSelected) {
                isNewsSelected = false
                updateTabButtonStates()

                if (campaignAdapter != null) {
                    showCampaignsInRecyclerView()
                } else {
                    viewModel.getCampaigns()
                }
            }
        }

        binding.btnMyCar.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_myCarFragment)
        }

        updateTabButtonStates()
    }

    private fun updateTabButtonStates() {
        if (isNewsSelected) {
            binding.btnNews.apply {
                isChecked = true
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedBackground)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.tabSelectedText))
                strokeColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedStroke)
                iconTint =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedIcon)
                rippleColor = ContextCompat.getColorStateList(requireContext(), R.color.tabRipple)
            }

            binding.btnCampaigns.apply {
                isChecked = false
                backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.tabUnselectedBackground
                )
                setTextColor(ContextCompat.getColor(requireContext(), R.color.tabUnselectedText))
                strokeColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabUnselectedStroke)
                iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                rippleColor = ContextCompat.getColorStateList(requireContext(), R.color.tabRipple)
            }
        } else {
            binding.btnCampaigns.apply {
                isChecked = true
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedBackground)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.tabSelectedText))
                strokeColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedStroke)
                iconTint =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabSelectedIcon)
                rippleColor = ContextCompat.getColorStateList(requireContext(), R.color.tabRipple)
            }

            binding.btnNews.apply {
                isChecked = false
                backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.tabUnselectedBackground
                )
                setTextColor(ContextCompat.getColor(requireContext(), R.color.tabUnselectedText))
                strokeColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.tabUnselectedStroke)
                iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                rippleColor = ContextCompat.getColorStateList(requireContext(), R.color.tabRipple)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.news.observe(viewLifecycleOwner) { newsList ->
            newsList?.let {
                Log.d("ISPARK_FLOW", "📱 UI UPDATE -> News received (${it.size} items)")
                newsAdapter = NewsAdapter(it) { selectedNews ->
                    onNewsItemClick(selectedNews)
                }
                if (isNewsSelected) {
                    showNewsInRecyclerView()
                }
            }
        }

        viewModel.campaign.observe(viewLifecycleOwner) { campaignList ->
            campaignList?.let {
                Log.d("ISPARK_FLOW", "📱 UI UPDATE -> Campaigns received (${it.size} items)")
                campaignAdapter = CampaignAdapter(it) { selectedCampaign ->
                    onCampaignItemClick(selectedCampaign)
                }
                if (!isNewsSelected) {
                    showCampaignsInRecyclerView()
                }
            }
        }

    }

    private fun onNewsItemClick(news: News) {
        val action = HomeFragmentDirections.actionNavHomeToNavDetail(news = news, null)
        findNavController().navigate(action)
    }

    private fun onCampaignItemClick(campaign: Campaign) {
        val action = HomeFragmentDirections.actionNavHomeToNavDetail(null, campaign)
        findNavController().navigate(action)
    }

    private fun showNewsInRecyclerView() {
        newsAdapter?.let {
            binding.rvHomePage.adapter = it
        }
    }

    private fun showCampaignsInRecyclerView() {
        campaignAdapter?.let {
            binding.rvHomePage.adapter = it
        }
    }

    private fun setupRecyclerView() {
        binding.rvHomePage.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
            setItemViewCacheSize(20)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
