package fr.x.grama.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.x.grama.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            CoroutineScope(Dispatchers.IO).launch {
                NetworkClass.connect(ip, port.toInt(), requireContext())
            }
            Thread.sleep(500)
            if (NetworkClass.isClientConnnected) {
                val intent = Intent(requireContext(), NetworkActivity::class.java)
                intent.putExtra("isHost", false)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show()
            }
            return@setOnClickListener
        }
        current.findViewById<Button>(R.id.duel_host_button).setOnClickListener {
            val stringPort = current.findViewById<android.widget.EditText>(R.id.port).text.toString()
            val port: Int = if (stringPort == "" || stringPort.toIntOrNull() == null) {
                8080
            } else {
                stringPort.toInt()
            }
            val ret = ServerClass.checkPort(port)
            if (ret == 1) {
                Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ret == 2) {
                Toast.makeText(requireContext(), "Le port est déjà utilisé", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                ServerClass.start(port)
            }
            CoroutineScope(Dispatchers.IO).launch {
                NetworkClass.connect("localhost", port, requireContext())
            }
            val intent = Intent(requireContext(), NetworkActivity::class.java)
            startActivity(intent)
            return@setOnClickListener
        }
        return current
    }

}
