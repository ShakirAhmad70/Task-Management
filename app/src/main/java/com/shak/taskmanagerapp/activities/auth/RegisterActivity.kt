    package com.shak.taskmanagerapp.activities.auth
    
    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.text.Spannable
    import android.text.SpannableString
    import android.text.style.ForegroundColorSpan
    import android.util.Log
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.credentials.CredentialManager
    import androidx.credentials.CustomCredential
    import androidx.credentials.GetCredentialRequest
    import androidx.lifecycle.lifecycleScope
    import com.facebook.AccessToken
    import com.facebook.CallbackManager
    import com.facebook.FacebookCallback
    import com.facebook.FacebookException
    import com.facebook.login.LoginManager
    import com.facebook.login.LoginResult
    import com.google.android.libraries.identity.googleid.GetGoogleIdOption
    import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
    import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
    import com.google.firebase.auth.FacebookAuthProvider
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.FirebaseAuthUserCollisionException
    import com.google.firebase.auth.GoogleAuthProvider
    import com.shak.taskmanagerapp.BuildConfig
    import com.shak.taskmanagerapp.R
    import com.shak.taskmanagerapp.activities.ui.MainActivity
    import com.shak.taskmanagerapp.databinding.ActivityRegisterBinding
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    class RegisterActivity : AppCompatActivity() {
        private lateinit var binding: ActivityRegisterBinding
        private lateinit var fbCallbackManager: CallbackManager
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }


            binding.apply {
                val fullText = getString(R.string.register_for_extra_benefits)
                val spannableString = SpannableString(fullText)
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.orange)),
                    fullText.indexOf("Extra"),
                    fullText.indexOf("Extra") + "Extra".length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                registerTxt.text = spannableString
    
                registerWithEmailBtn.setOnClickListener {
                    val intent = Intent(this@RegisterActivity, EmailRegisterLoginActivity::class.java)
                    intent.putExtra("comingFrom", this@RegisterActivity::class.java.simpleName)
                    startActivity(intent)
                }
    
                registerWithPhoneNoBtn.setOnClickListener {
                    val intent = Intent(this@RegisterActivity, PhoneRegisterLoginActivity::class.java)
                    intent.putExtra("comingFrom", this@RegisterActivity::class.java.simpleName)
                    startActivity(intent)
                }
    
                registerWithGoogleBtn.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        registerAndLoginWithGoogle(this@RegisterActivity)
                    }
                }
    
                registerWithFacebookBtn.setOnClickListener {
                    registerAndLoginWithFacebook(this@RegisterActivity)
                }
    
                goToLoginTxt.setOnClickListener {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
    
            }
        }
    
        suspend fun registerAndLoginWithGoogle(context: Context) {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .setNonce("")
                .build()
    
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
    
            try {
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
    
                val credential = result.credential
                if (credential is CustomCredential) {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
    
                            val firebaseCredential = GoogleAuthProvider.getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )
    
    
                            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Login successful",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent = Intent(context, MainActivity::class.java)
                                            .apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            }
                                        context.startActivity(intent)
                                        if(context is AppCompatActivity){
                                            context.finish()
                                        }
    
                                    } else {
                                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                                        Log.d("LoginStatus", task.exception.toString())
                                    }
    
                                }
    
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.d("GoogleCredentialCheck", "authenticationWithGoogle: ${e.message.toString()}")
                        }
                    }
                }
    
            } catch (e: Exception) {
                Log.d("CredentialCheck", "authenticationWithGoogle: ${e.message.toString()}")
            }
        }


        fun registerAndLoginWithFacebook(
            context: Context,
            callbackManager: CallbackManager? = null
        ) {
            fbCallbackManager = callbackManager?: CallbackManager.Factory.create()

            LoginManager.getInstance().logInWithReadPermissions(
                context as AppCompatActivity,
                listOf("email", "public_profile")
            )

            LoginManager.getInstance().registerCallback(
                fbCallbackManager,
                object: FacebookCallback<LoginResult>{
                            override fun onSuccess(result: LoginResult) {
                                handleFacebookAccessToken(result.accessToken, context)
                            }

                            override fun onCancel() {
                                Toast.makeText(context, "Login Cancelled", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(exception: FacebookException) {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                Log.d("FbCredentialCheck", "authenticationWithFacebook: ${exception.message.toString()}")
                            }
                }
            )
    
    
        }

        private fun handleFacebookAccessToken(accessToken: AccessToken, context: Context) {
            Log.d("FbToken", "handleFacebookAccessToken:$accessToken")

            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context as AppCompatActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                        context.finish()
                    } else {
                        val exception = it.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            //This happens when the same email is already used by another provider (like Email, Google, Twitter, etc.)
                            val email = exception.email
                            val updatedMessage = "This email is already registered with a different method.\nPlease sign in using Email or your original method."
                            Toast.makeText(context, updatedMessage, Toast.LENGTH_LONG).show()
                            Log.d("FbCredentialCheck", "registerWithFacebook: Email collision with different provider for $email")
                        } else {
                            Toast.makeText(context as AppCompatActivity, "Login failed", Toast.LENGTH_SHORT).show()
                            Log.d("FbCredentialCheck", "registerWithFacebook: ${exception.toString()}")
                        }
                    }
                }
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            // Pass the activity result back to the Facebook SDK
            fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }