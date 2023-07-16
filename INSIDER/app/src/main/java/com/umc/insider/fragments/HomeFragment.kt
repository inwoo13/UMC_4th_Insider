package com.umc.insider.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc.insider.R
import com.umc.insider.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()

        return _binding!!.root
    }

    private fun initView(){
        with(binding){
            searchLayout.setOnClickListener {
                val searchFragment = SearchFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, searchFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}