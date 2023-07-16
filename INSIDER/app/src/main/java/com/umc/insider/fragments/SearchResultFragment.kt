package com.umc.insider.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc.insider.R
import com.umc.insider.adapter.SearchTermsRankingAdapter
import com.umc.insider.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {

    private var _binding : FragmentSearchResultBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        initView()

        val searchQuery = arguments?.getString("search_query")
        binding.searchText.text = "\"$searchQuery\" 검색 결과"

        return binding.root

    }

    private fun initView(){
        with(binding){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}