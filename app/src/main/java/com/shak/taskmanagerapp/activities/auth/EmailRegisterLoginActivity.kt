package com.shak.taskmanagerapp.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.ui.MainActivity
import com.shak.taskmanagerapp.databinding.ActivityEmailRegisterLoginBinding

class EmailRegisterLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailRegisterLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmailRegisterLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()


        binding.apply {
            emailLottieAnimView.playAnimation()
            emailLottieAnimView.setOnClickListener {
                emailLottieAnimView.playAnimation()
            }


            //Eye Animation
            setPasswordAnimationOn(passwordLottieAnimView, passwordEdt)
            setPasswordAnimationOn(confirmPasswordLottieAnimView, confirmPasswordEdt)


            if (intent.getStringExtra("comingFrom") == RegisterActivity::class.java.simpleName) {
                //Set the Visibilities
                regLoginTv.text = getString(R.string.register_with_email)
                forgotPasswordTv.visibility = View.GONE
                confirmPasswordLLC.visibility = View.VISIBLE
                regLoginBtn.text = getString(R.string.register)


                //Button click listener
                regLoginBtn.setOnClickListener {
                    val email = emailEdt.text.toString()
                    val password = passwordEdt.text.toString()
                    val confirmPassword = confirmPasswordEdt.text.toString()

                    if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@EmailRegisterLoginActivity,"Registration Successful",Toast.LENGTH_SHORT).show()

                                        val intentToMain = Intent(this@EmailRegisterLoginActivity, MainActivity::class.java)
                                            .apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            }
                                        startActivity(intentToMain)
                                        finish()
                                    } else {
                                        Toast.makeText(this@EmailRegisterLoginActivity,task.exception?.message.toString(),Toast.LENGTH_SHORT).show()
                                        Log.e("RegistrationCheck", "${task.exception?.message}")
                                    }
                                }
                        } else {
                            confirmPasswordEdt.error = "Password doesn't match"
                        }
                    } else {
                        emailEdt.error = "Email is required"
                        passwordEdt.error = "Password is required"
                        confirmPasswordEdt.error = "Confirm Password is required"
                    }
                }

            } else if (intent.getStringExtra("comingFrom") == LoginActivity::class.java.simpleName) {
                //Set the Visibilities
                regLoginTv.text = getString(R.string.login_with_email)
                forgotPasswordTv.visibility = View.VISIBLE
                confirmPasswordLLC.visibility = View.GONE
                regLoginBtn.text = getString(R.string.login)


                //Button click listeners
                regLoginBtn.setOnClickListener {
                    val email = emailEdt.text.toString()
                    val password = passwordEdt.text.toString()

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@EmailRegisterLoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()

                                    val intentToMain = Intent(this@EmailRegisterLoginActivity,MainActivity::class.java)
                                        .apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                    startActivity(intentToMain)
                                    finish()
                                } else {
                                    Toast.makeText(this@EmailRegisterLoginActivity, task.exception?.message.toString(),Toast.LENGTH_SHORT).show()
                                    Log.e("LoginCheck", task.exception.toString())
                                }
                            }
                    } else {
                        emailEdt.error = "Email is required"
                        passwordEdt.error = "Password is required"
                    }
                }


                forgotPasswordTv.setOnClickListener {
                    val forgotDialogView = layoutInflater.inflate(R.layout.forgot_password_dialog, null)
                    val emailInput = forgotDialogView.findViewById<AppCompatEditText>(R.id.emailInput)
                    val progressLottie = forgotDialogView.findViewById<LottieAnimationView>(R.id.progressLottie)

                    val dialog = AlertDialog.Builder(this@EmailRegisterLoginActivity)
                        .setTitle("Reset Password")
                        .setView(forgotDialogView)
                        .setPositiveButton("Send", null)  // We'll override this later
                        .setNegativeButton("Cancel", null)  //Eat 5 star, Do nothing
                        .create()

                    dialog.setOnShowListener {
                        val sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        sendButton.setOnClickListener {
                            val email = emailInput.text.toString().trim()

                            if (email.isNotEmpty()) {
                                // Show progress animation
                                progressLottie.visibility = View.VISIBLE
                                progressLottie.playAnimation()

                                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                                        progressLottie.cancelAnimation()
                                        progressLottie.visibility = View.GONE

                                        if (task.isSuccessful) {
                                            Toast.makeText(this@EmailRegisterLoginActivity,"Reset link sent to $email",Toast.LENGTH_LONG).show()
                                            dialog.dismiss()  // Only dismiss if successful
                                        } else {
                                            Toast.makeText(this@EmailRegisterLoginActivity,"Error: ${task.exception?.message}",Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this@EmailRegisterLoginActivity, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.show()
                }
            }
        }
    }

    private fun setPasswordAnimationOn(view: LottieAnimationView, edt: AppCompatEditText) {
        view.setMinAndMaxProgress(0.5f, 0.5f) //set the closed eye first
        view.playAnimation()  //show the closed eye


        var isPassShown = false
        view.setOnClickListener {
            val savedFont = edt.typeface  //save the current font/typeface

            if (isPassShown) {
                edt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD //show password
                view.setMinAndMaxProgress(0f, 0.5f)  //Closed eye animation
                isPassShown = false
            } else {
                edt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD //hide password
                view.setMinAndMaxProgress(0.5f, 1f)  //Open eye animation
                isPassShown = true
            }
            edt.typeface = savedFont
            edt.setSelection(edt.text.toString().length)
            view.playAnimation()
        }
    }
}