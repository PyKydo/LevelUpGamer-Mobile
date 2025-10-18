package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.ProductDetailScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Datos de ejemplo
        val products = List(10) {
            Producto(
                id = it.toLong(),
                nombre = "Producto de Videojuego $it",
                descripcion = "Descripción detallada del producto de videojuego $it. Este texto es un placeholder para el contenido que vendrá después.",
                precio = (20..100).random().toDouble(),
                imageUrl = ""
            )
        }

        setContent {
            LevelUpGamerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "catalog") {
                    composable("catalog") {
                        CatalogScreen(products = products) {
                            navController.navigate("productDetail/${it.id}")
                        }
                    }
                    composable(
                        "productDetail/{productId}",
                        arguments = listOf(navArgument("productId") { }) 
                    ) {
                        val productId = it.arguments?.getString("productId")?.toLongOrNull()
                        val product = products.find { p -> p.id == productId }
                        if (product != null) {
                            ProductDetailScreen(producto = product) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}