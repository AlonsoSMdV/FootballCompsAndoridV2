package com.example.footballcompsuserv2.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentFavouritesBinding
import com.example.footballcompsuserv2.ui.viewModels.FavouritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment: Fragment(R.layout.fragment_favourites) {
    private lateinit var binding: FragmentFavouritesBinding
    private val viewModel: FavouritesViewModel by viewModels()
}