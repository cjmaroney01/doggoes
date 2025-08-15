package com.colinmaroney.core.ui

import androidx.annotation.DrawableRes
import com.colinmaroney.core.R
import kotlin.random.Random

enum class RandomDog(@DrawableRes val resId: Int) {
    BLOSSOM(R.drawable.blossom),
    CORDELIA(R.drawable.cordelia),
    DAISY(R.drawable.daisy),
    EMMA(R.drawable.emma),
    GABBY(R.drawable.gabby),
    GUS(R.drawable.gus),
    LIESEL(R.drawable.liesel),
    MADDY(R.drawable.maddy),
    NOEL(R.drawable.noel),
    OLIVER(R.drawable.oliver),
    OSCAR(R.drawable.oscar),
    RIPLEY(R.drawable.ripley),
    SCARLETT(R.drawable.scarlett),
    STRYKER(R.drawable.stryker),
    TOAST(R.drawable.toast),
    ZINNY(R.drawable.zinny);

    companion object {
        fun getRandomDog(): RandomDog {
            val i = Random.nextInt(0, entries.size)
            return entries[i]
        }
    }
}
