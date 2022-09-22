package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.x.grama.GameModel
import fr.x.grama.R
import fr.x.grama.adapter.ButtonAdapter

class HomeGameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_game_home, container, false)
        val listGame = arrayListOf<GameModel>()
        listGame.add(GameModel("Ortographe"))
        listGame.add(GameModel("Grammaire"))
        listGame.add(GameModel("Conjugaison"))
        val listView = view.findViewById<RecyclerView>(R.id.list_view)
        listView.adapter = ButtonAdapter(listGame)
        return view
    }
}