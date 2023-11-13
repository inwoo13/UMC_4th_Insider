package com.umc.insider.fragments

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.umc.insider.EditProfileActivity
import com.umc.insider.R
import com.umc.insider.adapter.ExchangeEndAdapter
import com.umc.insider.adapter.ExchangingListAdapter
import com.umc.insider.adapter.ShoppingSaleAdapter
import com.umc.insider.auth.AutoLoginManager
import com.umc.insider.auth.TokenManager
import com.umc.insider.auth.UserManager
import com.umc.insider.auth.login.LogInActivity
import com.umc.insider.databinding.FragmentMyPageBinding
import com.umc.insider.model.ExchangeItem
import com.umc.insider.model.SearchItem
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.model.ExchangesPostRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class MyPageFragment : Fragment() {

    private var _binding : FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val shoppingListAdapter = ShoppingSaleAdapter()
    //private val saleListAdapter = ShoppingSaleAdapter()
    private val saleendListAdapter = ShoppingSaleAdapter()
    //private val exchangingListAdapter = ExchangingListAdapter()
    private val exchangeEndAdapter = ExchangeEndAdapter()

    private val REQUEST_CODE_EDIT_PROFILE = 100
    private val UserApi = RetrofitInstance.getInstance().create(UserInterface::class.java)
    private val chattingApi = RetrofitInstance.getInstance().create(ChattingInterface::class.java)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        Log.d("MYPAGEEE", "userIdx : {$userIdx}")

        lifecycleScope.launch{
            try {
                val response = withContext(Dispatchers.IO){
                    UserApi.getUserById(userIdx)
                }
                Log.d("MYPAGEEE", "$response")

                withContext(Dispatchers.Main){
                    binding.nicknameTextView.text = response.nickname + "님"
                    binding.idTextView.text = response.userId
                    binding.liveAddress.text = response.detailAddress

                    if(response.sellerOrBuyer == 1){
                        binding.buyerTextView.text = "팔아유"
                        binding.buyerTextView.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.yellow_round_rectangle)
                    }else{
                        binding.buyerTextView.text = "사유"
                        binding.buyerTextView.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.main_round_rectangle)
                    }

                    if(response.img != null) {
                        Glide.with(binding.profileImg.context)
                            .load(response.img)
                            .placeholder(null)
                            .into(binding.profileImg)
                    } else {

                    }
                }

            }catch( e : Exception){
                Log.e("MYPAGEEE", "$e")
            }
        }

        // 구매목록, 판매목록 가져오기
        lifecycleScope.launch {

            try {
                val response = chattingApi.getGoodsByUser(userIdx)

                if (response.isSuccessful){
                    val map = response.body()
                    val SaleGoodsList = map?.get("sale") // or "purchase" or any other key
                    val PurchaseGoodsList = map?.get("purchase")
                    saleendListAdapter.submitList(SaleGoodsList)
                    shoppingListAdapter.submitList(PurchaseGoodsList)

                }
            }catch (e : Exception ){

            }
        }

        // 교환목록 가져오기
        lifecycleScope.launch {
            try {
                val response = chattingApi.getExchangesByUser(userIdx)

                if (response.isSuccessful){
                    val map = response.body()
                    val ExchangeYourGoodsList = map?.get("Exchange your item")
                    val ExchangeMyGoodsList = map?.get("Exchange my item")

                    val combinedList = mutableListOf<ExchangesPostRes>()

                    if(ExchangeYourGoodsList != null){
                        combinedList.addAll(ExchangeYourGoodsList)
                    }

                    if(ExchangeMyGoodsList != null){
                        combinedList.addAll(ExchangeMyGoodsList)
                    }

                    exchangeEndAdapter.submitList(combinedList)
                    for ((listName, list) in map!!){
                        Log.d("교환 리스트", "$listName : $list")
                    }
                }
            }catch (e : Exception ){

            }
        }

        initView()

        return binding.root

    }

    private fun initView(){
        with(binding){
            //val dummies = DummyDate()
            // 구매완료
            shoppingListRV.adapter = shoppingListAdapter
            shoppingListRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            shoppingListRV.addItemDecoration(ShoppingSaleListAdapterDecoration())

            // 판매하기 판매완료
            saleEndListRV.adapter = saleendListAdapter
            saleEndListRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            saleEndListRV.addItemDecoration(ShoppingSaleListAdapterDecoration())

            // 교환하기 교환완료
            exchangeEndListRV.adapter = exchangeEndAdapter
            exchangeEndListRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            exchangeEndListRV.addItemDecoration(ShoppingSaleListAdapterDecoration())

            // 로그아웃
            logoutTextView.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
                googleSignInClient.signOut()

                TokenManager.clearToken(requireContext())
                UserManager.clearUserIdx(requireContext())
                UserManager.clearUserSellerOrBuyer(requireContext())
                startActivity(Intent(activity, LogInActivity::class.java))
                activity?.finish()
            }

            // 계정 탈퇴
            withdrawalTextView.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("계정 탈퇴")
                    .setMessage("계정을 삭제하시겠습니까?")
                    .setPositiveButton("네", DialogInterface.OnClickListener { dialog, which ->
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
                        googleSignInClient.signOut()

                        TokenManager.clearToken(requireContext())
                        UserManager.clearUserIdx(requireContext())
                        UserManager.clearUserSellerOrBuyer(requireContext())

                        startActivity(Intent(activity, LogInActivity::class.java))
                        activity?.finish()
                    })
                    .setNegativeButton("아니요", null)
                    .show()
            }

            // 내 정보 수정하기 화면으로 넘어가기
            editTV.setOnClickListener {
                val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                intent.putExtra("current_nickname", nicknameTextView.text.toString())
                intent.putExtra("current_address", liveAddress.text.toString())
                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
            }

            // 구매 목록 화면으로 이동
            shoppingListShow.setOnClickListener{
                val purchaseCatalogFragment = PurchaseCatalogFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, purchaseCatalogFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }

            // 판매 목록 화면으로 이동
            saleListShow.setOnClickListener {
                val sellCatalgFragment = SellCatalogFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, sellCatalgFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }

            // 교환 목록 화면으로 이동
            exchangeListShow.setOnClickListener {
                val exchangeCatalgFragment = ExchangeCatalogFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, exchangeCatalgFragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }

            detailReview.setOnClickListener {
                val searchFragment = ReviewListFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, searchFragment)
                    .addToBackStack(null)
                    .commit()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val userIdx = UserManager.getUserIdx(requireActivity().applicationContext)!!.toLong()
        Log.d("MYPAGEEE", "userIdx : {$userIdx}")

        lifecycleScope.launch{
            try {
                val response = withContext(Dispatchers.IO){
                    UserApi.getUserById(userIdx)
                }
                Log.d("MYPAGEEE", "$response")

                withContext(Dispatchers.Main){
                    binding.nicknameTextView.text = response.nickname + "님"
                    binding.idTextView.text = response.userId
                    binding.liveAddress.text = response.detailAddress

                    if(response.sellerOrBuyer == 1){
                        binding.buyerTextView.text = "팔아유"
                        binding.buyerTextView.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.yellow_round_rectangle)
                    }else{
                        binding.buyerTextView.text = "사유"
                        binding.buyerTextView.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.main_round_rectangle)
                    }

                    if(response.img != null) {
                        Glide.with(binding.profileImg.context)
                            .load(response.img)
                            .placeholder(null)
                            .into(binding.profileImg)
                    } else {

                    }
                }

            }catch( e : Exception){
                Log.e("MYPAGEEE", "$e")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            val editedNickname = data?.getStringExtra("edited_nickname")
            val editedAddress = data?.getStringExtra("edited_address")

            editedNickname?.let { binding.nicknameTextView.text = it }
            editedAddress?.let { binding.liveAddress.text = it }
        }
    }
}

class ShoppingSaleListAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.right = 20
    }
}
