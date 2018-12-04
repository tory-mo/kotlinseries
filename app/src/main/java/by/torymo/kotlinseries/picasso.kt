package by.torymo.kotlinseries

import android.content.Context
import com.squareup.picasso.Picasso


public val Context.picasso: Picasso
    get() = Picasso.get()
