package com.example.footballcompsuserv2.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentCompetitionListBinding
import com.example.footballcompsuserv2.databinding.FragmentStatsBinding
import com.example.footballcompsuserv2.ui.viewModels.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private lateinit var binding: FragmentStatsBinding
    private val viewModel: StatsViewModel by viewModels()
    private val args: StatsFragmentArgs by navArgs()//Obtener argumentos pasados desde el partido

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.stats_toolbar)
        toolbar.setOnClickListener {
            val action = StatsFragmentDirections.statsToMatches()
            it.findNavController().navigate(action)
        }
        val statsContainer = view.findViewById<LinearLayout>(R.id.stats_container)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stats.collect { statsFb ->
                    statsContainer.removeAllViews()
                    statsFb.stats?.forEach { stat ->
                        val statView = layoutInflater.inflate(R.layout.match_stats_item, statsContainer, false)

                        val statNameText = statView.findViewById<TextView>(R.id.stat_name)
                        val localText = statView.findViewById<TextView>(R.id.stat_local)
                        val visitorText = statView.findViewById<TextView>(R.id.stat_visitor)

                        statNameText.text = stat.name

                        val localValue = stat.localValue?.toString() ?: "-"
                        val visitorValue = stat.visitorValue?.toString() ?: "-"

                        localText.text = localValue
                        visitorText.text = visitorValue

                        // Comparación para resaltar el mayor
                        val localFloat = stat.localValue?.toString()?.removeSuffix("%")?.toFloatOrNull()
                        val visitorFloat = stat.visitorValue?.toString()?.removeSuffix("%")?.toFloatOrNull()

                        if (localFloat != null && visitorFloat != null) {
                            when {
                                localFloat > visitorFloat -> highlightText(localText)
                                visitorFloat > localFloat -> highlightText(visitorText)
                                // Si son iguales, no resaltar
                            }
                        }

                        statsContainer.addView(statView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.match.collect{ matchFb ->
                    if (matchFb != null) {
                        binding.localTeamName.text = matchFb.localTeamName
                        binding.localTeamLogo.load(matchFb.localTeamImg)
                        binding.visitorTeamName.text = matchFb.visitorTeamName
                        binding.visitorTeamLogo.load(matchFb.visitorTeamImg)
                    }
                }
            }
        }



        viewModel.loadStats(args.matchId, args.matchStatus)
        viewModel.getMatchById(args.matchId)
    }

    private fun highlightText(textView: TextView) {
        textView.setBackgroundResource(R.drawable.dorsal_circle_background)
        textView.setTextColor(Color.WHITE)
        textView.setPadding(24, 12, 24, 12) // Espaciado interno para mantener buena forma
    }
}
