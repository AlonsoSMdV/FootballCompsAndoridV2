    package com.example.footballcompsuserv2.ui.adapters

    import android.view.LayoutInflater
    import android.view.ViewGroup

    import androidx.appcompat.app.AlertDialog
    import androidx.navigation.findNavController
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.ListAdapter
    import androidx.recyclerview.widget.RecyclerView

    import coil3.load

    import com.example.footballcompsuserv2.R
    import com.example.footballcompsuserv2.ui.fragments.TeamFragmentDirections
    import com.example.footballcompsuserv2.data.teams.Team
    import com.example.footballcompsuserv2.data.teams.TeamFb
    import com.example.footballcompsuserv2.data.user.UserFb
    import com.example.footballcompsuserv2.databinding.TeamItemBinding
    import com.example.footballcompsuserv2.ui.fragments.CompsFragmentDirections
    import com.example.footballcompsuserv2.ui.viewModels.TeamViewModel

    class TeamListAdapter(private val viewModel: TeamViewModel, private val idComp: String, private var user: UserFb? ): ListAdapter<TeamFb, TeamListAdapter.TeamViewHolder>(
        DiffCallback()
    ) {
        fun updateUser(user: UserFb?) {
            this.user = user
            notifyDataSetChanged()  // Redibuja para que se actualicen los botones de favoritos
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TeamViewHolder {
            val binding = TeamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TeamViewHolder(binding, viewModel, idComp)
        }

        override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
            val team = getItem(position)
            holder.bind(team, user)
        }

        //ViewHolder de equipo
        class TeamViewHolder(private val binding: TeamItemBinding, private val viewModel: TeamViewModel, private val compId: String): RecyclerView.ViewHolder(binding.root){
            fun bind(team: TeamFb, user: UserFb?){
                //Nombre
                binding.teamName.text = team.name

                binding.teamPoints.text = "Pts: ${team.pts}"
                binding.teamMatches.text = "Partidos: ${team.nMatches}"

                //Img
                if (team.picture != null){
                    binding.teamImage.load(team.picture)
                }

                //Botón de borrar
                binding.deleteTeamButton.setOnClickListener {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Eliminar equipo")
                        .setMessage("¿Estás seguro de que quieres eliminar el equipo \"${team.name}\"?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            viewModel.deleteTeam(team.id!!, compId)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                binding.updateTeamButton.setOnClickListener {
                    val action = TeamFragmentDirections.teamsToUpdate(team.id.toString(), team.league!!.id)
                    it.findNavController().navigate(action)
                }

                // ¿Es favorito?
                val isFavourite = user?.teamFav?.id == team.id
                binding.favouriteButtonTeams.setImageResource(
                    if (isFavourite) R.drawable.ic_star_filled else R.drawable.ic_star
                )

                binding.favouriteButtonTeams.setOnClickListener {
                    viewModel.setFavouriteTeam(team)
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