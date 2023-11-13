package com.umc.insider.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.GoodsShowFragment
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.adapter.PurchaseCatalogAdapter
import com.umc.insider.adapter.SearchResultAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentPurchaseCatalogBinding
import com.umc.insider.fragments.SearchResultAdapterDecoration
import com.umc.insider.model.SearchItem
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PurchaseCatalogFragment : Fragment(), OnNoteListener{

    private var _binding: FragmentPurchaseCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var goodsAdapter : PurchaseCatalogAdapter
    private val chattingApi = RetrofitInstance.getInstance().create(ChattingInterface::class.java)

    private var selectPosition : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchaseCatalogBinding.inflate(inflater, container, false)

        goodsAdapter = PurchaseCatalogAdapter(this)
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()

        lifecycleScope.launch {
            try {
                val response = chattingApi.getGoodsByUser(userIdx)

                if (response.isSuccessful){
                    val map = response.body()
                    val PurchaseGoodsList = map?.get("purchase")
                    goodsAdapter.submitList(PurchaseGoodsList)
                }
            }catch (e : Exception ){
                Log.d("BUGGGG", "$e")
            }
        }

        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){
            PurchaseCatalogRV.adapter = goodsAdapter
            PurchaseCatalogRV.layoutManager = LinearLayoutManager(context)
            PurchaseCatalogRV.addItemDecoration(PurchaseAdapterDecoration())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNotePurchaseDetail(goods_id: Long) {
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)

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

class PurchaseAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.bottom = 20
    }
}