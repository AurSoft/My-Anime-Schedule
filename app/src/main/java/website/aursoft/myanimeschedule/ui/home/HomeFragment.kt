package website.aursoft.myanimeschedule.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import website.aursoft.myanimeschedule.MainActivity
import website.aursoft.myanimeschedule.base.BaseFragment
import website.aursoft.myanimeschedule.utils.WeekDaysForJikan
import website.aursoft.myanimeschedule.utils.getTodayAsString
import website.aursoft.myanimeschedule.utils.setTitle

class HomeFragment : BaseFragment() {

    override val _viewModel: HomeViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = _viewModel
        setHasOptionsMenu(true)
        binding.refreshLayout.setOnRefreshListener {
            _viewModel.refreshList()
            binding.refreshLayout.isRefreshing = false
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
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

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> false
        }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_schedule, menu)
            setOnMenuItemClickListener {
                _viewModel.setFiltering(
                    when(it.itemId) {
                        R.id.mon -> WeekDaysForJikan.MON
                        R.id.tue -> WeekDaysForJikan.TUE
                        R.id.wed -> WeekDaysForJikan.WED
                        R.id.thu -> WeekDaysForJikan.THU
                        R.id.fri -> WeekDaysForJikan.FRI
                        R.id.sat -> WeekDaysForJikan.SAT
                        else -> WeekDaysForJikan.SUN
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupRecyclerView() {
        val adapter = AnimeListAdapter {
            this.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToAnimeDetails(it))
        }
        binding.animeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.animeRecyclerView.adapter = adapter
    }
}