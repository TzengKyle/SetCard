package tw.edu.ncku.iim.rsliu.setcard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tw.edu.ncku.iim.rsliu.setcard.Adapter.CardGridRecyclerViewAdapter
import tw.edu.ncku.iim.rsliu.setcard.databinding.FragmentCardGridBinding


class CardGridFragment : Fragment(), CardGridRecyclerViewAdapter.SetCardViewClickListener {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentCardGridBinding
    private lateinit var adapter: CardGridRecyclerViewAdapter

//    onCreateView: 使用 FragmentCardGridBinding 來綁定檔案跟介面
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardGridBinding.inflate(inflater, container, false)
        return binding.root
    }

//    onViewCreated: 設定了RecyclerView 的adapter等等，並設定重新開始遊戲和增加卡片的按鈕的click listener。
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        adapter = CardGridRecyclerViewAdapter(sharedViewModel)
        adapter.setSetCardViewClickListener(this)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val restartButton = binding.restartButton
        restartButton.setOnClickListener {
            sharedViewModel.restartGame()
            adapter.notifyDataSetChanged()
        }
        val add3Button = binding.plus3
        add3Button.setOnClickListener {
            sharedViewModel.addCards()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onSetCardViewClick(position: Int, cardView: SetCardView) {
        var cardIndex = when (cardView.id) {
            R.id.setCardView1 -> 0
            R.id.setCardView2 -> 1
            R.id.setCardView3 -> 2
            else -> -1
        }

        val positionIdx = position * sharedViewModel.columns + cardIndex
        sharedViewModel.findValidSelection()
        sharedViewModel.update_selectedCardsIdxs(positionIdx) //主要就是這個

        adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //抓共有的shared model
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
}