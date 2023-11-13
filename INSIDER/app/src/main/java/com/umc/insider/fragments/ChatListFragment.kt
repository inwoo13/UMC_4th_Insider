package com.umc.insider.fragments

import ChatListAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.umc.insider.ChatRoomActivity
import com.umc.insider.DeleteChatActivity
import com.umc.insider.EditProfileActivity
import com.umc.insider.R
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.FragmentChatListBinding
import com.umc.insider.model.ChatListItem
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ChattingInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.utils.ChatListClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatListFragment : Fragment(), ChatListClickListener {


    private var _binding : FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatListAdapter : ChatListAdapter
    private val chattingAPI = RetrofitInstance.getInstance().create(ChattingInterface::class.java)

    private var pollingJob: Job? = null

    private fun startPolling() {
        pollingJob = lifecycleScope.launch {
            while (isActive) {  // 코루틴이 active한 동안 계속 실행
                val user_id = UserManager.getUserIdx(requireContext())!!.toLong()

                try {
                    val response = withContext(Dispatchers.IO) {
                        chattingAPI.getChatRoomByUser(user_id)
                    }
                    if (response.isSuccessful) {
                        val chatRoomList = response.body()
                        val sortedChatRoomsList = chatRoomList!!.sortedByDescending { it.createdAt }
                        withContext(Dispatchers.Main) {
                            chatListAdapter.submitList(sortedChatRoomsList)
                        }
                    }
                } catch (e: Exception) {
                }

                delay(1000L)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentChatListBinding.inflate(inflater, container, false)


        chatListAdapter = ChatListAdapter(this)

        binding.deleteTextView.setOnClickListener{
            startActivity(Intent(activity, DeleteChatActivity::class.java))
        }

        binding.chatList.layoutManager = LinearLayoutManager(requireContext())
        binding.chatList.adapter = chatListAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun stopPolling() {
        pollingJob?.cancel()
    }
    override fun onPause() {
        super.onPause()
        stopPolling()
    }

    override fun onResume() {
        super.onResume()
        startPolling()
    }

    override fun ChatListItemClick(chatRoomId : Long, goodsId : Long) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("chatRoom_id", chatRoomId.toString())
        intent.putExtra("goods_id",goodsId.toString())
        startActivity(intent)
    }

}