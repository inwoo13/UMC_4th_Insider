package com.umc.insider.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.ExchangeAdapter
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.adapter.SearchResultAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentFavoriteBinding
import com.umc.insider.exchange.ExchangeDetailActivity
import com.umc.insider.exchange.ExchangeModifyActivity
import com.umc.insider.model.ExchangeItem
import com.umc.insider.model.SearchItem
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.WishListInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment(), OnNoteListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    //private val searchAdapter = SearchResultAdapter(this)
    private val generalPurchasefavoriteAdapter = GoodsLongAdapter(this)
    private val exchangeAdapter = ExchangeAdapter(this)

    private val wishListAPI = RetrofitInstance.getInstance().create(WishListInterface::class.java)

    private var isGeneralPurchaseSelected = true
    private var isDecorateCheck = true
    private var user_id : Long? = null

    override fun onResume() {
        super.onResume()
        updateButtonUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        user_id = UserManager.getUserIdx(requireContext())!!.toLong()
        initview()

        return binding.root
    }

    private fun initview() {

        with(binding) {
            // Click listeners for the buttons

            favoriteRV.adapter = generalPurchasefavoriteAdapter
            favoriteRV.layoutManager = LinearLayoutManager(context)
            favoriteRV.addItemDecoration(ExchangeMainFragment.ExchangeAdapterDecoration())

            selectPurchase.setOnClickListener {
                isGeneralPurchaseSelected = true
                updateButtonUI()
            }

            Exchange.setOnClickListener {
                isGeneralPurchaseSelected = false
                updateButtonUI()
            }

            // Update the button UI initially
            updateButtonUI()
            isDecorateCheck = false
        }
    }

    private fun updateButtonUI() {
        with(binding) {

            // 서버 넘겨받으면 찜한 목록 중에서 어떤 건지 판단해서 recyclerview 띄우게 하기
            if (isGeneralPurchaseSelected) {

                generalPurchase.background = ContextCompat.getDrawable(requireContext(), R.drawable.green_left_round)
                generalPurchase.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                Exchange.background = ContextCompat.getDrawable(requireContext(), R.drawable.white_right_round)
                Exchange.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))

                favoriteRV.adapter = generalPurchasefavoriteAdapter
                favoriteRV.layoutManager = LinearLayoutManager(context)

                lifecycleScope.launch {

                    try {

                        val response = withContext(Dispatchers.IO){
                            wishListAPI.getGoodsInWishList(user_id!!)
                        }

                        if (response.isSuccessful){
                            val generalPurchasefavoriteGoodsList = response.body()
                            val sortedGeneralPurchasefavoriteGoodsList = generalPurchasefavoriteGoodsList!!.sortedByDescending { it.createdAt }
                            withContext(Dispatchers.Main){
                                generalPurchasefavoriteAdapter.submitList(sortedGeneralPurchasefavoriteGoodsList)
                            }
                        }

                    }catch (e : Exception){

                    }

                }


            } else {
                generalPurchase.background = ContextCompat.getDrawable(requireContext(), R.drawable.white_left_round)
                generalPurchase.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))
                Exchange.background = ContextCompat.getDrawable(requireContext(), R.drawable.green_right_round)
                Exchange.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                favoriteRV.adapter = exchangeAdapter
                favoriteRV.layoutManager = LinearLayoutManager(context)

                lifecycleScope.launch {

                    try {

                        val response = withContext(Dispatchers.IO){
                            wishListAPI.getExchangesInWishList(user_id!!)
                        }

                        if (response.isSuccessful){
                            val exchangesFavoriteGoodsList = response.body()
                            val sortedExchangesFavoriteGoodsList = exchangesFavoriteGoodsList!!.sortedByDescending { it.createdAt }
                            withContext(Dispatchers.Main){
                                exchangeAdapter.submitList(sortedExchangesFavoriteGoodsList)
                            }
                        }

                    }catch (e : Exception){

                    }

                }
            }
        }
    }

    override fun onPause(){
        super.onPause()
        isDecorateCheck = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onNotePurchaseDetail(goods_id: Long) {
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
        val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
        Log.d("REVISEEE", "userIdx : {$userIdx}")

        if (isGeneralPurchaseSelected){
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO){
                        goodsAPI.getGoodsById(goods_id)
                    }
                    withContext(Dispatchers.Main){
                        val sellerID = response.users_id.id

                        if(userIdx != sellerID){
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
        }else{
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO){
                        exchangesAPI.getGoodsById(goods_id)
                    }
                    withContext(Dispatchers.Main){
                        val sellerID = response.user.id

                        if(userIdx != sellerID){
                            //Toast.makeText(requireContext(), goods_id.toString(), Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), ExchangeDetailActivity::class.java)
                            intent.putExtra("goods_id", goods_id.toString())
                            startActivity(intent)
                        } else {
                            val intent = Intent(requireContext(), ExchangeModifyActivity::class.java)
                            intent.putExtra("goods_id", goods_id.toString())
                            startActivity(intent)
                        }
                    }
                }catch (e : Exception){

                }

            }
        }


    }
}

