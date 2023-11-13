package com.umc.insider

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.umc.insider.adapter.ChatRoomAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivityChatRoomBinding
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.api.MessageInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.model.MessageGetRes
import com.umc.insider.retrofit.model.MessagePostReq
import com.umc.insider.viewmodel.ChatRoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatRoomBinding
    private lateinit var adapter : ChatRoomAdapter
    private var chatRoom_id : Long? = null
    private var sender_id : Long? = null
    private val userAPI = RetrofitInstance.getInstance().create(UserInterface::class.java)
    private val messageAPI = RetrofitInstance.getInstance().create(MessageInterface::class.java)
    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
    private val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
    private val chattingAPI = RetrofitInstance.getInstance().create(ChattingInterface::class.java)
    private var pollingJob: Job? = null
    private var currentChatList: List<MessageGetRes> = emptyList()
    private var first = true

    override fun onResume() {
        super.onResume()
        chatRoom_id = intent.getStringExtra("chatRoom_id")!!.toLong()
        sender_id = UserManager.getUserIdx(this)!!.toLong()
        startPolling()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var goods_id = intent.getStringExtra("goods_id")!!.toLong()
        chatRoom_id = intent.getStringExtra("chatRoom_id")!!.toLong()
        sender_id = UserManager.getUserIdx(this)!!.toLong()
        var status = intent.getIntExtra("status", 0)
        var interlocutor : Long = -1

        lifecycleScope.launch {

        }

        lifecycleScope.launch {

            try {

                val response = withContext(Dispatchers.IO){
                    chattingAPI.getChatRoomByChatRoomId(chatId = chatRoom_id!!)
                }

                if (response.isSuccess){
                    val chatRoomInfo = response.result
                    goods_id = chatRoomInfo!!.goodsOrExchangesId
                    status = chatRoomInfo.status
                    if (chatRoomInfo.sellerId == sender_id){
                        interlocutor = chatRoomInfo.buyerId
                    }else{
                        interlocutor = chatRoomInfo.sellerId
                    }
                }

            }catch (e : Exception){

            }

            try {
                val response = withContext(Dispatchers.IO){
                    userAPI.getUserById(interlocutor)
                }
                binding.interlocutor.text = response.nickname
            }catch (e : Exception){

            }

            when(status) {
                0 -> {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            goodsAPI.getGoodsById(goods_id)
                        }

                        withContext(Dispatchers.Main) {
                            binding.goodsName.text = response.name
                            binding.goodsPrice.text = "${response.price}원"
                            Glide.with(binding.goodsImg.context)
                                .load(response.img_url)
                                .placeholder(null)
                                .into(binding.goodsImg)
                            //binding.interlocutor.text = response.users_id.nickname
                            //binding.interlocutorRating.text = null
                        }
                    } catch (e: Exception) {
                        // 예외 처리를 해주세요.
                    }
                }

                1 -> {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            exchangesAPI.getGoodsById(goods_id)
                        }

                        withContext(Dispatchers.Main) {
                            binding.goodsName.text = response.name
                            binding.goodsPrice.text = "교환 원하는 상품: ${response.wantItem}"
                            Glide.with(binding.goodsImg.context)
                                .load(response.imageUrl)
                                .placeholder(null)
                                .into(binding.goodsImg)
                            //binding.interlocutor.text = response.user.nickname
                            //binding.interlocutorRating.text = null
                        }
                    } catch (e: Exception) {
                        // 예외 처리를 해주세요.
                    }
                }
            }
        }


        initView()

    }

    private fun initView(){

        with(binding){
            adapter = ChatRoomAdapter(this@ChatRoomActivity)
            chatRV.adapter = adapter
            chatRV.layoutManager = LinearLayoutManager(this@ChatRoomActivity)

            sendBtn.setOnClickListener {

                if(binding.chatEditTextView.text.isNullOrBlank()) return@setOnClickListener

                lifecycleScope.launch {

                    val content = chatEditTextView.text.toString()
                    val messagePostReq = MessagePostReq(chatRoom_id!!,sender_id!!,content)

                    try {
                        val response = withContext(Dispatchers.IO){
                            messageAPI.createMessage(messagePostReq)
                        }
                        if (response.isSuccessful){
                            chatEditTextView.text = null
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    messageAPI.getMessageesInChatRoom(chatRoom_id!!)
                                }
                                if (response.isSuccessful) {
                                    val chatList = response.body()

                                    withContext(Dispatchers.Main) {
                                        adapter.submitList(chatList) {
                                            binding.chatRV.smoothScrollToPosition(adapter.itemCount - 1)
                                        }
                                    }
                                    currentChatList = chatList!!
                                } else { }
                            } catch (e: Exception) { }

                        }else{ }
                    }catch (e : Exception){

                    }
                }
                chatEditTextView.requestFocus()
            }

            chatRV.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                var previousHeight = 0
                var previousFirstVisibleItem = -1
                var previousTopOffset = 0

                override fun onGlobalLayout() {
                    val r = Rect()
                    chatRV.getWindowVisibleDisplayFrame(r)
                    val visibleHeight = r.bottom - r.top // 보이는 화면
                    Log.d("키보드", "visibleHeight : "+visibleHeight.toString())
                    val diffHeight = previousHeight - visibleHeight //
                    Log.d("키보드", "diffHeight : "+diffHeight.toString())


                    val layoutManager = chatRV.layoutManager as LinearLayoutManager

                    if (diffHeight > 0) {  // 키보드가 올라왔을 때
                        if (previousFirstVisibleItem == -1) {  // 이전 위치가 저장되지 않았을 때만 저장
                            previousFirstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                            val view = layoutManager.findViewByPosition(previousFirstVisibleItem)
                            previousTopOffset = view?.top ?: 0

                            // 키보드 높이만큼 아래로 스크롤 조정
                            chatRV.scrollBy(0, diffHeight)
                        }
                    } else if (diffHeight < 0) {  // 키보드가 내려갔을 때
                        if (previousFirstVisibleItem != -1) {
                            layoutManager.scrollToPositionWithOffset(previousFirstVisibleItem, previousTopOffset)
                            previousFirstVisibleItem = -1  // 위치 초기화
                        }
                    }

                    previousHeight = visibleHeight
                }
            })

            menuBtn.setOnClickListener {
                val popupMenu = PopupMenu(this@ChatRoomActivity, it)
                menuInflater?.inflate(R.menu.chat_room_menu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.purchaseCompleted -> {
                            CoroutineScope(Dispatchers.IO).launch{

                                try {
                                    val response = withContext(Dispatchers.IO){chattingAPI.purchase(chatRoom_id!!, sender_id!!)}
                                    if (response.isSuccessful){
                                        Toast.makeText(this@ChatRoomActivity, "거래 완료를 누르셨습니다. \n변경하실 수 없습니다.",Toast.LENGTH_SHORT).show()
                                    }
                                }catch (e : Exception){}
                            }
                            return@setOnMenuItemClickListener true
                        }
                        else->{
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
            }
        }
    }

    private fun startPolling() {
        pollingJob = lifecycleScope.launch {
            while (isActive) {  // 코루틴이 active한 동안 계속 실행
                try {
                    val response = withContext(Dispatchers.IO) {
                        messageAPI.getMessageesInChatRoom(chatRoom_id!!)
                    }
                    if (response.isSuccessful) {
                        val chatList = response.body()


                        withContext(Dispatchers.Main) {
                            adapter.submitList(chatList){

                                if (first && currentChatList.isNotEmpty()){
                                    binding.root.post{
                                        binding.chatRV.scrollToPosition(adapter.itemCount - 1)
                                        binding.progressBar.visibility = View.INVISIBLE
                                        binding.chatRV.visibility = View.VISIBLE
                                    }
                                    first = false
                                }
                            }
                        }
                        currentChatList = chatList!!
                        if(currentChatList.isEmpty()){
                            binding.chatRV.visibility = View.VISIBLE
                        }
                    } else { }
                } catch (e: Exception) {
                }
                delay(1000L)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
    }


    override fun onPause() {
        super.onPause()
        stopPolling()
        binding.progressBar.visibility = View.VISIBLE
        binding.chatRV.visibility = View.INVISIBLE
    }

}