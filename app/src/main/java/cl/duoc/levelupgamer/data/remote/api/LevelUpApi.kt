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
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LevelUpApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body body: TokenRefreshRequest): TokenRefreshResponse

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)

    @POST("api/v1/users/register")
    suspend fun register(@Body body: UsuarioRegistroDto): UsuarioRespuestaDto

    @GET("api/v1/users/{id}")
    suspend fun getUser(@Path("id") id: Long): UsuarioRespuestaDto

    @PUT("api/v1/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body body: UsuarioUpdateDto
    ): UsuarioRespuestaDto

    @GET("api/v1/users/roles")
    suspend fun getRoles(): RolesResponse

    @GET("api/v1/products")
    suspend fun getProducts(): List<ProductoDto>

    @GET("api/v1/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductoDto

    @GET("api/v1/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): CarritoDto

    @POST("api/v1/cart/{userId}/add")
    suspend fun addToCart(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): CarritoDto

    @DELETE("api/v1/cart/{userId}/remove")
    suspend fun removeFromCart(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long
    ): CarritoDto

    @DELETE("api/v1/cart/{userId}")
    suspend fun clearCart(@Path("userId") userId: Long)

    @POST("api/v1/boletas")
    suspend fun createOrder(@Body body: PedidoCrearDto): PedidoRespuestaDto

    @GET("api/v1/boletas/user/{userId}")
    suspend fun getOrdersForUser(@Path("userId") userId: Long): List<PedidoRespuestaDto>

    @GET("api/v1/boletas/{id}")
    suspend fun getOrder(@Path("id") orderId: Long): PedidoRespuestaDto

    @GET("api/v1/blog-posts")
    suspend fun getBlogPosts(): List<BlogDto>

    @GET("api/v1/blog-posts/{id}")
    suspend fun getBlogPost(@Path("id") id: Long): BlogDto

    @GET("api/v1/blog-posts/{id}/content")
    suspend fun getBlogContent(@Path("id") id: Long): ResponseBody

    @POST("api/v1/contact-messages")
    suspend fun sendContactMessage(@Body body: ContactoDto): ContactoDto

    @GET("api/v1/points/{userId}")
    suspend fun getPoints(@Path("userId") userId: Long): PuntosDto

    @POST("api/v1/points/earn")
    suspend fun earnPoints(@Body body: PuntosDto): PuntosDto

    @POST("api/v1/points/redeem")
    suspend fun redeemPoints(@Body body: PuntosDto): PuntosDto
}
