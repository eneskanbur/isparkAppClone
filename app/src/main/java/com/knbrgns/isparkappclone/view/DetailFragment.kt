package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val news = args.news
        val campaign = args.campaign

        if (news != null) {
            binding.tvDetailDescription.text = news.descriptionLong
            Glide.with(binding.ivDetail.context).load(news.imageUrl).into(binding.ivDetail)
            binding.tvDetailTitle.text = news.title
            binding.tvDate.text = news.sDate
            binding.chipCategory.text = getString(R.string.news_tag)
        } else if (campaign != null) {
            binding.tvDetailDescription.text = campaign.descriptionLong
            Glide.with(binding.ivDetail.context).load(campaign.imageUrl).into(binding.ivDetail)
            binding.tvDetailTitle.text = campaign.title
            binding.tvDate.text = campaign.sDate
            binding.chipCategory.text = getString(R.string.campaigns)
        }
    }
}