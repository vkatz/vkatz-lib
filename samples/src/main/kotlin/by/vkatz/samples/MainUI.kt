package by.vkatz.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainUI : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        navController = findNavController(R.id.mainNavFragment)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
