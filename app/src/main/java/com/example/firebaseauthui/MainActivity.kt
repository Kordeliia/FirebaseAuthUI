package com.example.firebaseauthui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebaseauthui.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var authStateListener : FirebaseAuth.AuthStateListener
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val response = IdpResponse.fromResultIntent(it.data)
        if(it.resultCode == RESULT_OK){
            val user = FirebaseAuth.getInstance().currentUser
            if(user != null){
                Toast.makeText(this,
                    getString(R.string.mssg_bienvenida),
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            if(response == null){
                Toast.makeText(this,
                    getString(R.string.mssg_despedida),
                    Toast.LENGTH_SHORT).show()
                finish()
            } else {
                response.error?.let{
                    if(it.errorCode == ErrorCodes.NO_NETWORK){
                        Toast.makeText(this,
                            getString(R.string.mssg_error_no_network),
                            Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this,
                            getString(R.string.mssg_error_codigo_error),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configAuth()
    }
    private fun configAuth(){
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if(auth.currentUser != null){
                supportActionBar?.title = auth.currentUser?.displayName
                binding.tvInit.visibility = View.VISIBLE
                binding.llProgress.visibility = View.GONE
            } else{
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build())
                resultLauncher.launch(AuthUI
                    .getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sign_out -> {
                AuthUI.getInstance().signOut(this)
                    .addOnSuccessListener {
                        Toast.makeText(this,
                            getString(R.string.mssg_sign_out_success),
                            Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            binding.tvInit.visibility = View.GONE
                            binding.llProgress.visibility = View.VISIBLE
                        } else{
                            Toast.makeText(this,
                                getString(R.string.mssg_sign_out_failure),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}