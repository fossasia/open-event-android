package org.fossasia.openevent.general.order

import io.reactivex.Single
import org.fossasia.openevent.general.attendees.Attendee
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {

    @POST("orders?include=event,attendees&fields[event]=id&fields[attendees]=id")
    fun placeOrder(@Body order: Order): Single<Order>

    @PATCH("orders/{orderIdentifier}")
    fun confirmOrder(@Path("orderIdentifier") orderIdentifier: String, @Body order: ConfirmOrder): Single<Order>

    @POST("orders/{orderIdentifier}/charge")
    fun chargeOrder(@Path("orderIdentifier") orderIdentifier: String, @Body charge: Charge): Single<Charge>

    @GET("/v1/users/{userId}/orders?filter=[{\"name\":\"status\",\"op\":\"eq\",\"val\":\"completed\"}]&include=event,attendees&fields[attendees]=id")
        fun ordersUnderUser(@Path("userId") userId: Long): Single<List<Order>>

    @GET("/v1/orders/{orderIdentifier}/attendees")
    fun attendeesUnderOrder(@Path("orderIdentifier") orderIdentifier: String): Single<List<Attendee>>
}
