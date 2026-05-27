package com.example.fitprime

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitprime.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Manejar la pantalla de splash antes de super.onCreate
        installSplashScreen()
        
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // SOLUCIÓN PARA LA PÍLDORA GRIS:
        // Desactivamos el indicador y ponemos su color como transparente por código.
        // Esto evita errores de compilación en el XML.
        navView.isItemActiveIndicatorEnabled = false
        navView.itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }
}
