package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.ui.viewModels.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private val viewModel: StatsViewModel by viewModels()
    private val args: StatsFragmentArgs by navArgs()//Obtener argumentos pasados desde el partido

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val statsContainer = view.findViewById<LinearLayout>(R.id.stats_container)

        lifecycleScope.launchWhenStarted {
            viewModel.stats.collect { statsFb ->
                statsContainer.removeAllViews()
                statsFb.stats?.forEach { stat ->
                    val statView = layoutInflater.inflate(R.layout.match_stats_item, statsContainer, false)
                    statView.findViewById<TextView>(R.id.stat_name).text = stat.name
                    statView.findViewById<TextView>(R.id.stat_local).text = stat.localValue?.toString() ?: "-"
                    statView.findViewById<TextView>(R.id.stat_visitor).text = stat.visitorValue?.toString() ?: "-"
                    statsContainer.addView(statView)
                }
            }
        }

        viewModel.loadStats(args.matchId, args.matchStatus)
    }
}
