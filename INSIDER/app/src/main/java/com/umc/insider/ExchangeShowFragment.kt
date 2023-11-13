package com.umc.insider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.databinding.FragmentExchangeShowBinding
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ExchangeShowFragment : Fragment() {

    private var _binding : FragmentExchangeShowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExchangeShowBinding.inflate(inflater, container, false)

        val goods_id = arguments?.getString("ExchangeShw")!!.toLong()
        val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)

        lifecycleScope.launch {

            try {

                val response = exchangesAPI.getGoodsById(goods_id)

                withContext(Dispatchers.Main){

                    Glide.with(binding.productImage.context)
                        .load(response.imageUrl)
                        .placeholder(null)
                        .transform(RoundedCorners(30))
                        .into(binding.productImage)

                    binding.productNameTitle.text = response.title
                    binding.productName.text = response.name
                    if (response.weight.isNullOrBlank()){
                        binding.productAmount.text = "(${response.count}개)"
                        binding.PurchaseTotalamountTv.text = "${response.count}g"
                    }else{
                        binding.productAmount.text = "(${response.weight}g)"
                        binding.PurchaseTotalamountTv.text = "${response.count}개"
                    }
                    binding.PurchaseExpirationDate.text = response.shelfLife
                    binding.purchaseLocation.text = response.detail
                    binding.sellerInfo.text = response.user.nickname

                }

            }catch (e : Exception){

            }
        }

        return binding.root
    }


}