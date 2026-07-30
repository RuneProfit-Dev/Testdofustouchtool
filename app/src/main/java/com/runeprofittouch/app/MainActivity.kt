package com.runeprofittouch.app

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.runeprofittouch.app.data.GameDataImporter
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.database.DatabaseProvider
import com.runeprofittouch.app.navigation.AppNavigation
import com.runeprofittouch.app.ui.theme.RuneProfitTouchTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    companion object {
        private const val DATA_VERSION = 8
        private const val DATA_PREFERENCES = "game_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        enableImmersiveMode()
        ServerStore.initialize(this)

        val database = DatabaseProvider.getDatabase(this)

        lifecycleScope.launch {
            val preferences = getSharedPreferences(
                DATA_PREFERENCES,
                MODE_PRIVATE
            )
            val importedVersion = preferences.getInt("version", 0)
            if (
                database.itemDao().countItems() == 0 ||
                database.itemStatDao().count() == 0 ||
                importedVersion < DATA_VERSION
            ) {
                withContext(Dispatchers.IO) {
                    GameDataImporter(
                        context = this@MainActivity,
                        database = database
                    ).importFromAssets(
                        fileName = "runetouch.json"
                    )
                }
                preferences.edit()
                    .putInt("version", DATA_VERSION)
                    .apply()
            }
        }

        setContent {
            RuneProfitTouchTheme {
                AppNavigation()
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enableImmersiveMode()
    }

    private fun enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}
