package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.ui.viewModels.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private val viewModel: StatsViewModel by viewModels()
    private val args: StatsFragmentArgs by navArgs()//Obtener argumentos pasados desde el partido

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
                        val statView =
                            layoutInflater.inflate(R.layout.match_stats_item, statsContainer, false)
                        statView.findViewById<TextView>(R.id.stat_name).text = stat.name
                        statView.findViewById<TextView>(R.id.stat_local).text =
                            stat.localValue?.toString() ?: "-"
                        statView.findViewById<TextView>(R.id.stat_visitor).text =
                            stat.visitorValue?.toString() ?: "-"
                        statsContainer.addView(statView)
                    }
                }
            }
        }

        viewModel.loadStats(args.matchId, args.matchStatus)
    }
}
