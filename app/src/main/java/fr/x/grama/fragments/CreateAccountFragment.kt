package fr.x.grama.fragments

import android.content.ContentValues
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


class CreateAccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_profil_create, container, false)
        current.findViewById<TextView>(R.id.prof_connexion_account_create).setOnClickListener {
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(R.id.setting_box, ProfFragment())
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        current.findViewById<Button>(R.id.prof_create_account_create).setOnClickListener {
            val emailText = current.findViewById<TextView>(R.id.prof_email_create).text.toString()
            val pseudoText = current.findViewById<TextView>(R.id.prof_pseudo_create).text.toString()
            val passwordText = current.findViewById<TextView>(R.id.prof_password_create).text.toString()
            val db = DatabaseManager(requireContext()).writableDatabase
            val contentValues = ContentValues()
            contentValues.put("EMAIL", emailText)
            contentValues.put("USERNAME", pseudoText)
            contentValues.put("PASSWORD", BCrypt.hashpw(passwordText, BCrypt.gensalt()))
            val result = db.insert("TABLE_USER", null, contentValues)

            if (result == (-1).toLong())
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            UserInfo.email = emailText
            UserInfo.pseudo = pseudoText
            val ed = UserInfo.sp?.edit()
            ed?.putString("Unm", UserInfo.email)
            ed?.putString("Psw", UserInfo.pseudo)
            ed?.apply()
            if (result != (-1).toLong()) {
                val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
                containerLoad.replace(R.id.setting_box, ConnectedFragment())
                containerLoad.addToBackStack(null)
                containerLoad.commit()
            }
        }
        return current
    }
}