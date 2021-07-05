package website.aursoft.myanimeschedule.utils

import android.graphics.drawable.Icon
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.base.BaseRecyclerViewAdapter
import website.aursoft.myanimeschedule.data.Anime

object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    @BindingAdapter("remoteImage")
    @JvmStatic
    fun bindImageViewToUrlImage(imageView: ImageView, imageUrl: String) {
        Picasso.with(imageView.context)
            .load(imageUrl)
            .fit()
            .centerInside()
            .placeholder(R.drawable.no_image)
            .error(R.drawable.no_image)
            .into(imageView)
    }

    @BindingAdapter("numberText")
    @JvmStatic
    fun setNumberAsText(textView: TextView, number: Number?) {
        var numberText = ""
        if (number == 0.0 || number == 0 || number == null) {
            numberText = "N/A"
        } else {
            numberText = number.toString()
        }

        textView.text = numberText
    }

    @BindingAdapter("imageVisible")
    @JvmStatic
    fun setImageVisible(imageView: ImageView, visible: Boolean) {
        if (visible) {
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }
}