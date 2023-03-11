package com.example.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.R

class InnerNavContainerFragment : Fragment() {

    private val args: InnerNavContainerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_inner_nav_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.inner_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.inner_nav_graph)
        if (args.listType == null) {
            navGraph.setStartDestination(R.id.filmInfoFragment)
        } else {
            navGraph.setStartDestination(R.id.listFragment)
        }
        navController.setGraph(navGraph, args.toBundle())
    }


}