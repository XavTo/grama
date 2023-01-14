package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.x.grama.R
import fr.x.grama.UserInfo

class DuelFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!UserInfo.isLogged()) {
            return inflater.inflate(R.layout.fragment_duel, container, false)
        }
        val current = inflater.inflate(R.layout.fragment_duel_connected, container, false)
        current.findViewById<Button>(R.id.duel_connexion_button).setOnClickListener {
            val ip = current.findViewById<android.widget.EditText>(R.id.adresse_ip).text.toString()
            val port = current.findViewById<android.widget.EditText>(R.id.port).text.toString()
            if (ip == "" || port == "") {
                Toast.makeText(requireContext(), "Entrez une adresse ip et un port", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(R.id.duel_layout, NetworkFragment(ip.toLong(), port.toLong()))
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        current.findViewById<Button>(R.id.duel_host_button).setOnClickListener {
            val port = current.findViewById<android.widget.EditText>(R.id.port).text.toString()
            if (port == "") {
                Toast.makeText(requireContext(), "Entrez un port", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            return@setOnClickListener
        }
        return current
    }

}