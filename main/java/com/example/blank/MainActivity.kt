package com.example.blank
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.test.isEnabled
import com.example.blank.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        navView.setNavigationItemSelectedListener { menuItem ->
            Log.d("current id", menuItem.itemId.toString())
            when (menuItem.itemId) {
                R.id.nav_clear_favorites -> {
                    Log.d("MainActivity", "Clearing favorites...")
                    clearFavorites()
                    showToast("Favorites cleared!")
                    return@setNavigationItemSelectedListener true
                }
                menuItem.itemId -> {
                    navController.navigate(menuItem.itemId)
                    true
                }

                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun getCurrentSelectedItemId(): Int? {

        val menu = binding.navView.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.isChecked) {
                return item.itemId
            }
        }
        return null
    }

    private fun addItemToTopOfList(itemId: Int) {
        val menu = binding.navView.menu
        val item = menu.findItem(itemId)

        var favoritesSection = findFavoritesSection(menu)

        if (favoritesSection == null) {
            favoritesSection = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Favorites")
            favoritesSection?.setGroupCheckable(0, true, true)

        }

        favoritesSection?.add(0, itemId, item.order, item.title)
            ?.setIcon(item.icon)

        showToast("${item.title} added to the top of the list!")
    }

    private fun findFavoritesSection(menu: Menu): SubMenu? {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.hasSubMenu()) {
                val subMenu = item.subMenu
                if (subMenu?.size()!! > 0 || subMenu?.getItem(0)?.title == "Favorites") {
                    return subMenu
                }
            }
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_button -> {

                val selectedItemId = getCurrentSelectedItemId()

                if (selectedItemId != null) {
                    addItemToTopOfList(selectedItemId)
                }
                showToast("Added to top of list!")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun clearFavorites() {
        val menu = binding.navView.menu
        val favoritesSection = findFavoritesSection(menu)

        favoritesSection?.clear()

        binding.navView.menu.clear()
        binding.navView.invalidate()

        showToast("Favorites cleared!")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}