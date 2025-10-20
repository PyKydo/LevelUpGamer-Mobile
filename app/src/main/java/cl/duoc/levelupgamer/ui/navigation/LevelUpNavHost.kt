package cl.duoc.levelupgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.InAuthRepository
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.ProductDetailScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.ui.ShoppingCartScreen
import cl.duoc.levelupgamer.viewmodel.CarritoViewModel
import cl.duoc.levelupgamer.viewmodel.CarritoViewModelFactory
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory

@Composable
fun LevelUpNavHost(
    productosVm: ProductoViewModel,
    authRepository: InAuthRepository,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val usuarioActual by authRepository.usuarioActual.collectAsState(initial = null)
    val carritoVm: CarritoViewModel? = usuarioActual?.let { usuario ->
        viewModel(
            key = "carrito_vm_${usuario.id}",
            factory = CarritoViewModelFactory(database, usuario.id)
        )
    }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route,
        modifier = modifier
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Login.route) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
            LoginScreen(
                vm = vm,
                onRegisterClick = { navController.navigate(AppScreen.Register.route) },
                onLoggedIn = {
                    navController.navigate(AppScreen.Catalog.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Register.route) {
            val vm: RegistrationViewModel = viewModel(factory = RegistrationViewModelFactory(authRepository))
            RegistrationScreen(
                vm = vm,
                onRegistered = { navController.popBackStack() },
                onGoToLogin = { navController.popBackStack() }
            )
        }
        composable(AppScreen.Catalog.route) {
            val productos by productosVm.productos.collectAsState()
            CatalogScreen(
                products = productos,
                onProductClick = { producto -> navController.navigate(AppScreen.ProductDetail.createRoute(producto.id)) },
                onViewCart = { navController.navigate(AppScreen.Cart.route) }
            )
        }
        composable(
            route = AppScreen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { entry ->
            val productos by productosVm.productos.collectAsState()
            val productId = entry.arguments?.getLong("productId")
            val product = productos.firstOrNull { it.id == productId }
            if (product != null) {
                ProductDetailScreen(
                    producto = product,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = {
                        if (carritoVm != null) {
                            carritoVm.agregar(productoId = it.id)
                            true
                        } else {
                            navController.navigate(AppScreen.Login.route) {
                                popUpTo(AppScreen.Login.route) { inclusive = true }
                            }
                            false
                        }
                    },
                    onGoToCart = { navController.navigate(AppScreen.Cart.route) }
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(AppScreen.Cart.route) {
            val vm = carritoVm
            if (vm == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            } else {
                val productos by productosVm.productos.collectAsState()
                val items by vm.items.collectAsState()
                ShoppingCartScreen(
                    items = items,
                    productos = productos,
                    onBack = { navController.popBackStack() },
                    onChangeQuantity = { itemId, newQuantity ->
                        if (newQuantity >= 1) {
                            vm.actualizarCantidad(itemId, newQuantity)
                        }
                    },
                    onRemoveItem = vm::eliminar,
                    onCheckout = {
                        vm.limpiar()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
