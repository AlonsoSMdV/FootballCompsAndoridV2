package com.example.footballcompsuserv2.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.footballcompsuserv2.data.matches.Match
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.databinding.MatchesItemBinding
import com.example.footballcompsuserv2.ui.fragments.MatchesFragmentDirections
import com.example.footballcompsuserv2.ui.viewModels.MatchesViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MatchesAdapter (private val viewModel: MatchesViewModel): ListAdapter<Match, MatchesAdapter.MatchViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = MatchesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bind(match)
    }

    class MatchViewHolder(private val binding: MatchesItemBinding, private val viewModel: MatchesViewModel) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", Locale("es"))
            val date = LocalDate.parse(match.day)
            binding.day.text = date.format(dateFormatter)

            // Format time
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(match.hour)
            binding.hour.text = time.format(timeFormatter)

            binding.result.text = match.result

            binding.localTeamName.text = match.localTeamName
            binding.visitingTeamName.text = match.visitorTeamName
            binding.localTeamImg.load(match.localTeamImg)
            binding.visitingTeamImg.load(match.visitorTeamImg)

            binding.place.setOnClickListener {
                val action = MatchesFragmentDirections.matchToPlace(match.place.toString())
                it.findNavController().navigate(action)
            }

            binding.share.setOnClickListener {
                val shareText = """
                    📢 Partido: ${match.localTeamName} 🆚 ${match.visitorTeamName}
                    📅 Fecha: ${match.day}
                    ⏰ Hora: ${match.hour}
                    📍 Lugar: ${match.place}
                    🔢 Resultado: ${match.result}
                    
                    ¡No te lo pierdas! ⚽🔥
                """.trimIndent()

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(intent, "Compartir partido")
                it.context.startActivity(shareIntent)
            }



        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}