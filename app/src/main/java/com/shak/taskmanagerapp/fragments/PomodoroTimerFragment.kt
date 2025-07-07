package com.shak.taskmanagerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.databinding.FragmentPomodoroTimerBinding

class PomodoroTimerFragment : Fragment() {
    private lateinit var binding: FragmentPomodoroTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPomodoroTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}