package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.x.grama.DatabaseManager
import fr.x.grama.R
import fr.x.grama.UserInfo
import org.mindrot.jbcrypt.BCrypt


class ProfFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_profil, container, false)
        current.findViewById<TextView>(R.id.prof_create_account).setOnClickListener {
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(R.id.setting_box, CreateAccountFragment())
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        current.findViewById<Button>(R.id.prof_connexion_button).setOnClickListener {
            val pseudoText = current.findViewById<TextView>(R.id.prof_pseudo).text.toString()
            val passwordText = current.findViewById<TextView>(R.id.prof_password).text.toString()
            if (pseudoText == "" || passwordText == "") {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val db = DatabaseManager(requireContext()).readableDatabase
            var cursor = db.rawQuery("SELECT PASSWORD FROM TABLE_USER WHERE USERNAME = ?", arrayOf(pseudoText))
            if (cursor.count == 0) {
                Toast.makeText(requireContext(), "Pseudo incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cursor.moveToFirst()
            val password = cursor.getString(0)
            cursor.close()
            if (!(BCrypt.checkpw(passwordText, password))) {
                Toast.makeText(requireContext(), "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cursor = db.rawQuery("SELECT * FROM TABLE_USER WHERE USERNAME = ?", arrayOf(pseudoText))
            println(cursor.count)
            if (cursor.count > 0) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                cursor.moveToFirst()
                UserInfo.email = cursor.getString(1)
                UserInfo.pseudo = cursor.getString(2)
                cursor.close()
                db.close()
                val ed = UserInfo.sp?.edit()
                ed?.putString("Unm", UserInfo.email)
                ed?.putString("Psw", UserInfo.pseudo)
                ed?.apply()
                val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
                containerLoad.replace(R.id.content_frame_profil, ConnectedFragment())
                containerLoad.addToBackStack(null)
                containerLoad.commit()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                cursor.close()
                db.close()
            }
        }
        return current
    }
}