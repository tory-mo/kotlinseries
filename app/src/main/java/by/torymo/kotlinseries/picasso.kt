package by.torymo.kotlinseries

import android.widget.ImageView
import by.torymo.kotlinseries.data.network.Requester
import com.squareup.picasso.Picasso

fun ImageView.picasso(url: String?){
    if(url != null && url.isNotEmpty())
        Picasso.get().load(Requester.POSTER_PATH + url).error(R.drawable.ic_no_photo).into(this)
    else
        this.setImageResource(R.drawable.ic_no_photo)
}
