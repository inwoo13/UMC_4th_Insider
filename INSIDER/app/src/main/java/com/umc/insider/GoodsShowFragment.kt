package com.umc.insider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentGoodsShowBinding
import com.umc.insider.databinding.FragmentMyPageBinding
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.GoodsInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoodsShowFragment : Fragment() {

    private var _binding : FragmentGoodsShowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGoodsShowBinding.inflate(inflater, container, false)

        val goods_id = arguments?.getString("PurchaseOrSale")!!.toLong()
        val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    goodsAPI.getGoodsById(goods_id)
                }
                withContext(Dispatchers.Main){
                    binding.productNameTitle.text = response.title
                    binding.productName.text = response.name
                    binding.PurchaseUnitamountTv.text = null

                    if(response.weight.isNullOrBlank()){
                        binding.productUnit.text = "(개당)"
                        binding.PurchaseTotalamountTv.text = "${response.rest}개"
                    }else{
                        binding.productUnit.text = "(100g당)"
                        binding.PurchaseTotalamountTv.text = "${response.weight}g"
                    }

                    binding.PurchaseExpirationDate.text= response.shelf_life
                    binding.sellerInfo.text = response.users_id.nickname
                    if (response.sale_price != null){
                        binding.productPrice.text = "${response.sale_price}원"
                    }else{
                        binding.productPrice.text = "${response.price}원"
                    }
                    binding.purchaseLocation.text = response.detailAddress

                    Glide.with(binding.productImage.context)
                        .load(response.img_url)
                        .placeholder(null)
                        .into(binding.productImage)
                }
            }catch (e : Exception){

            }

        }

        initview()

        return binding.root
    }

    private fun initview(){
        with(binding){

        }
    }

}