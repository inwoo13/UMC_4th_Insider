package com.umc.insider

import DeleteChatListAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.insider.databinding.ActivityDeleteChatBinding
import com.umc.insider.model.DeleteChatItem

class DeleteChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up Data Binding
        val binding: ActivityDeleteChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_delete_chat)

        // Sample data for DeleteChatList (Replace with your actual data)
        val chatListData = createSampleChatList()

        // Create DeleteChatListAdapter
        val deleteChatListAdapter = DeleteChatListAdapter(chatListData)

        // Set LayoutManager and Adapter for RecyclerView using Data Binding
        binding.deleteChatList.layoutManager = LinearLayoutManager(this)
        binding.deleteChatList.adapter = deleteChatListAdapter
    }

    // Sample data for DeleteChatList (Replace with your actual data)
    private fun createSampleChatList(): List<DeleteChatItem> {
        val chatList = ArrayList<DeleteChatItem>()
        chatList.add(DeleteChatItem("User1", "안녕하세요. 교환 가능 하실까요?",true))
        chatList.add(DeleteChatItem("User2", "네, 가능합니다. 어떤 장소에서 교환하면 될까요?",true))
        chatList.add(DeleteChatItem("User3", "오늘은 어떤 시간이 가능하신가요?",true))
        // Add more chat items as needed
        return chatList
    }
}
