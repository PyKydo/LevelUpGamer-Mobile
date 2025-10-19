package cl.duoc.levelupgamer.ui.product

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    Text(text = "Product Detail Screen: $productId")
}
