package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.x.grama.NetworkClass
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
            if (ip == "" || port == "" || port.toIntOrNull() == null || ip.split(".").size != 4) {
                Toast.makeText(requireContext(), "Entrez une adresse ip et un port valide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val networkServ = NetworkClass()
            Thread{ networkServ.connect(ip, port.toInt()) }.start()
        }
        current.findViewById<Button>(R.id.duel_host_button).setOnClickListener {
            val port: Int = current.findViewById<android.widget.EditText>(R.id.port).text.toString().toInt()
            val networkServ = NetworkClass()
            val ret = networkServ.checkPort(port)
            if (ret == 1) {
                Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ret == 2) {
                Toast.makeText(requireContext(), "Le port est déjà utilisé", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Thread{ networkServ.start(port) }.start()
            Thread{ networkServ.connect("localhost", port) }.start()
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(this.id, DuelWaitFragment())
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        return current
    }

}