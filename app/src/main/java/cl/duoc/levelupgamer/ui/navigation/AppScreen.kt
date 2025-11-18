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
    object Home : AppScreen("home")
    object Profile : AppScreen("profile")
    object Blogs : AppScreen("blogs")
    object BlogDetail : AppScreen("blog_detail/{blogId}") {
        fun createRoute(blogId: Long) = "blog_detail/$blogId"
    }
    object EditProfile : AppScreen("profile/edit")
    object ChangePassword : AppScreen("profile/change-password")
    object Payment : AppScreen("payment")
}
