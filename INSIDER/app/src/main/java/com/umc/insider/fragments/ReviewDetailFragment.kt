package com.umc.insider.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.umc.insider.R
import com.umc.insider.databinding.FragmentReviewDetailBinding
import com.umc.insider.utils.changeStatusBarColor

class ReviewDetailFragment : Fragment() {

    private var _binding : FragmentReviewDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewDetailBinding.inflate(inflater, container, false)

        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}