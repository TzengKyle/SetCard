package tw.edu.ncku.iim.rsliu.setcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tw.edu.ncku.iim.rsliu.setcard.Adapter.CardGridRecyclerViewAdapter
import java.util.Objects

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isTablet = resources.configuration.screenWidthDp >= 600
        if(!isTablet) {
            val switchButton = findViewById<Button>(R.id.switch1)
            switchButton.setOnClickListener {
                val navController = findNavController(R.id.nav_host_fragment_container)
                val currentDestination = navController.currentDestination?.id

                if (currentDestination == R.id.cardGridFragment) {
                    navController.navigate(R.id.action_cardGridFragment_to_resultGridFragment)
                } else if (currentDestination == R.id.historyGridFragment) {
                    navController.popBackStack()
                }
            }
        }

    }
}