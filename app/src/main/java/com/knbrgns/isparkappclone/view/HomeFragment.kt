package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        setupOnClickListeners()
        setupTabButtons()
        viewModel.getNews()

    }

    private fun setupTabButtons() {
        binding.btnNews.setOnClickListener {
            if (!isNewsSelected) {
                isNewsSelected = true
                updateTabButtonStates()
                showNewsInRecyclerView()
            }
        }

        binding.btnCampaigns.setOnClickListener {
            if (isNewsSelected) {
                isNewsSelected = false
                updateTabButtonStates()
                showCampaignsInRecyclerView()
            }
        }

        updateTabButtonStates()
    }

    private fun updateTabButtonStates() {
        if (isNewsSelected) {
            binding.btnNews.isChecked = true
            binding.btnCampaigns.isChecked = false
        } else {
            binding.btnNews.isChecked = false
            binding.btnCampaigns.isChecked = true
        }
    }

    private fun observeViewModel() {

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
        // Detail fragmentına git ve haberi aktar
        val action = HomeFragmentDirections.actionNavHomeToNavDetail()
        // Eğer Safe Args kullanmıyorsanız bundle ile:
        val bundle = Bundle().apply {
            putParcelable("selected_news", news)
            putString("content_type", "news")
        }
        findNavController().navigate(R.id.nav_detail, bundle)
    }

    private fun onCampaignItemClick(campaign: Campaign) {
        // Detail fragmentına git ve kampanyayı aktar
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

    private fun setupRecyclerView(){
        binding.rvHomePage.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun setupOnClickListeners(){
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
            openWebUrl("https://linkedin.com/company/ispark")
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
}