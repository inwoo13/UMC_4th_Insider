package com.umc.insider.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.GoodsShowFragment
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.adapter.PurchaseCatalogAdapter
import com.umc.insider.adapter.SearchResultAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentSellCatalogBinding
import com.umc.insider.model.SearchItem
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.GoodsInterface
import kotlinx.coroutines.launch

class SellCatalogFragment : Fragment(), OnNoteListener{

    private var _binding: FragmentSellCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var goodsAdapter : PurchaseCatalogAdapter
    private val chattingApi = RetrofitInstance.getInstance().create(ChattingInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellCatalogBinding.inflate(inflater, container, false)

        goodsAdapter = PurchaseCatalogAdapter(this)
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()

        lifecycleScope.launch {
            try {
                val response = chattingApi.getGoodsByUser(userIdx)

                if (response.isSuccessful){
                    val map = response.body()
                    val PurchaseGoodsList = map?.get("sale")
                    goodsAdapter.submitList(PurchaseGoodsList)
                }
            }catch (e : Exception ){
                Log.d("BUGGG", "$e")
            }
        }

        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){
            sellCatalogRV.adapter = goodsAdapter
            sellCatalogRV.layoutManager = LinearLayoutManager(context)
            sellCatalogRV.addItemDecoration(PurchaseAdapterDecoration())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNotePurchaseDetail(goods_id: Long) {
        val GoodsShow = GoodsShowFragment().apply{
            arguments = Bundle().apply{
                putString("PurchaseOrSale", goods_id.toString())
            }
        }

        val transaction = parentFragmentManager.beginTransaction()

        transaction.replace(R.id.frame_layout, GoodsShow)
        transaction.addToBackStack(null)

        transaction.commit()
    }

}