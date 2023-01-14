package fr.x.grama.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import fr.x.grama.GameActivity
import fr.x.grama.GameModel
import fr.x.grama.R

class ButtonAdapter(private val listGame : List<GameModel>): RecyclerView.Adapter<ButtonAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName = view.findViewById<TextView>(R.id.text_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = listGame[position].name
        holder.itemName.setOnClickListener {
            val intent = Intent(it.context, GameActivity::class.java)
            startActivity(it.context, intent, null)
        }
    }

    override fun getItemCount(): Int = listGame.size
}