package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        setupOnClickListeners()

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
        binding.btnNews.isChecked = isNewsSelected
        binding.btnCampaigns.isChecked = !isNewsSelected
    }

    private fun observeViewModel() {
        // Loading durumu
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Loading indicator göster/gizle
            if (isLoading) {
                // Progress bar göster
            } else {
                // Progress bar gizle
            }
        }

        // Error durumu
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        // Mevcut observer'larınız aynen kalabilir...
        viewModel.news.observe(viewLifecycleOwner) { newsList ->
            newsList?.let {
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
        val bundle = Bundle().apply {
            putParcelable("selected_news", news)
            putString("content_type", "news")
        }
        findNavController().navigate(R.id.nav_detail, bundle)
    }

    private fun onCampaignItemClick(campaign: Campaign) {
        val bundle = Bundle().apply {
            putParcelable("selected_campaign", campaign)
            putString("content_type", "campaign")
        }
        findNavController().navigate(R.id.nav_detail, bundle)
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
        }
    }

    private fun setupOnClickListeners() {
        binding.ivIstanbul34.setOnClickListener {
            openWebUrl("https://istanbulsenin.istanbul")
        }

        binding.ivFacebook.setOnClickListener {
            openWebUrl("https://www.facebook.com/isparkas/?locale=tr_TR")
        }

        binding.ivX.setOnClickListener {
            openWebUrl("https://x.com/ispark?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor")
        }

        binding.ivInstagram.setOnClickListener {
            openWebUrl("https://instagram.com/ispark_as")
        }

        binding.ivYoutube.setOnClickListener {
            openWebUrl("https://www.youtube.com/c/ispark")
        }

        binding.ivLinkedIn.setOnClickListener {
            openWebUrl("https://www.linkedin.com/company/isparkas/posts/?feedView=all")
        }

        binding.cvNearestParking.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToNavDetail()
            findNavController().navigate(action)
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