package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.x.grama.R
import fr.x.grama.UserInfo

class StatFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_stat, container, false)
        current.findViewById<android.widget.Button>(R.id.reset_button).setOnClickListener {
            UserInfo.wins = 0
            UserInfo.losses = 0
            val ed = UserInfo.sp?.edit()
            ed?.putInt("Wins", UserInfo.wins)
            ed?.putInt("Losses", UserInfo.losses)
            ed?.apply()
            current.findViewById<android.widget.TextView>(R.id.wins).text = getString(R.string.total_wins, UserInfo.wins)
            current.findViewById<android.widget.TextView>(R.id.losses).text = getString(R.string.total_losses, UserInfo.losses)
            current.findViewById<android.widget.TextView>(R.id.win_percent).text = getString(R.string.win_percentage, (UserInfo.wins.toFloat() / (UserInfo.wins + UserInfo.losses) * 100))
            return@setOnClickListener
        }
        current.findViewById<android.widget.TextView>(R.id.wins).text = getString(R.string.total_losses, UserInfo.losses)
        current.findViewById<android.widget.TextView>(R.id.losses).text = getString(R.string.total_losses, UserInfo.losses)
        current.findViewById<android.widget.TextView>(R.id.win_percent).text = getString(R.string.win_percentage, (UserInfo.wins.toFloat() / (UserInfo.wins + UserInfo.losses) * 100))
        return current
    }
}