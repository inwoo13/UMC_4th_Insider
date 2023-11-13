package com.umc.insider.revise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.umc.insider.R
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivitySaleReviseDetailBinding
import com.umc.insider.purchase.PurchaseActivity
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.GoodsInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SaleReviseDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySaleReviseDetailBinding

    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sale_revise_detail)

        val goods_id = intent.getStringExtra("goods_id")!!.toLong()

        // 가져오기
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
                        val weightText = if (response.weight.toLong() >= 1000) {
                            String.format("%.1fkg", response.weight.toLong() / 1000.0)
                        } else {
                            "${response.weight}g"
                        }
                        binding.PurchaseTotalamountTv.text = "$weightText"
                    }

                    binding.PurchaseExpirationDate.text= response.shelf_life
                    binding.sellerInfo.text = response.users_id.nickname
                    if (response.sale_price != null){
                        binding.productPrice.text = "${response.sale_price}원"
                    }else{
                        binding.productPrice.text = "${response.price}원"
                    }
                    Log.d("detailAddresson", response.userZipCode.toString())
                    Log.d("detailAddresson", response.detailAddress)
                    binding.purchaseLocation.text = response.detailAddress

                    Glide.with(binding.productImage.context)
                        .load(response.img_url)
                        .placeholder(null)
                        .into(binding.productImage)
                }
            }catch (e : Exception){

            }
        }

        binding.sellRegistorBtn.setOnClickListener {

            UserManager.getUserIdx(this@SaleReviseDetailActivity)

            val intent = Intent(this@SaleReviseDetailActivity, SaleReviseRegistrationActivity::class.java)
            intent.putExtra("goods_id", goods_id.toString())
            intent.putExtra("sellOrExchange", "sell")
            startActivity(intent)
            finish()

        }

        // 상품 삭제
        binding.deleteBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val responseId = withContext(Dispatchers.IO){goodsAPI.deleteGoods(goods_id)}
                    Log.d("APIII", "Deleted goods with ID: $responseId")
                    withContext(Dispatchers.Main){
                        Toast.makeText(applicationContext, "상품이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("APIII", "Failed to delete goods", e)
                    Toast.makeText(applicationContext, "상품 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }

        initview()

    }

    private fun initview(){
        with(binding){

        }
    }

}