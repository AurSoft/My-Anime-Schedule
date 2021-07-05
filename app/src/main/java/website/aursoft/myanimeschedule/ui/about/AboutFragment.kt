package website.aursoft.myanimeschedule.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import website.aursoft.myanimeschedule.MainActivity
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.utils.setTitle


class AboutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle(getString(R.string.about))
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(View.GONE)
    }
}