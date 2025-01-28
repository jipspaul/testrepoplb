package com.plb.conference

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.plb.conference.ui.screen.LoginScreen
import com.plb.conference.ui.theme.ConferenceTheme
import com.plb.conference.viewmodels.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConferenceTheme {
                val loginViewModel : LoginViewModel = viewModel(
                    factory = LoginViewModelFactory()
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            val intent = Intent(this, HomePageActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
        Log.d("MainActivity", "onCreate")
    }

    override fun onStart() {
        super.onStart()

        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()

        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()

        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()

        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("MainActivity", "onDestroy")
    }
}

//===================== TODO approfondire =====================

class LoginViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}