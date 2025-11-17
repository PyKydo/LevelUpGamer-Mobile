package cl.duoc.levelupgamer.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import cl.duoc.levelupgamer.model.repository.PedidoRepository
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.ChangePasswordScreen
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.ProductDetailScreen
import cl.duoc.levelupgamer.ui.ProfileScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.ui.ShoppingCartScreen
import cl.duoc.levelupgamer.viewmodel.CarritoViewModel
import cl.duoc.levelupgamer.viewmodel.CarritoViewModelFactory
import cl.duoc.levelupgamer.viewmodel.ChangePasswordViewModel
import cl.duoc.levelupgamer.viewmodel.ChangePasswordViewModelFactory
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun LevelUpNavHost(
    productosVm: ProductoViewModel,
    usuarioRepository: UsuarioRepository,
    carritoRepository: CarritoRepository,
    pedidoRepository: PedidoRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val usuarioActual by usuarioRepository.usuarioActual.collectAsState(initial = null)
    val carritoVm: CarritoViewModel? = usuarioActual?.let { usuario ->
        viewModel(
            key = "carrito_vm_${usuario.id}",
            factory = CarritoViewModelFactory(
                repo = carritoRepository,
                pedidoRepository = pedidoRepository,
                usuarioRepository = usuarioRepository,
                usuarioId = usuario.id
            )
        )
    }
    var profileImageUri by remember(usuarioActual?.id) {
        mutableStateOf(usuarioActual?.fotoPerfilUrl?.let { Uri.parse(it) })
    }
    var profileUpdateError by remember(usuarioActual?.id) { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route,
        modifier = modifier
    ) {
        composable(AppScreen.Splash.route) {
            val isLoggedIn = usuarioActual != null
            SplashScreen(
                onTimeout = {
                    val destination = if (isLoggedIn) AppScreen.Catalog.route else AppScreen.Login.route
                    navController.navigate(destination) {
                        popUpTo(AppScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Login.route) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(usuarioRepository))
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
            val vm: RegistrationViewModel = viewModel(factory = RegistrationViewModelFactory(usuarioRepository))
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
                onViewCart = { navController.navigate(AppScreen.Cart.route) },
                onOpenProfile = {
                    profileUpdateError = null
                    navController.navigate(AppScreen.Profile.route)
                }
            )
        }
        composable(AppScreen.Profile.route) {
            val usuario = usuarioActual
            if (usuario == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Catalog.route) { inclusive = true }
                    }
                }
            } else {
                ProfileScreen(
                    user = usuario,
                    imageUri = profileImageUri,
                    onImageUriChange = { profileImageUri = it },
                    onEditClick = {
                        profileUpdateError = null
                        navController.navigate(AppScreen.EditProfile.route)
                    },
                    onLogoutClick = {
                        coroutineScope.launch {
                            usuarioRepository.cerrarSesion()
                            profileImageUri = null
                            profileUpdateError = null
                            navController.navigate(AppScreen.Login.route) {
                                popUpTo(AppScreen.Catalog.route) { inclusive = true }
                            }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        composable(AppScreen.EditProfile.route) {
            val usuario = usuarioActual
            if (usuario == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            } else {
                EditProfileScreen(
                    user = usuario,
                    onBackClick = { navController.popBackStack() },
                    onChangePasswordClick = { navController.navigate(AppScreen.ChangePassword.route) },
                    onSaveChanges = { newName, newEmail ->
                        profileUpdateError = null
                        coroutineScope.launch {
                            try {
                                usuarioRepository.actualizarPerfil(newName, newEmail)
                                navController.popBackStack()
                            } catch (t: IllegalArgumentException) {
                                profileUpdateError = t.message
                            } catch (t: IllegalStateException) {
                                profileUpdateError = t.message
                            }
                        }
                    },
                    errorMessage = profileUpdateError
                )
            }
        }
        composable(AppScreen.ChangePassword.route) {
            if (usuarioActual == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            } else {
                val vm: ChangePasswordViewModel = viewModel(factory = ChangePasswordViewModelFactory(usuarioRepository))
                ChangePasswordScreen(
                    vm = vm,
                    onBackClick = { navController.popBackStack() }
                )
            }
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
                val usuario = usuarioActual
                val productos by productosVm.productos.collectAsState()
                val items by vm.items.collectAsState()
                val checkoutState by vm.checkoutState.collectAsState()
                ShoppingCartScreen(
                    items = items,
                    productos = productos,
                    initialAddress = usuario?.direccion,
                    checkoutState = checkoutState,
                    onBack = { navController.popBackStack() },
                    onChangeQuantity = { itemId, newQuantity ->
                        if (newQuantity >= 1) {
                            vm.actualizarCantidad(itemId, newQuantity)
                        }
                    },
                    onRemoveItem = vm::eliminar,
                    onCheckout = vm::realizarCheckout,
                    onCheckoutStateConsumed = vm::consumirResultadoCheckout,
                    onCheckoutSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
