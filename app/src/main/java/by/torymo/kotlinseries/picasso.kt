package by.torymo.kotlinseries

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.picasso(url: String){
    Picasso.get().load(url).error(R.drawable.ic_no_photo).into(this)
}
