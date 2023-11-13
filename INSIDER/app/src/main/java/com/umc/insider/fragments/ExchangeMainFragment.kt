package com.umc.insider.fragments

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.CategoryAdapter
import com.umc.insider.adapter.CategoryImgAdapter
import com.umc.insider.adapter.ExchangeAdapter
import com.umc.insider.adapter.GoodsLongAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentExchangeMainBinding
import com.umc.insider.exchange.ExchangeDetailActivity
import com.umc.insider.exchange.ExchangeModifyActivity
import com.umc.insider.model.ExchangeItem
import com.umc.insider.model.SearchItem
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import com.umc.insider.utils.CategoryClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExchangeMainFragment : Fragment(), CategoryClickListener, OnNoteListener {

    private var _binding : FragmentExchangeMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var getResultText: ActivityResultLauncher<Intent>
    private val adapter = ExchangeAdapter(this)

    private val imageArray = intArrayOf(R.drawable.category_fruit,R.drawable.category_meat_egg,R.drawable.category_vegetable,R.drawable.category_dairy_product,R.drawable.category_seafood_driedfish,R.drawable.category_etc)
    private val clickImageArray = intArrayOf(R.drawable.category_fruit_click,R.drawable.category_meat_egg_click,R.drawable.category_vegetable_click,R.drawable.category_dairy_product_click,R.drawable.category_seafood_driedfish_click,R.drawable.category_etc_click)

    private var selectPosition : Int = -1
    private lateinit var categoryImgAdapter : CategoryImgAdapter
    val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)

    override fun onResume() {
        super.onResume()

        val categoryIdx = (selectPosition + 1).toLong()
        Log.d("교환", categoryIdx.toString())
        //Toast.makeText(requireContext(), selectPosition.toString(), Toast.LENGTH_SHORT).show()

        if (categoryIdx.toInt() == 0){
            lifecycleScope.launch {

                try {

                    val response = withContext(Dispatchers.IO){
                        exchangesAPI.getAllExchanges(null)
                    }

                    if (response.isSuccess){
                        val categoryExchangesList = response.result
                        val sortedExchangesList = categoryExchangesList?.sortedByDescending { it.id }
                        adapter.submitList(sortedExchangesList)
                    }else{

                    }

                }catch (e : Exception){

                }
            }
        }else{
            lifecycleScope.launch {

                try {

                    val response = withContext(Dispatchers.IO) {
                        exchangesAPI.getExchangesByCategoryId(categoryIdx)
                    }

                    if (response.isSuccessful){
                        val categoryExchangesList = response.body()
                        val sortedExchangesList = categoryExchangesList?.sortedByDescending { it.id }
                        adapter.submitList(sortedExchangesList)
                    }else{

                    }

                }catch (e : Exception){

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExchangeMainBinding.inflate(inflater, container, false)

        categoryImgAdapter = CategoryImgAdapter(imageArray,clickImageArray, -1, this)


        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){
            searchRV.adapter = adapter
            searchRV.layoutManager = LinearLayoutManager(context)
            searchRV.addItemDecoration(ExchangeAdapterDecoration())

            categoryRecyclerView.adapter = categoryImgAdapter
            categoryRecyclerView.layoutManager = GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false )

            lifecycleScope.launch {

                try {

                    val response = withContext(Dispatchers.IO){
                        exchangesAPI.getAllExchanges(null)
                    }

                    if (response.isSuccess){
                        val categoryExchangesList = response.result
                        val sortedExchangesList = categoryExchangesList?.sortedByDescending { it.id }
                        adapter.submitList(sortedExchangesList)
                    }else{

                    }

                }catch (e : Exception){

                }
            }

            searchImg.setOnClickListener {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.frame_layout, SearchFragment())
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ExchangeAdapterDecoration : RecyclerView.ItemDecoration() {

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

    override fun onNotePurchaseDetail(goods_id: Long) {

        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        Log.d("REVISEEE", "userIdx : {$userIdx}")
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

    override fun onImageTouch(position: Int) {
        selectPosition = position

        val categoryIdx = (position + 1).toLong()
        Log.d("교환", categoryIdx.toString())
        //Toast.makeText(requireContext(), selectPosition.toString(), Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {

            try {

                val response = withContext(Dispatchers.IO) {
                    exchangesAPI.getExchangesByCategoryId(categoryIdx)
                }

                if (response.isSuccessful){
                    val categoryExchangesList = response.body()
                    val sortedExchangesList = categoryExchangesList?.sortedByDescending { it.id }
                    adapter.submitList(sortedExchangesList)
                }else{

                }

            }catch (e : Exception){

            }
        }
    }

}