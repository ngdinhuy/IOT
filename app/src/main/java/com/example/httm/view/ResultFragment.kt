package com.example.httm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.httm.databinding.FragmentResultBinding

class ResultFragment:Fragment() {
    lateinit var databinding:FragmentResultBinding
    private val args by navArgs<ResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentResultBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpEvent()
        databinding.msv.setText(args.msv)
        databinding.name.setText(args.username)
    }

    private fun setUpEvent() {
        databinding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
        databinding.btnClose.setOnClickListener{
            val action = HomeFragmentDirections.actionGlobalHomeFragment()
            findNavController().navigate(action)
        }
    }
}