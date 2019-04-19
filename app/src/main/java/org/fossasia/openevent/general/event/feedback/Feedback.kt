package org.fossasia.openevent.general.event.feedback

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.github.jasminb.jsonapi.LongIdHandler
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import org.fossasia.openevent.general.auth.UserId
import org.fossasia.openevent.general.event.EventId

@Type("feedback")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class Feedback(
    @Id(LongIdHandler::class)
    val id: Long,
    val rating: String?,
    val comment: String?,
    val deletedAt: String?,
    @Relationship("event")
    var event: EventId? = null,
    @Relationship("user")
    var user: UserId? = null
    )
