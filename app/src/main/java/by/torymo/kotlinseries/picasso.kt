package by.torymo.kotlinseries

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.picasso(url: String){
    if(url.isNotEmpty())
        Picasso.get().load(url).error(R.drawable.ic_no_photo).into(this)
    else
        this.setImageResource(R.drawable.ic_no_photo)
}
