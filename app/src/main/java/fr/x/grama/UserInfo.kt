package fr.x.grama

class UserInfo {
    companion object {
        var email: String = ""
        var pseudo: String = ""
        fun isLogged(): Boolean {
            if (email == "" || pseudo == "") {
                return false
            }
            return true
        }
    }
}