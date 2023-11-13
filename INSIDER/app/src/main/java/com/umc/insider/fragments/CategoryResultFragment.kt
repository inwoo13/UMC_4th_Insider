package com.umc.insider.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.CategoryImgAdapter
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentCategoryResultBinding
import com.umc.insider.databinding.FragmentHomeBinding
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import com.umc.insider.utils.CategoryClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryResultFragment : Fragment(), CategoryClickListener, OnNoteListener{

    private var _binding : FragmentCategoryResultBinding? = null
    private val binding get() = _binding!!

    private val imageArray = intArrayOf(R.drawable.category_fruit,R.drawable.category_meat_egg,R.drawable.category_vegetable,R.drawable.category_dairy_product,R.drawable.category_seafood_driedfish,R.drawable.category_etc)
    private val clickImageArray = intArrayOf(R.drawable.category_fruit_click,R.drawable.category_meat_egg_click,R.drawable.category_vegetable_click,R.drawable.category_dairy_product_click,R.drawable.category_seafood_driedfish_click,R.drawable.category_etc_click)
    private val categoryTextArray = mutableListOf<String>("과일", "정육/계란", "채소", "유제품", "수산/건어물", "기타")

    private lateinit var categoryImgAdapter : CategoryImgAdapter
    private lateinit var goodsAdapter : GoodsLongAdapter

    private var selectPosition : Int? = null

    override fun onResume() {
        super.onResume()
        //Toast.makeText(requireContext(), "언제 나올까", Toast.LENGTH_SHORT).show()
        if (selectPosition == null) return
        else onImageTouch(selectPosition!!)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryResultBinding.inflate(inflater, container, false)

        selectPosition = arguments?.getString("select_category")!!.toInt()
        goodsAdapter = GoodsLongAdapter(this)
        categoryImgAdapter = CategoryImgAdapter(imageArray,clickImageArray, selectPosition!!, this)

        initView()

        return binding.root
    }

    private fun initView(){

        with(binding){
            categoryRecyclerView.adapter = categoryImgAdapter
            categoryRecyclerView.layoutManager = GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false )

            categorySearchRV.adapter = goodsAdapter
            categorySearchRV.layoutManager = LinearLayoutManager(context)
            categorySearchRV.addItemDecoration(SearchResultAdapterDecoration())

            searchImg.setOnClickListener {
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

    override fun onImageTouch(position: Int) {
        // 여기서 api 호출 - category
        //Toast.makeText(context, categoryTextArray[position], Toast.LENGTH_SHORT).show()
        selectPosition = position

        val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)

        val categoryIdx = (position + 1).toLong()

        lifecycleScope.launch {

            try {

                val response = goodsAPI.getGoodsByCategoryId(categoryIdx)

                if (response.isSuccessful){
                    val categoryGoodsList = response.body()
                    val sortedGoodsList = categoryGoodsList?.sortedByDescending { it.goods_id }
                    goodsAdapter.submitList(sortedGoodsList)
                }else{

                }

            }catch (e : Exception){

            }
        }

    }

    override fun onNotePurchaseDetail(goods_id: Long) {
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
        Log.d("REVISEEE", "userIdx : {$userIdx}")
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
    }
}