package org.fossasia.openevent.general.attendees

import io.reactivex.Completable
import io.reactivex.Single
import org.fossasia.openevent.general.attendees.forms.CustomForm
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AttendeeApi {

    @POST("attendees?include=event,ticket&fields[event]=id&fields[ticket]=id")
    fun postAttendee(@Body attendee: Attendee): Single<Attendee>

    @DELETE("attendees/{attendeeId}")
    fun deleteAttendee(@Path("attendeeId") id: Long): Completable

    @GET("events/{id}/custom-forms?include=event&fields[event]=id")
    fun getCustomFormsForAttendees(
        @Path("id") id: Long,
        @Query("filter") filter: String
    ): Single<List<CustomForm>>

    @GET("/v1/orders/{order_identifier}/attendees")
    fun getAttendeesUnderOrder(@Path("order_identifier") identifier:String)
        :Single<List<Attendee>>
}
