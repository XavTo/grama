package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import fr.x.grama.NetworkClass
import fr.x.grama.R

class DuelWaitFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_duel_wait_connexion, container, false)
        current.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            NetworkClass().server.close()
            val containerLoad = requireActivity().supportFragmentManager.beginTransaction()
            containerLoad.replace(this.id, DuelFragment())
            containerLoad.addToBackStack(null)
            containerLoad.commit()
            return@setOnClickListener
        }
        return current
    }
}