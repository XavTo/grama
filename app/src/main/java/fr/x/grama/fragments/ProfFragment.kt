package fr.x.grama.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.x.grama.R


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
            // requete
        }
        return current
    }
}