package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.x.grama.R

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
        current.findViewById<Button>(R.id.prof_connexion_button).setOnClickListener {
            val emailText = current.findViewById<TextView>(R.id.prof_email_create).text.toString()
            val pseudoText = current.findViewById<TextView>(R.id.prof_pseudo_create).text.toString()
            val passwordText = current.findViewById<TextView>(R.id.prof_password_create).text.toString()
            // requete
        }
        return current
    }
}