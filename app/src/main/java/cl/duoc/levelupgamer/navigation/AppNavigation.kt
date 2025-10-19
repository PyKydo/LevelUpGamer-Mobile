package cl.duoc.levelupgamer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.ui.splash.SplashScreen
import cl.duoc.levelupgamer.ui.login.LoginScreen
import cl.duoc.levelupgamer.ui.catalog.CatalogScreen
import cl.duoc.levelupgamer.ui.product.ProductDetailScreen
import cl.duoc.levelupgamer.ui.cart.CartScreen
import cl.duoc.levelupgamer.ui.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreen.Splash.route) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController)
        }
        composable(AppScreen.Login.route) {
            LoginScreen(navController)
        }
        composable(AppScreen.Catalog.route) {
            CatalogScreen(navController)
        }
        composable(AppScreen.ProductDetail.route) {
            val productId = it.arguments?.getString("productId")
            requireNotNull(productId) { "Product ID not found" }
            ProductDetailScreen(navController, productId)
        }
        composable(AppScreen.Cart.route) {
            CartScreen(navController)
        }
        composable(AppScreen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
