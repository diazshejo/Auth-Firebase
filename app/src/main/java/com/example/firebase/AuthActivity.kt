package com.example.firebase

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.concurrent.TimeUnit


private lateinit var firebaseAnalytics: FirebaseAnalytics
// [START declare_auth]
private lateinit var auth: FirebaseAuth

 class AuthActivity : AppCompatActivity() {
    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

       FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]


        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        //integracion firebase
        bundle.putString("message","Integracion firebase completa")
        analytics.logEvent("InitScreen",bundle)
        setup()

    }

    private fun setup() {
        title = "Autenticacion"
        buttonsignUp.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextEmail.text.toString(),
                    editTextPass.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                        }else{
                            showAlert()

                        }
                }
            }
        }
        //boton acceder
        buttonacceder.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextPass.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(editTextEmail.text.toString(),
                    editTextPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                    }else{
                        showAlert()

                    }
                }
            }
        }

        btnPhoneN.setOnClickListener {

            // Turn off phone auth app verification.
           /* FirebaseAuth.getInstance().firebaseAuthSettings
                .setAppVerificationDisabledForTesting();*/

            val numP = EditPhonum.text.toString()

            // [START auth_test_phone_verify]
            val phoneNum = "+502 41468363"
            val testVerificationCode = "123456"

            // Whenever verification is triggered with the whitelisted number,
            // provided it is not set for auto-retrieval, onCodeSent will be triggered.
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onCodeSent(
                        verificationId: String,
                        forceResendingToken: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Save the verification id somewhere
                        // ...

                        // The corresponding whitelisted code above should be used to complete sign-in.
                        //this@AuthActivity.
                        showAlert()
                    }

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        // Sign in with the credential
                        // ...
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        // ...
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            // [END auth_test_phone_verify]


        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al user")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private  fun showHome(email: String, Provider: ProviderType){
        val homeIntent =Intent(this, HomeActivity::class.java).apply {
            putExtra( "email",email)
            putExtra("provider",Provider)
        }
        startActivity(homeIntent)
    }
   /* private fun testPhoneVerify() {
        // [START auth_test_phone_verify]
        val phoneNum = "38288056"
        val testVerificationCode = "123456"

        // Whenever verification is triggered with the whitelisted number,
        // provided it is not set for auto-retrieval, onCodeSent will be triggered.
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save the verification id somewhere
                    // ...

                    // The corresponding whitelisted code above should be used to complete sign-in.
                    this@AuthActivity.
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // Sign in with the credential
                    // ...
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // ...
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END auth_test_phone_verify]
    }*/
}

private fun FirebaseAuthSettings.setAppVerificationDisabledForTesting() {
    TODO("Not yet implemented")
}
