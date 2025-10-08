package ut.cs.ee.phonedev25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ut.cs.ee.phonedev25.data.local.GameDatabase
import ut.cs.ee.phonedev25.data.repository.GameRepository
import ut.cs.ee.phonedev25.ui.theme.Phonedev25Theme

class MainActivity : ComponentActivity() {

    val database: GameDatabase by lazy {
        GameDatabase.getDatabase(this)
    }

    val repository = GameRepository(
        database.gameItemDao(),
        database.matchHistoryDao(),
        database.matchActionDao()
    )

    //Cleaned up this part.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
}