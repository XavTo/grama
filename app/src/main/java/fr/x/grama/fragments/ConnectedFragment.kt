package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import fr.x.grama.R
import fr.x.grama.UserInfo

class ConnectedFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_profil_connected, container, false)
        current.findViewById<Button>(R.id.prof_disconnect_button).setOnClickListener {
            UserInfo.email = ""
            UserInfo.pseudo = ""
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(R.id.setting_box, ProfFragment())
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        return current
    }
}