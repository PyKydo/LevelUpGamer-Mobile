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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.compose.NavHost
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import cl.duoc.levelupgamer.model.repository.PedidoRepository
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.HomeScreen
import cl.duoc.levelupgamer.ui.BlogsScreen
import cl.duoc.levelupgamer.ui.BlogDetailScreen
import cl.duoc.levelupgamer.ui.ChangePasswordScreen
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.ProductDetailScreen
import cl.duoc.levelupgamer.ui.ProfileScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.ui.ShoppingCartScreen
import cl.duoc.levelupgamer.ui.PaymentScreen
import cl.duoc.levelupgamer.viewmodel.CarritoViewModel
import cl.duoc.levelupgamer.viewmodel.CarritoViewModelFactory
import cl.duoc.levelupgamer.viewmodel.ChangePasswordViewModel
import cl.duoc.levelupgamer.viewmodel.ChangePasswordViewModelFactory
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.BlogViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory
import cl.duoc.levelupgamer.util.NetworkErrorMapper
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpNavHost(
    productosVm: ProductoViewModel,
    usuarioRepository: UsuarioRepository,
    carritoRepository: CarritoRepository,
    pedidoRepository: PedidoRepository,
    blogRepository: cl.duoc.levelupgamer.model.repository.BlogRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val usuarioActual by usuarioRepository.usuarioActual.collectAsState(initial = null)
    val isSessionActive by usuarioRepository.sesionActiva.collectAsState()
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {


            val show = currentRoute == AppScreen.Catalog.route || currentRoute == AppScreen.Home.route || currentRoute == AppScreen.Blogs.route
            if (show) {
                NavigationBar {
                    val items = listOf(
                        AppScreen.Catalog to Pair(Icons.Default.List, "Catálogo"),
                        AppScreen.Home to Pair(Icons.Default.Home, "Home"),
                        AppScreen.Blogs to Pair(Icons.Default.List, "Blogs")
                    )
                    items.forEach { (screen, iconAndLabel) ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(iconAndLabel.first, contentDescription = iconAndLabel.second) },
                            label = { Text(iconAndLabel.second) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Splash.route,
            modifier = modifier.padding(innerPadding)
        ) {
        composable(AppScreen.Splash.route) {


            SplashScreen(
                onTimeout = {
                    coroutineScope.launch {
                        try {
                            usuarioRepository.refreshPerfil()
                        } catch (_: Exception) {

                        }
                        val destination = if (usuarioRepository.usuarioActual.value != null) {
                            AppScreen.Catalog.route
                        } else {
                            AppScreen.Login.route
                        }
                        navController.navigate(destination) {
                            popUpTo(AppScreen.Splash.route) { inclusive = true }
                        }
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
            val context = LocalContext.current


            BackHandler(enabled = isSessionActive) {
                (context as? Activity)?.finish()
            }
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
        composable(AppScreen.Home.route) {
            val productos by productosVm.productos.collectAsState()
            val blogVm: BlogViewModel = viewModel(factory = BlogViewModel.Factory(blogRepository))
            val blogs by blogVm.blogs.collectAsState()
            HomeScreen(
                productosDestacados = productos.take(6),
                blogsDestacados = blogs.filter { it.featured },
                onProductClick = { producto -> navController.navigate(AppScreen.ProductDetail.createRoute(producto.id)) },
                onBlogClick = { blog -> navController.navigate(AppScreen.BlogDetail.createRoute(blog.id)) }
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
        composable(AppScreen.Blogs.route) {
            val blogVm: BlogViewModel = viewModel(factory = BlogViewModel.Factory(blogRepository))
            val blogs by blogVm.blogs.collectAsState()
            BlogsScreen(blogs = blogs, onBlogClick = { b -> navController.navigate(AppScreen.BlogDetail.createRoute(b.id)) })
        }
        composable(
            route = AppScreen.BlogDetail.route,
            arguments = listOf(navArgument("blogId") { type = NavType.LongType })
        ) { entry ->
            val blogVm: BlogViewModel = viewModel(factory = BlogViewModel.Factory(blogRepository))
            val blogs by blogVm.blogs.collectAsState()
            val loadingBlogs by blogVm.loading.collectAsState()
            val blogError by blogVm.error.collectAsState()
            val blogId = entry.arguments?.getLong("blogId")
            val blog = blogs.firstOrNull { it.id == blogId }
            when {
                blog != null -> {
                val detailVm: cl.duoc.levelupgamer.viewmodel.BlogDetailViewModel = viewModel(
                    factory = cl.duoc.levelupgamer.viewmodel.BlogDetailViewModel.Factory(
                        repo = blogRepository,
                        blogId = blog.id
                    )
                )
                BlogDetailScreen(blog = blog, detailVm = detailVm, onBack = { navController.popBackStack() })
                }
                loadingBlogs -> {
                    BlogDetailLoadingState(onBack = { navController.popBackStack() })
                }
                blogError != null || blogs.isEmpty() -> {
                    BlogDetailErrorState(
                        message = blogError ?: "No se encontró el blog solicitado",
                        onBack = { navController.popBackStack() }
                    )
                }
                else -> {
                    BlogDetailLoadingState(onBack = { navController.popBackStack() })
                }
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
                                    profileUpdateError = NetworkErrorMapper.map(t)
                                } catch (t: IllegalStateException) {
                                    profileUpdateError = NetworkErrorMapper.map(t)
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
                    },
                    onGoToPayment = { navController.navigate(AppScreen.Payment.route) }
                )
            }
        }
        composable(AppScreen.Payment.route) {
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
                PaymentScreen(
                    items = items,
                    productos = productos,
                    initialAddress = usuario?.direccion,
                    checkoutState = checkoutState,
                    onBack = { navController.popBackStack() },
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
}

@Composable
private fun BlogDetailLoadingState(onBack: () -> Unit) {
    BlogDetailScaffoldWrapper(onBack = onBack) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun BlogDetailErrorState(message: String, onBack: () -> Unit) {
    BlogDetailScaffoldWrapper(onBack = onBack) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlogDetailScaffoldWrapper(
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blog") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        content = content
    )
}

