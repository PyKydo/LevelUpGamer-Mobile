package cl.duoc.levelupgamer.data.remote.api

import cl.duoc.levelupgamer.data.remote.dto.auth.ChangePasswordRequest
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginRequest
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginResponse
import cl.duoc.levelupgamer.data.remote.dto.auth.TokenRefreshRequest
import cl.duoc.levelupgamer.data.remote.dto.auth.TokenRefreshResponse
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.data.remote.dto.contenido.BlogDto
import cl.duoc.levelupgamer.data.remote.dto.contenido.ContactoDto
import cl.duoc.levelupgamer.data.remote.dto.gamificacion.PuntosDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoCrearDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoRespuestaDto
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.data.remote.dto.users.RolesResponse
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRegistroDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LevelUpApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: TokenRefreshRequest): TokenRefreshResponse

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)

    @POST("api/users/register")
    suspend fun register(@Body body: UsuarioRegistroDto): UsuarioRespuestaDto

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Long): UsuarioRespuestaDto

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body body: UsuarioUpdateDto
    ): UsuarioRespuestaDto

    @GET("api/users/roles")
    suspend fun getRoles(): RolesResponse

    @GET("api/products")
    suspend fun getProducts(): List<ProductoDto>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductoDto

    @GET("api/carrito/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): CarritoDto

    @POST("api/carrito/{userId}/add")
    suspend fun addToCart(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): CarritoDto

    @DELETE("api/carrito/{userId}/remove")
    suspend fun removeFromCart(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long
    ): CarritoDto

    @DELETE("api/carrito/{userId}")
    suspend fun clearCart(@Path("userId") userId: Long)

    @POST("api/orders")
    suspend fun createOrder(@Body body: PedidoCrearDto): PedidoRespuestaDto

    @GET("api/orders/user/{userId}")
    suspend fun getOrdersForUser(@Path("userId") userId: Long): List<PedidoRespuestaDto>

    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") orderId: Long): PedidoRespuestaDto

    @GET("api/blog-posts")
    suspend fun getBlogPosts(): List<BlogDto>

    @GET("api/blog-posts/{id}")
    suspend fun getBlogPost(@Path("id") id: Long): BlogDto

    @POST("api/contact-messages")
    suspend fun sendContactMessage(@Body body: ContactoDto): ContactoDto

    @GET("api/points/{userId}")
    suspend fun getPoints(@Path("userId") userId: Long): PuntosDto

    @POST("api/points/earn")
    suspend fun earnPoints(@Body body: PuntosDto): PuntosDto

    @POST("api/points/redeem")
    suspend fun redeemPoints(@Body body: PuntosDto): PuntosDto
}
