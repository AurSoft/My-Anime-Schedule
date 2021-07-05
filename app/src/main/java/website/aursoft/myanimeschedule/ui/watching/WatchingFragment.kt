package website.aursoft.myanimeschedule.ui.watching

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import website.aursoft.myanimeschedule.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import website.aursoft.myanimeschedule.MainActivity
import website.aursoft.myanimeschedule.base.BaseFragment
import website.aursoft.myanimeschedule.databinding.FragmentWatchingBinding
import website.aursoft.myanimeschedule.ui.home.AnimeListAdapter

class WatchingFragment : BaseFragment() {

    override val _viewModel: WatchingViewModel by viewModel()
    private lateinit var binding: FragmentWatchingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_watching, container, false)
        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(View.VISIBLE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("WatchingFragment", "onDestroyView")
    }

    private fun setupRecyclerView() {
        val adapter = AnimeListAdapter {
            this.findNavController().navigate(WatchingFragmentDirections.actionNavigationWatchingToAnimeDetails(it))
        }
        binding.watchingAnimeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.watchingAnimeRecyclerView.adapter = adapter
    }
}