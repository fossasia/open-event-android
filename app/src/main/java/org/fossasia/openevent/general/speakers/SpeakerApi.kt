package org.fossasia.openevent.general.speakers

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface SpeakerApi {

    @GET("events/{id}/speakers")
    fun getSpeakerForEvent(@Path("id") id: Long): Single<List<Speaker>>

    @GET("sessions/{sessionId}/speakers")
    fun getSpeakersForSession(@Path("sessionId") id: Long): Single<List<Speaker>>

    @GET("speakers/{speaker_id}")
    fun getSpeakerWithId(@Path("speaker_id") id: Long): Single<Speaker>
}
