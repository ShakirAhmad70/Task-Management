package com.shak.taskmanagerapp.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.ui.MainActivity
import com.shak.taskmanagerapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var selectedImageUri: Uri? = null
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        uri ->
        uri?.let {
            selectedImageUri = it
            binding.profileImg.setImageURI(it)
            // TODO: Upload profile image to Firebase Storage here
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore image URI if available
        savedInstanceState?.getString("PROFILE_IMAGE_URI")?.let {
            selectedImageUri = it.toUri()
            binding.profileImg.setImageURI(selectedImageUri)
        }

        binding.apply {
            profileImg.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }

            gotoTasksBtn.setOnClickListener {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainFrameLay, TasksFragment())
                transaction.commit()

                (requireActivity() as MainActivity).setNavItemActive(0)
            }

            setUpOptionsList()
            profileOptionsListView.onItemClickListener

        }


    }

    private fun setUpOptionsList() {
        val options = listOf("Edit Profile", "Change Password", "Delete Account", "Log Out")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)

        binding.profileOptionsListView.adapter = adapter

        binding.profileOptionsListView.setOnItemClickListener { parent, view, position, id ->
            when(position) {
                0 -> {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Edit Profile clicked",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                1 -> {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Change Password clicked",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                2 -> {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Delete Account clicked",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                3 -> {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Log Out clicked",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // ✅ Save URI string only if not null
        selectedImageUri?.let {
            outState.putString("PROFILE_IMAGE_URI", it.toString())
        }
    }
}