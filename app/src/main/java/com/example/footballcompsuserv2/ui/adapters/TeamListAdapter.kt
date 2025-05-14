    package com.example.footballcompsuserv2.ui.adapters

    import android.view.LayoutInflater
    import android.view.ViewGroup

    import androidx.navigation.findNavController
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.ListAdapter
    import androidx.recyclerview.widget.RecyclerView

    import coil3.load

    import com.example.footballcompsuserv2.R
    import com.example.footballcompsuserv2.ui.fragments.TeamFragmentDirections
    import com.example.footballcompsuserv2.data.teams.Team
    import com.example.footballcompsuserv2.data.teams.TeamFb
    import com.example.footballcompsuserv2.databinding.TeamItemBinding
    import com.example.footballcompsuserv2.ui.fragments.CompsFragmentDirections
    import com.example.footballcompsuserv2.ui.viewModels.TeamViewModel

    class TeamListAdapter(private val viewModel: TeamViewModel, private val idComp: String): ListAdapter<TeamFb, TeamListAdapter.TeamViewHolder>(
        DiffCallback()
    ) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TeamViewHolder {
            val binding = TeamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TeamViewHolder(binding, viewModel, idComp)
        }

        override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
            val team = getItem(position)
            holder.bind(team)
        }

        //ViewHolder de equipo
        class TeamViewHolder(private val binding: TeamItemBinding, private val viewModel: TeamViewModel, private val compId: String): RecyclerView.ViewHolder(binding.root){
            fun bind(team: TeamFb){
                //Nombre
                binding.teamName.text = team.name

                //Img
                if (team.picture != null){
                    binding.teamImage.load(team.picture)
                }

                //Botón de borrar
                binding.deleteTeamButton.setOnClickListener {
                    viewModel.deleteTeam(team.id!!, compId)
                }

                binding.updateTeamButton.setOnClickListener {
                    val action = TeamFragmentDirections.teamsToUpdate(team.id.toString(), team.league!!.id)
                    it.findNavController().navigate(action)
                }

                //Botón de favoritos
                binding.favouriteButtonTeams.apply {
                    setImageResource(
                        R.drawable.ic_fav2
                    )
                    setOnClickListener {
                        //viewModel.toggleFavouriteTeams(team, compId)
                    }
                }

                //Hacer que al clickar en la card navegue a los jugadores del equipo
                binding.teamCard.setOnClickListener {
                    val action = TeamFragmentDirections.teamsToPlayers(team.id.toString(), team.league!!.id)
                    it.findNavController().navigate(action)
                }
            }
        }

        class DiffCallback: DiffUtil.ItemCallback<TeamFb>(){
            override fun areItemsTheSame(oldItem: TeamFb, newItem: TeamFb): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TeamFb, newItem: TeamFb): Boolean {
                return oldItem == newItem
            }

        }

    }