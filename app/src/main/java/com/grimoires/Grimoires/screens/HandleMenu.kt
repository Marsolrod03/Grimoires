import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HandleMenu(
    nickname: String,
    navController: NavHostController,
    content: @Composable (CoroutineScope, DrawerState) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawerContent(
                nickname = nickname,
                onOptionSelected = { option ->
                    val route = when (option) {
                        "MY CHARACTERS" -> "characters"
                        "MY CAMPAIGNS" -> "campaigns"
                        "THE LIBRARY" -> "library"
                        "DICE CALCULATOR" -> "calculator"
                        "profile" -> "userProfileSection"
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it)
                        scope.launch { drawerState.close() }
                    }
                }
            )
        }
    ) {
        content(scope, drawerState)
    }
}
