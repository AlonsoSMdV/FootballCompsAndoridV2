package com.example.footballcompsuserv2.ui.adapters

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import coil3.load

import com.example.footballcompsuserv2.data.matches.Match
import com.example.footballcompsuserv2.data.matches.MatchFbWithTeams
import com.example.footballcompsuserv2.databinding.MatchesItemBinding
import com.example.footballcompsuserv2.di.NetworkUtils
import com.example.footballcompsuserv2.ui.fragments.MatchesFragmentDirections
import com.example.footballcompsuserv2.ui.viewModels.MatchesViewModel

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class MatchesAdapter (private val viewModel: MatchesViewModel,
                      private val networkUtils: NetworkUtils): ListAdapter<MatchFbWithTeams, MatchesAdapter.MatchViewHolder>(
    DiffCallback()
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = MatchesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding, viewModel,  networkUtils)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bind(match)
    }

    //ViewHolder de partidos
    class MatchViewHolder(private val binding: MatchesItemBinding, private val viewModel: MatchesViewModel,
                          private val networkUtils: NetworkUtils) : RecyclerView.ViewHolder(binding.root) {


        fun bind(match: MatchFbWithTeams) {
            //Formatear la fecha para que quede mas estética
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", Locale("es"))
            val parsedDate = LocalDate.parse(match.day ?: "", inputFormatter)
            binding.day.text = parsedDate.format(outputFormatter)

            // Format time
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(match.hour)
            binding.hour.text = time.format(timeFormatter)

            //Resultado
            binding.result.text = match.result

            //Equipo local - nombre
            binding.localTeamName.text = match.localTeamName
            //Equipo visitante - nombre
            binding.visitingTeamName.text = match.visitorTeamName
            //Equipo local - img
            binding.localTeamImg.load(match.localTeamImg)
            //Equipo visitante - img
            binding.visitingTeamImg.load(match.visitorTeamImg)
            binding.statusText.text = match.status ?: "Desconocido"

            val statusBar = binding.statusBar
            val status = match.status?.lowercase()

            when (status) {
                "por jugar" -> {
                    statusBar.setBackgroundColor(Color.parseColor("#FFA726")) // naranja
                }
                "jugando" -> {
                    statusBar.setBackgroundColor(Color.parseColor("#66BB6A")) // verde
                }
                "finalizado" -> {
                    statusBar.setBackgroundColor(Color.parseColor("#EF5350")) // rojo
                }
                else -> {
                    statusBar.setBackgroundColor(Color.GRAY)
                }
            }

            // Animación para la barra
            statusBar.animate()
                .alpha(1f)
                .scaleX(1f)
                .setDuration(600)
                .start()

            //Lugar de partido
            binding.place.setOnClickListener {
                if (networkUtils.isNetworkAvailable() && !match.place.isNullOrBlank()) {
                    val action = MatchesFragmentDirections.matchToPlace(match.place.toString(), 10f)
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(it.context, "Sin conexión. El mapa no se puede mostrar.", Toast.LENGTH_SHORT).show()
                }
            }

            //Botón de compartir
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

            binding.btnStatistics.setOnClickListener {
                val matchId = match.id
                val matchStatus = match.status

                if (matchId != null && matchStatus != null) {
                    val action = MatchesFragmentDirections.matchToStats(matchId, matchStatus)
                    it.findNavController().navigate(action)
                } else {
                    // Puedes mostrar un log o un Toast
                    Log.e("MatchesAdapter", "ID o estado del partido es null ${matchId}, $matchStatus")
                    Toast.makeText(it.context, "No se pueden cargar estadísticas de este partido", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnLineup.setOnClickListener {
                val matchId = match.id

                if (matchId != null) {
                    val action = MatchesFragmentDirections.matchToLineups(matchId)
                    it.findNavController().navigate(action)
                } else {
                    // Puedes mostrar un log o un Toast
                    Log.e("MatchesAdapter", "ID o estado del partido es null ${matchId}")
                    Toast.makeText(it.context, "No se pueden cargar estadísticas de este partido", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MatchFbWithTeams>() {
        override fun areItemsTheSame(oldItem: MatchFbWithTeams, newItem: MatchFbWithTeams): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MatchFbWithTeams, newItem: MatchFbWithTeams): Boolean {
            return oldItem == newItem
        }
    }
}