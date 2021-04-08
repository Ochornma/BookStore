package com.promisebooks.app.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.navigation.navOptions
import com.promisebooks.app.R

class BookView {
     companion object {
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


         fun alert(message: String, view: View?, context: Activity, onback: Boolean) {
             if (view != null){
                 view.visibility = View.GONE
             }
             val builder: AlertDialog.Builder? = context.let { AlertDialog.Builder(it) }
             builder?.setNegativeButton(context.resources?.getString(R.string.cancel)) { dialog, _ ->
                 dialog.dismiss()
                 // Since we cannot proceed, we should finish the activity
             }
             builder?.setMessage(message)
             builder?.create()?.show()
             if(onback)
                 context.onBackPressed()
         }

     }
}