package com.umc.insider.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.ExchangeShowFragment
import com.umc.insider.GoodsShowFragment
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.ExchangeAdapter
import com.umc.insider.adapter.ExchangeEndAdapter
import com.umc.insider.adapter.ExchangeEndCatalogAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentExchangeCatalogBinding
import com.umc.insider.model.ExchangeItem
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.model.ExchangesPostRes
import kotlinx.coroutines.launch


class ExchangeCatalogFragment : Fragment(), OnNoteListener {

    private var _binding : FragmentExchangeCatalogBinding? = null
    private val binding get() = _binding!!

    private val exchangeEndAdapter = ExchangeAdapter(this)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExchangeCatalogBinding.inflate(inflater, container, false)

        val chattingApi = RetrofitInstance.getInstance().create(ChattingInterface::class.java)
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()

        lifecycleScope.launch {
            try {
                val response = chattingApi.getExchangesByUser(userIdx)

                if (response.isSuccessful){
                    val map = response.body()
                    val ExchangeYourGoodsList = map?.get("Exchange your item")
                    val ExchangeMyGoodsList = map?.get("Exchange my item")

                    val combinedList = mutableListOf<ExchangesPostRes>()

                    if(ExchangeYourGoodsList != null){
                        combinedList.addAll(ExchangeYourGoodsList)
                    }

                    if(ExchangeMyGoodsList != null){
                        combinedList.addAll(ExchangeMyGoodsList)
                    }

                    exchangeEndAdapter.submitList(combinedList)
                }
            }catch (e : Exception ){

            }
        }

        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){

            exchangeCatalogRV.adapter = exchangeEndAdapter
            exchangeCatalogRV.layoutManager = LinearLayoutManager(context)
            exchangeCatalogRV.addItemDecoration(ExchangeMainFragment.ExchangeAdapterDecoration())

        }
    }


    override fun onPause(){
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onNotePurchaseDetail(goods_id: Long) {
        val ExchangeShow = ExchangeShowFragment().apply{
            arguments = Bundle().apply{
                putString("ExchangeShw", goods_id.toString())
            }
        }

        val transaction = parentFragmentManager.beginTransaction()

        transaction.replace(R.id.frame_layout, ExchangeShow)
        transaction.addToBackStack(null)

        transaction.commit()
    }


}