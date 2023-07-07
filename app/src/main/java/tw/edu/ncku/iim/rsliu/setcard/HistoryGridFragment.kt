package tw.edu.ncku.iim.rsliu.setcard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tw.edu.ncku.iim.rsliu.setcard.Adapter.HistoryGridRecyclerViewAdapter
import tw.edu.ncku.iim.rsliu.setcard.databinding.FragmentHistoryGridBinding

class HistoryGridFragment : Fragment(){
    private lateinit var sharedViewModel: SharedViewModel // by viewModels()

    private lateinit var binding: FragmentHistoryGridBinding
    private lateinit var adapter: HistoryGridRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerViewHistory
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        adapter = HistoryGridRecyclerViewAdapter(sharedViewModel)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.historyLiveData.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
}