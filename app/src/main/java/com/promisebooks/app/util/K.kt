package com.promisebooks.app.util

import androidx.navigation.navOptions
import com.promisebooks.app.R

class K {
     companion object{
         val options = navOptions {
             anim {
                 enter = R.anim.slide_in_right
                 exit = R.anim.slide_out_left
                 popEnter = R.anim.slide_in_left
                 popExit = R.anim.slide_out_right
             }
         }

         val collection = "Books"
         val query = "price"
     }

}