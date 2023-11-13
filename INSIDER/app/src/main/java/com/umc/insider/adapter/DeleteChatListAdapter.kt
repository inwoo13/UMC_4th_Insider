import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.ChatDeleteItemBinding
import com.umc.insider.model.DeleteChatItem

class DeleteChatListAdapter(private val DeleteChatList: List<DeleteChatItem>) :
    RecyclerView.Adapter<DeleteChatListAdapter.DeleteChatListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteChatListViewHolder {
        val binding = ChatDeleteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeleteChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeleteChatListViewHolder, position: Int) {
        val currentChatItem = DeleteChatList[position]
        holder.bindData(currentChatItem)
    }

    override fun getItemCount(): Int {
        return DeleteChatList.size
    }

    inner class DeleteChatListViewHolder(private val binding: ChatDeleteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(DeleteChatItem: DeleteChatItem) {
            binding.interlocutor.text = DeleteChatItem.chatId
            binding.recentMessageTextView.text = DeleteChatItem.recentMessage
            binding.checkbox.isChecked = DeleteChatItem.isChecked
        }
    }
}
