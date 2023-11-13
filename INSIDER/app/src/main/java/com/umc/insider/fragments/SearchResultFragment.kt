package com.umc.insider.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.ExchangeAdapter
import com.umc.insider.adapter.ExchangesLongAdapter
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.adapter.SearchResultAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentSearchResultBinding
import com.umc.insider.exchange.ExchangeDetailActivity
import com.umc.insider.exchange.ExchangeModifyActivity
import com.umc.insider.model.SearchItem
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import com.umc.insider.utils.changeStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultFragment : Fragment(), OnNoteListener {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var getResultText: ActivityResultLauncher<Intent>
    private val goodsAdapter = GoodsLongAdapter(this)
    private val exchangesAdapter =  ExchangeAdapter(this)


    // 일반구매 교환하기 select에서 사용
    private var isGeneralPurchaseSelected = true
    private var isDecorateCheck = true

    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
    private val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
    private var searchQuery : String? = null

    override fun onResume() {
        super.onResume()
        updateButtonUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarGreenColor)
        activity?.changeStatusBarColor(statusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        initView()
        updateButtonUI()
        searchQuery = arguments?.getString("search_query")
        binding.searchText.text = "\"$searchQuery\" 검색 결과"


        return binding.root
    }

    private fun initView() {
        with(binding) {
            searchRV.adapter = goodsAdapter
            searchRV.layoutManager = LinearLayoutManager(context)
            searchRV.addItemDecoration(SearchResultAdapterDecoration())

            searchBtn.setOnClickListener {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.frame_layout, SearchFragment())
                transaction?.addToBackStack(null)
                transaction?.commit()
            }

            // 일반, 교환 select
            selectPurchase.setOnClickListener {
                isGeneralPurchaseSelected = true
                updateButtonUI()
            }
            Exchange.setOnClickListener {
                isGeneralPurchaseSelected = false
                updateButtonUI()
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

        if(isGeneralPurchaseSelected){
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

    private fun updateButtonUI() {
        with(binding) {

            if (isGeneralPurchaseSelected) {
                generalPurchase.background = ContextCompat.getDrawable(requireContext(), R.drawable.green_left_round)
                generalPurchase.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                Exchange.background = ContextCompat.getDrawable(requireContext(), R.drawable.white_right_round)
                Exchange.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))

                searchRV.adapter = goodsAdapter
                searchRV.layoutManager = LinearLayoutManager(context)

                lifecycleScope.launch {

                    try {
                        val response = withContext(Dispatchers.IO) {
                            goodsAPI.getGoods(searchQuery)
                        }
                        if(response.isSuccess){
                            val goodsList = response.result
                            if (goodsList.isNullOrEmpty()) {
                                Toast.makeText(context, "찾으시는 상품이 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                val sortedGoodsList = goodsList.sortedByDescending { it.goods_id }
                                goodsAdapter.submitList(sortedGoodsList)
                            }

                        }else{

                            // 에러
                        }
                    }catch ( e : Exception){ //
                        //네트워크나 기타 오류
                    }
                }

            } else {
                generalPurchase.background = ContextCompat.getDrawable(requireContext(), R.drawable.white_left_round)
                generalPurchase.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))
                Exchange.background = ContextCompat.getDrawable(requireContext(), R.drawable.green_right_round)
                Exchange.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                searchRV.adapter = exchangesAdapter
                searchRV.layoutManager = LinearLayoutManager(context)

                lifecycleScope.launch {

                    try {
                        val response = withContext(Dispatchers.IO) {
                            exchangesAPI.getAllExchanges(searchQuery)
                        }
                        if(response.isSuccess){
                            val goodsList = response.result
                            if (goodsList.isNullOrEmpty()) {
                                Toast.makeText(context, "찾으시는 상품이 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                val sortedGoodsList = goodsList.sortedByDescending { it.id }
                                exchangesAdapter.submitList(sortedGoodsList)
                            }

                        }else{

                            // 에러
                        }
                    }catch ( e : Exception){ //
                        //네트워크나 기타 오류
                    }
                }

            }
        }
    }

}

class SearchResultAdapterDecoration : RecyclerView.ItemDecoration() {

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