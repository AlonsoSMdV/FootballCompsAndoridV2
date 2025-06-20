    package com.example.footballcompsuserv2.ui.adapters

    import android.util.Log
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import android.widget.Toast

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
    import com.google.android.material.dialog.MaterialAlertDialogBuilder
    import com.google.android.material.snackbar.Snackbar

    class TeamListAdapter(private val viewModel: TeamViewModel, private val idComp: String, private var user: UserFb?, private var userId: String? ): ListAdapter<TeamFb, TeamListAdapter.TeamViewHolder>(
        DiffCallback()
    ) {
        fun updateUser(user: UserFb?) {
            this.user = user
            notifyDataSetChanged()  // Redibuja para que se actualicen los botones de favoritos
        }

        fun updateUserId(userId: String?) {
            this.userId = userId
            notifyDataSetChanged()
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
            holder.bind(team, user, userId)
        }

        //ViewHolder de equipo
        class TeamViewHolder(private val binding: TeamItemBinding, private val viewModel: TeamViewModel, private val compId: String): RecyclerView.ViewHolder(binding.root){
            fun bind(team: TeamFb, user: UserFb?, userId: String?){
                //Nombre
                binding.teamName.text = team.name

                binding.teamPoints.text = "Pts: ${team.pts}"
                binding.teamMatches.text = "Partidos: ${team.nMatches}"

                //Img
                if (team.picture != null){
                    binding.teamImage.load(team.picture)
                }

                val context = binding.root.context
                val deleteTitle = context.getString(R.string.team_delete)
                val deleteMessage = context.getString(R.string.team_delete_message, team.name)
                val cancel = context.getString(R.string.cancel)
                val yes = context.getString(R.string.yes)
                val notDelete = context.getString(R.string.team_delete_not)
                val notUpdate = context.getString(R.string.team_update_not)

                // ✅ Usar userId del ViewModel para comprobar propiedad
                val isOwner = team.userId?.id == userId

                binding.deleteTeamButton.alpha = if (isOwner) 1.0f else 0.5f
                binding.updateTeamButton.alpha = if (isOwner) 1.0f else 0.5f

                binding.deleteTeamButton.setOnClickListener {
                    if (isOwner) {
                        MaterialAlertDialogBuilder(binding.root.context)
                            .setTitle(deleteTitle)
                            .setMessage(deleteMessage)
                            .setPositiveButton(yes) { dialog, _ ->
                                viewModel.deleteTeam(team.id!!, compId)
                                dialog.dismiss()
                                Snackbar.make(binding.root, R.string.team_deleted, Snackbar.LENGTH_SHORT)
                                    .setAnchorView(R.id.bottom_navigation)
                                    .setAction("X"){}
                                    .show()
                            }
                            .setNegativeButton(cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                    } else {
                        Toast.makeText(
                            binding.root.context,
                            notDelete,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                binding.updateTeamButton.setOnClickListener {
                    if (isOwner) {
                        val action = TeamFragmentDirections.teamsToUpdate(team.id!!, compId)
                        it.findNavController().navigate(action)
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            notUpdate,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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