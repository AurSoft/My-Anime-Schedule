package website.aursoft.myanimeschedule.ui.home

import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.base.BaseRecyclerViewAdapter
import website.aursoft.myanimeschedule.data.Anime

class AnimeListAdapter(callBack: (selectedAnime: Anime) -> Unit) :
    BaseRecyclerViewAdapter<Anime>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_anime
}