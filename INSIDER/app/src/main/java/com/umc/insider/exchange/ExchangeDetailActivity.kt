package com.umc.insider.exchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.R
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivityExchangeDetailBinding
import com.umc.insider.purchase.PurchaseActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.WishListInterface
import com.umc.insider.retrofit.model.WishListPostReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExchangeDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityExchangeDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exchange_detail)

        val user_id = UserManager.getUserIdx(this)!!.toLong()
        val goods_id = intent.getStringExtra("goods_id")!!.toLong()

        val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
        val wishListAPI = RetrofitInstance.getInstance().create(WishListInterface::class.java)

        val wishListPostReq = WishListPostReq(user_id ,goods_id, 1)

        lifecycleScope.launch {
            Log.d("찜", "시작")
            try {
                val response = withContext(Dispatchers.IO){
                    wishListAPI.checkWishList(user_id,goods_id, 1)
                }

                withContext(Dispatchers.Main) { binding.favoriteBtn.isChecked = response }
            }catch (e : Exception){

            }
        }

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
                        binding.PurchaseTotalamountTv.text = "${response.count}개"
                    }else{
                        val weightText = if (response.weight.toLong() >= 1000) {
                            String.format("%.1fkg", response.weight.toLong() / 1000.0)
                        } else {
                            "(${response.weight}g)"
                        }
                        binding.productAmount.text = "$weightText"
                        binding.PurchaseTotalamountTv.text = "${response.count}개"
                    }
                    binding.PurchaseExpirationDate.text = response.shelfLife
                    binding.purchaseLocation.text = response.detail
                    binding.sellerInfo.text = response.user.nickname

                }


            }catch (e : Exception){

            }
        }

        binding.favoriteBtn.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){

                CoroutineScope(Dispatchers.IO).launch {

                    try {

                        val response = wishListAPI.addGoodsToWishList(wishListPostReq)

                        if(response.isSuccessful){
                            withContext(Dispatchers.Main){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(this@ExchangeDetailActivity, "찜목록에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }catch (e : Exception){

                    }
                }

            }
            if(!isChecked){

                CoroutineScope(Dispatchers.IO).launch {

                    try {

                        val response = wishListAPI.deleteWishList(userId = user_id, goodsOrExchangesId = goods_id, status = 1)

                        if (response.isSuccessful){
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@ExchangeDetailActivity, "찜목록에 삭제했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }catch (e : Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ExchangeDetailActivity, "찜목록에 삭제했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }

        binding.ExchangeBtn.setOnClickListener {
            val intent = Intent(this@ExchangeDetailActivity, ExchangeActivity::class.java)
            intent.putExtra("goods_id", goods_id.toString())
            startActivity(intent)
            finish()
        }

    }
}