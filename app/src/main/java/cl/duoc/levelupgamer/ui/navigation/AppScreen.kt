package cl.duoc.levelupgamer.ui.navigation

sealed class AppScreen(val route: String) {
    object Splash : AppScreen("splash")
    object Login : AppScreen("login")
    object Register : AppScreen("register")
    object Catalog : AppScreen("catalog")
    object ProductDetail : AppScreen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    object Cart : AppScreen("cart")
    object Profile : AppScreen("profile")
    object EditProfile : AppScreen("profile/edit")
    object ChangePassword : AppScreen("profile/change-password")
}
