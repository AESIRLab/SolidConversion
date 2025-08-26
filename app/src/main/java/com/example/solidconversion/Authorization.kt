package com.example.solidconversion

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.solidconversion.screens.StartAuthScreen
import com.example.solidconversion.screens.AuthCompleteScreen
import com.example.solidconversion.screens.UnfetchableWebIdScreen
import com.example.solidconversion.screens.UpdateBlogPostsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.skCompiler.generatedModel.AuthTokenStore

// Contains all screens used for app
enum class BlogScreens {
    UnfetchableWebIdScreen,
    AuthCompleteScreen,
    StartAuthScreen,
    BlogList,
    UpdateBlogPostsScreen,
    AddEditBlogScreen,
    BlogCardScreen,
}

// Used for navbar
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object BlogList : BottomNavItem(
        route = BlogScreens.BlogList.name,
        title = "Blog List",
        icon = Icons.AutoMirrored.Filled.List
    )
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "NewApi")
@Composable
fun Authorization(){
    val navController = rememberNavController()
    val tokenStore = AuthTokenStore(LocalContext.current.applicationContext)
    val coroutineScope = rememberCoroutineScope()

    Scaffold {
        val context = LocalContext.current
        NavHost(
            navController = navController,
            startDestination = BlogScreens.StartAuthScreen.name,
        ) {
            composable(route = BlogScreens.UpdateBlogPostsScreen.name) {
                UpdateBlogPostsScreen()
            }

            // SCREEN: Authentication (Starting screen)
            composable(route = BlogScreens.StartAuthScreen.name) {
//                val accessToken = runBlocking { tokenStore.getAccessToken().first() }
//                if (accessToken.isNotEmpty()) {
//                    navController.navigate(route = BlogScreens.UpdateBlogPostsScreen.name)
//                }
                StartAuthScreen(
                    tokenStore = tokenStore,
                    onFailNavigation = {
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                navController.navigate(BlogScreens.UnfetchableWebIdScreen.name)
                            }
                        }
                    },
                    onInvalidInput = { msg ->
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // SCREEN: UnfetchableWebID
            composable(route = BlogScreens.UnfetchableWebIdScreen.name) {
                UnfetchableWebIdScreen(tokenStore = tokenStore) { err ->
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // SCREEN: Authentication complete
            composable(
                route = BlogScreens.AuthCompleteScreen.name,
                deepLinks = listOf(navDeepLink { uriPattern = "app://www.solid-oidc.com/solidconversion"})
            ) {
//                AuthCompleteScreen(tokenStore = tokenStore) {
                AuthCompleteScreen() {
                    navController.navigate(BlogScreens.UpdateBlogPostsScreen.name)
                }
            }
        }
    }
}