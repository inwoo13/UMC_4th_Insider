package com.umc.insider.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umc.insider.OnNoteListener
import com.umc.insider.R
import com.umc.insider.adapter.CategoryAdapter
import com.umc.insider.adapter.DiscountGoodsAdapter
import com.umc.insider.adapter.GoodsShortAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentHomeBinding
import com.umc.insider.model.SearchItem
import com.umc.insider.purchase.PurchaseDetailActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.api.WishListInterface
import com.umc.insider.revise.SaleReviseDetailActivity
import com.umc.insider.saleregistraion.SalesRegistrationActivity
import com.umc.insider.utils.CategoryClickListener
import com.umc.insider.utils.changeStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), CategoryClickListener, OnNoteListener {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val imageArray = intArrayOf(R.drawable.category_fruit,R.drawable.category_meat_egg,R.drawable.category_vegetable,R.drawable.category_dairy_product,R.drawable.category_seafood_driedfish,R.drawable.category_etc)
    private val clickImageArray = intArrayOf(R.drawable.category_fruit_click,R.drawable.category_meat_egg_click,R.drawable.category_vegetable_click,R.drawable.category_dairy_product_click,R.drawable.category_seafood_driedfish_click,R.drawable.category_etc_click)
    private val categoryAdapter = CategoryAdapter(imageArray, clickImageArray, this)
    private val categoryTextArray = mutableListOf<String>("과일", "정육/계란", "채소", "유제품", "수산/건어물", "기타")
    private val discountGoodsAdapter = DiscountGoodsAdapter()
    private val goodsShortAdapter = GoodsShortAdapter(this)
    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
    private val wishListAPI = RetrofitInstance.getInstance().create(WishListInterface::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        activity?.changeStatusBarColor(statusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()

        return binding!!.root
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
            //채팅 목록
            messageImg.setOnClickListener {
                val chatListFragment = ChatListFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, chatListFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }

            // 판매 등록 바로가기
            sellLayout.setOnClickListener {
                val intent = Intent(requireContext(), SalesRegistrationActivity::class.java)
                startActivity(intent)
            }

            hotGoodsListShow.setOnClickListener {
                val hotGoodsFragment = HotGoodsFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, hotGoodsFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }

            pointLayout.setOnClickListener {
                return@setOnClickListener
            }

            categoryRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            categoryRV.addItemDecoration(CategoryAdapterDecoration())
            categoryRV.adapter = categoryAdapter

            hotGoodsRV.adapter = goodsShortAdapter
            hotGoodsRV.layoutManager= GridLayoutManager(context, 2)
            hotGoodsRV.addItemDecoration(DiscountAdapterDecoration())

            lifecycleScope.launch {

                try {
                    val response = withContext(Dispatchers.IO){
                        wishListAPI.getHotGoods()
                    }

                    if (response.isSuccess){
                        val hotList = response.result
                        if(!hotList.isNullOrEmpty()){
                            val fourHotList = hotList.take(4)
                            withContext(Dispatchers.Main){
                                goodsShortAdapter.submitList(fourHotList)
                            }
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

    override fun onImageTouch(position: Int) {
        val categoryResultFragment = CategoryResultFragment().apply {
            arguments = Bundle().apply {
                putString("select_category", position.toString())
            }
        }
        val transaction = parentFragmentManager.beginTransaction()

        transaction.replace(R.id.frame_layout, categoryResultFragment)
        transaction.addToBackStack(null)

        transaction.commit()
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

class CategoryAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.right = -5
    }
}

class DiscountAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if(position % 2 == 0) outRect.right = 10
        else outRect.left = 10

        outRect.bottom = 20
    }
}