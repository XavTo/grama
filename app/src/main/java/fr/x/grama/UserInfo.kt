package fr.x.grama

import android.content.Context
import android.content.SharedPreferences

class UserInfo {
    companion object {
        var email: String = ""
        var pseudo: String = ""
        var wins: Int = 0
        var losses: Int = 0
        var sp: SharedPreferences? = null
        fun isLogged(): Boolean {
            if (email == "" || pseudo == "") {
                return false
            }
            return true
        }
        fun setSp(context: Context) {
            sp = context.getSharedPreferences("fr.x.grama", Context.MODE_PRIVATE)
        }
    }
}