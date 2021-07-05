package website.aursoft.myanimeschedule.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.databinding.FragmentDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import website.aursoft.myanimeschedule.MainActivity
import website.aursoft.myanimeschedule.base.BaseFragment
import website.aursoft.myanimeschedule.utils.setTitle

class AnimeDetailsFragment : BaseFragment() {
    private lateinit var binding: FragmentDetailsBinding
    override val _viewModel: AnimeDetailsViewModel by viewModel()
    private val args: AnimeDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        binding.lifecycleOwner = this
        if(_viewModel.selectedAnime.value == null) {
            _viewModel.selectedAnime.value = args.selectedAnime
        }
        binding.viewModel = _viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.getMostRecentVersionOfAnime(args.selectedAnime)
        _viewModel.selectedAnime.observe(viewLifecycleOwner, Observer {
            setTitle(it.title)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(View.GONE)
    }

}