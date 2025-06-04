package com.shak.taskmanagerapp.activities.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.ui.MainActivity
import com.shak.taskmanagerapp.databinding.ActivityPhoneRegisterLoginBinding
import java.util.concurrent.TimeUnit


class PhoneRegisterLoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPhoneRegisterLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationId: String? = null
    private var isVerificationStage = false
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneRegisterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.phoneLottieAnimView.playAnimation()
        binding.phoneLottieAnimView.setOnClickListener {
            binding.phoneLottieAnimView.playAnimation()
        }

        // Set up OTP input fields to move forward and backward automatically
        watchAndSetupOtpEdt()

        binding.sendAndVerifyOtpBtn.setOnClickListener {
            if (!isVerificationStage) {
                val phone = binding.phoneEdt.text.toString().trim()
                if (phone.length == 10) {
                    val fullPhoneNo = "+91$phone"
                    Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show()
                    startPhoneVerification(fullPhoneNo)
                } else {
                    Toast.makeText(this, "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                }
            } else {
                val otp = getEnteredOtp()
                if (otp.length == 6 && verificationId != null) {
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.resendOtpBtn.setOnClickListener {
            val phone = binding.phoneEdt.text.toString().trim()
            if (phone.length == 10 && resendToken != null) {
                val fullPhone = "+91$phone"
                resendVerificationCode(fullPhone, resendToken!!)
            } else {
                Toast.makeText(this, "Cannot resend OTP yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPhoneVerification(phone: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        showLoading(true)
    }

    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show()
        startResendTimer()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            showLoading(false)
            Toast.makeText(this@PhoneRegisterLoginActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            showLoading(false)
            this@PhoneRegisterLoginActivity.verificationId = verificationId
            this@PhoneRegisterLoginActivity.resendToken = token
            isVerificationStage = true
            binding.OtpLLC.visibility = View.VISIBLE
            binding.sendAndVerifyOtpBtn.text = getString(R.string.verify_otp)
            Toast.makeText(this@PhoneRegisterLoginActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            startResendTimer()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showLoading(true)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            showLoading(false)
            if (task.isSuccessful) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                startActivity(intent)
                finish()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getEnteredOtp(): String {
        var otp = ""
        binding.apply {
            otp = otpEdt1.text.toString().trim() +
                    otpEdt2.text.toString().trim() +
                    otpEdt3.text.toString().trim() +
                    otpEdt4.text.toString().trim() +
                    otpEdt5.text.toString().trim() +
                    otpEdt6.text.toString().trim()
        }
        return otp
    }

    private fun startResendTimer() {
        binding.resendOtpBtn.visibility = View.GONE
        binding.resendOtpBtn.isEnabled = false

        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOtpBtn.text = "Resend OTP in ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.resendOtpBtn.text = getString(R.string.resend_otp)
                binding.resendOtpBtn.visibility = View.VISIBLE
                binding.resendOtpBtn.isEnabled = true
            }
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.phoneLottieAnimView.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) binding.phoneLottieAnimView.playAnimation() else binding.phoneLottieAnimView.cancelAnimation()
    }

    private fun watchAndSetupOtpEdt() {
        val otpEdt = listOf(
            binding.otpEdt1,
            binding.otpEdt2,
            binding.otpEdt3,
            binding.otpEdt4,
            binding.otpEdt5,
            binding.otpEdt6
        )

        for (i in otpEdt.indices) {
            otpEdt[i].apply {
                // Move to next on input
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s?.length == 1 && i < otpEdt.size - 1) {
                            otpEdt[i + 1].requestFocus()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                // Move to previous on delete
                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                        if (text?.isEmpty() == true && i > 0) {
                            otpEdt[i - 1].requestFocus()
                        }
                    }
                    false
                }
            }
        }
    }

}