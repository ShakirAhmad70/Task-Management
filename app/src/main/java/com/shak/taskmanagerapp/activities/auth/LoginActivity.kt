package com.shak.taskmanagerapp.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.shak.taskmanagerapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val registerActivity = RegisterActivity()
    private lateinit var fbCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        fbCallbackManager = CallbackManager.Factory.create()


        binding.apply {
            loginWithEmailBtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, EmailRegisterLoginActivity::class.java)
                intent.putExtra("comingFrom", this@LoginActivity::class.java.simpleName)
                startActivity(intent)
            }

            loginWithPhoneNoBtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, PhoneRegisterLoginActivity::class.java)
                intent.putExtra("comingFrom", this@LoginActivity::class.java.simpleName)
                startActivity(intent)
            }

            loginWithGoogleBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    registerActivity.registerAndLoginWithGoogle(this@LoginActivity)
                }
            }

            loginWithFacebookBtn.setOnClickListener {
                //TODO: iska and main activity me back button ka kuchh karna padega
                setUiEnabled(false)  //disable all buttons to prevent multiple clicks during login
                registerActivity.registerAndLoginWithFacebook(this@LoginActivity, fbCallbackManager)
                setUiEnabled(true)
            }

            goToRegisterTxt.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUiEnabled(enabled: Boolean) {
        binding.loginWithEmailBtn.isEnabled = enabled
        binding.loginWithPhoneNoBtn.isEnabled = enabled
        binding.loginWithGoogleBtn.isEnabled = enabled
        binding.loginWithFacebookBtn.isEnabled = enabled
        binding.goToRegisterTxt.isEnabled = enabled
        binding.loginProgressBar.visibility = if (enabled) View.GONE else View.VISIBLE
    }

}