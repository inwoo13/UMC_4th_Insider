package com.umc.insider.fragments

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.R
import com.umc.insider.adapter.RecentSearchesAdapter
import com.umc.insider.adapter.SearchTermsRankingAdapter
import com.umc.insider.databinding.FragmentSearchBinding
import com.umc.insider.model.RankingItem
import com.umc.insider.saveRecentSearch.SearchManager
import com.umc.insider.utils.SearchesItemClickListener
import com.umc.insider.utils.changeStatusBarColor

class SearchFragment : Fragment(),SearchesItemClickListener {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchTermsRankingAdapter = SearchTermsRankingAdapter(this)
    private val recentSearchesAdapter = RecentSearchesAdapter(this)
    private val searchManager : SearchManager by lazy { SearchManager(requireContext()) }

    override fun onResume() {
        super.onResume()
        binding.root.post {
            binding.searchView.isFocusable = true
            binding.searchView.isIconified = false
            binding.searchView.requestFocusFromTouch()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        activity?.changeStatusBarColor(statusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        //Toast.makeText(requireContext(), history.toString(), Toast.LENGTH_SHORT).show()

        initView()
        return binding.root
    }

    private fun initView(){
        with(binding){
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchKeyword(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    // text 바뀔 때마다
                    return false
                }

            })

            realTimeSearchTermsRankingRV.adapter = searchTermsRankingAdapter
            realTimeSearchTermsRankingRV.layoutManager = GridLayoutManager(context, 2)
            searchTermsRankingAdapter.submitList(DummyData())

            recentSearchesRV.adapter = recentSearchesAdapter
            recentSearchesRV.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            recentSearchesRV.addItemDecoration(RecentSearchesAdapterDecoration())
            recentSearchesAdapter.submitList(searchManager.getSearchHistory())

            removeAll.setOnClickListener {
                searchManager.clearSearchHistory()
                recentSearchesAdapter.submitList(searchManager.getSearchHistory())
            }
        }
    }

    // ranking이 1,6,2,7,3,8,4,9,5,10 이런 식으로 들어와야함
    private fun DummyData() : ArrayList<RankingItem>{
        val dummy1 = RankingItem(1, "양파")
        val dummy2 = RankingItem(2, "마늘")
        val dummy3 = RankingItem(3, "돼지고기")
        val dummy4 = RankingItem(4, "토마토")
        val dummy5 = RankingItem(5, "파")
        val dummy6 = RankingItem(6, "커피")
        val dummy7 = RankingItem(7, "감자")
        val dummy8 = RankingItem(8, "치즈")
        val dummy9 = RankingItem(9, "소고기")
        val dummy10 = RankingItem(10, "우유")

        val arr = ArrayList<RankingItem>()
        arr.add(dummy1)
        arr.add(dummy2)
        arr.add(dummy3)
        arr.add(dummy4)
        arr.add(dummy5)
        arr.add(dummy6)
        arr.add(dummy7)
        arr.add(dummy8)
        arr.add(dummy9)
        arr.add(dummy10)



        return SortAlgorithm(arr)
    }



    private fun SortAlgorithm(array : ArrayList<RankingItem>) : ArrayList<RankingItem>{
        val half = array.size / 2
        val newArray = arrayListOf<RankingItem>()

        for(i in 0 until half){
            newArray.add(array[i])
            newArray.add(array[i+half])
        }

        return newArray
    }


    override fun onClickSearch(searchTerm: String) {
        searchKeyword(searchTerm)
    }

    override fun onClickDelete(searchTerm: String) {
        searchManager.removeSearchWord(searchTerm)
        recentSearchesAdapter.submitList(searchManager.getSearchHistory())
    }

    fun searchKeyword(keyword : String){
        searchManager.addSearchWord(keyword)
        val searchResultFragment = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString("search_query", keyword)
            }
        }

        val transaction = parentFragmentManager.beginTransaction()

        transaction.replace(R.id.frame_layout, searchResultFragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }


    override fun onPause() {
        super.onPause()

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}

class RecentSearchesAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if(position != 4) outRect.right = 20
    }
}