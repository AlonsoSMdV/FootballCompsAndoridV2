    package com.example.footballcompsuserv2.ui.fragments

    import android.Manifest
    import android.content.Context
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.Toast

    import androidx.activity.result.PickVisualMediaRequest
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import androidx.navigation.fragment.findNavController

    import coil3.load

    import com.example.footballcompsuserv2.R
    import com.example.footballcompsuserv2.data.remote.teams.LeagueData
    import com.example.footballcompsuserv2.data.remote.teams.LeagueId
    import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
    import com.example.footballcompsuserv2.data.remote.teams.TeamCreateRawAttributes
    import com.example.footballcompsuserv2.data.remote.teams.TeamRawAttributes
    import com.example.footballcompsuserv2.data.teams.Team
    import com.example.footballcompsuserv2.data.teams.TeamFbFields
    import com.example.footballcompsuserv2.databinding.FragmentCreateTeamBinding
    import com.example.footballcompsuserv2.ui.viewModels.CreateTeamViewModel

    import dagger.hilt.android.AndroidEntryPoint

    import kotlinx.coroutines.launch

    //Permisos
    private var PERMISSIONS_REQUIRED =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO)

    @AndroidEntryPoint
    class CreateTeamFragment :Fragment(R.layout.fragment_create_team){
        private var _logoUri: Uri? = null
        private lateinit var binding: FragmentCreateTeamBinding
        private val viewModel: CreateTeamViewModel by viewModels()
        private var idComp: String? = null

        //IMAGES
        private val contract = ActivityResultContracts.RequestMultiplePermissions()

        private val launcher = registerForActivityResult(contract)
        {
                permissions ->
            var granted = true
            permissions.entries.forEach {
                    permission ->
                if (permission.key in PERMISSIONS_REQUIRED && !permission.value){
                    granted = false
                }
            }
            if (granted){
                //Navigate to camera
                navigateToCamera()

            }else{
                Toast.makeText(requireContext(), "No tiene permisos de camara", Toast.LENGTH_LONG).show()
            }
        }

        //Elegir imagen de la galeria
        private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
            if (uri != null){
                viewLifecycleOwner.lifecycleScope.launch {
                    loadLogo(uri)
                }
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentCreateTeamBinding.inflate(
                inflater,
                container,
                false
            )
            return binding.root
        }


        /**
         * Función que navega al fragmento de Preview de camara
         */
        private fun navigateToCamera() {
            val action = CreateTeamFragmentDirections.createTeamToCamera()
            findNavController().navigate(action)
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


            val teamId = arguments?.getString("idTeam")
            var pts = 0;
            var nMatches = 0;

            if (teamId != null) {
                // Si es edición, precargar los datos
                binding.createTeamToolbar.title = requireContext().getString(R.string.team_update)
                binding.textCreateTeam.text = requireContext().getString(R.string.update_a_team)
                binding.createTeam.text = requireContext().getString(R.string.update)
                viewLifecycleOwner.lifecycleScope.launch {
                    val team = viewModel.getTeamById(teamId)
                    team?.let {
                        binding.editTextTeamName.setText(it.name)
                        binding.editTextTeamNPlayers.setText(it.numberOfPlayers)
                        pts = it.pts!!
                        nMatches = it.nMatches!!
                        it.picture?.let { imgUrl ->
                            binding.teamImageView.load(imgUrl)
                        }
                    }
                }
            }


            idComp = arguments?.getString("idCompSelected")!!

            //Toolbar
            binding.createTeamToolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            //Botón de seleccionar fotos de la galeria
            binding.teamSelectPhotoBtn.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
            }


            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.photo.collect{
                            photoUri ->
                        when(photoUri){
                            Uri.EMPTY -> {
                                //No hay foto, podemos poner un placeholder
                            }
                            else -> {
                                //tenemos la foto, la ponemos en la UI aprovechando coil
                                binding.teamImageView.load(photoUri)
                            }
                        }
                    }
                }
            }

            //Botón para confirmar la creación del equipo
            val btnCreate = view.findViewById<Button>(R.id.create_team)
            btnCreate.setOnClickListener{
                val name = binding.editTextTeamName.text.toString()
                val nPlayers = binding.editTextTeamNPlayers.text.toString()

                if (name.isBlank() || nPlayers.isBlank()) {
                    Toast.makeText(requireContext(),"Rellene todos los campos", Toast.LENGTH_SHORT).show()
                }else{
                    val createTeam = TeamFbFields(
                        name = name,
                        numberOfPlayers = nPlayers,
                        pts = 0,
                        nMatches = 0
                    )

                    val updateTeam = TeamFbFields(
                        name = name,
                        numberOfPlayers = nPlayers,
                        pts = pts,
                        nMatches = nMatches
                    )
                    if (teamId != null) {
                        viewModel.updateTeam(teamId, updateTeam, _logoUri)
                    } else {
                        viewModel.createTeam(createTeam, _logoUri, idComp!!)
                    }
                    val action = CreateTeamFragmentDirections.createToTeams(idComp!!)
                    findNavController().navigate(action)
                }
            }
        }

        //FUNCIÓN para cargar la imagen
        private fun loadLogo(uri:Uri?) {
            binding.teamImageView.load(uri)
            _logoUri = uri
        }

        //FUNCIÓN que comprueba que haya permisos
        private fun hasCameraPermissions(context: Context):Boolean {
            return PERMISSIONS_REQUIRED.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }