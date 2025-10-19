package cl.duoc.levelupgamer.navigation

sealed class AppScreen(val route: String) {
    object Splash : AppScreen("splash")
    object Login : AppScreen("login")
    object Catalog : AppScreen("catalog")
    object ProductDetail : AppScreen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : AppScreen("cart")
    object Profile : AppScreen("profile")
}
