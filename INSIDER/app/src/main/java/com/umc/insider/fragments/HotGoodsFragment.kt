package com.umc.insider.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.OnNoteListener
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentHotGoodsBinding
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.WishListInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HotGoodsFragment : Fragment(), OnNoteListener {

    private var _binding: FragmentHotGoodsBinding? = null
    private val binding get() = _binding!!

    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
    private val wishListAPI = RetrofitInstance.getInstance().create(WishListInterface::class.java)
    private val hotGoodsAdapter = GoodsLongAdapter(this)

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {

            try {
                val response = withContext(Dispatchers.IO){
                    wishListAPI.getHotGoods()
                }

                if (response.isSuccess){
                    val hotList = response.result
                    if (!hotList.isNullOrEmpty()){
                        hotGoodsAdapter.submitList(hotList)
                    }
                }
            }catch (e : Exception){

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentHotGoodsBinding.inflate(inflater, container, false)
        initView()

        return binding!!.root
    }

    private fun initView(){
        with(binding){
            hotGoodsListRV.adapter = hotGoodsAdapter
            hotGoodsListRV.layoutManager = LinearLayoutManager(context)
            hotGoodsListRV.addItemDecoration(SearchResultAdapterDecoration())

            lifecycleScope.launch {

                try {
                    val response = withContext(Dispatchers.IO){
                        wishListAPI.getHotGoods()
                    }

                    if (response.isSuccess){
                        val hotList = response.result
                        if (!hotList.isNullOrEmpty()){
                            hotGoodsAdapter.submitList(hotList)
                        }
                    }
                }catch (e : Exception){

                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNotePurchaseDetail(goods_id: Long) {

        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        Log.d("REVISEEE", "userIdx : {$userIdx}")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    goodsAPI.getGoodsById(goods_id)
                }
                withContext(Dispatchers.Main){
                    val sellerID = response.users_id.id

                    if(userIdx != sellerID){
                        //Toast.makeText(requireContext(), goods_id.toString(), Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), PurchaseDetailActivity::class.java)
                        intent.putExtra("goods_id", goods_id.toString())
                        startActivity(intent)
                    } else {
                        val intent = Intent(requireContext(), SaleReviseDetailActivity::class.java)
                        intent.putExtra("goods_id", goods_id.toString())
                        startActivity(intent)
                    }
                }
            }catch (e : Exception){

            }

        }
    }
}