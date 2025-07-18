package com.knbrgns.isparkappclone.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.databinding.FragmentHomeBinding
import com.knbrgns.isparkappclone.view.adapter.CampaignAdapter
import com.knbrgns.isparkappclone.view.adapter.NewsAdapter
import com.knbrgns.isparkappclone.viewmodel.HomeViewModel
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var isNewsSelected = true
    private var newsAdapter: NewsAdapter? = null
    private var campaignAdapter: CampaignAdapter? = null

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
        viewModel.initialize()
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

    private fun openWebUrl(url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = url.toUri()
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                "Web sayfası açılamadı: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
