package org.fossasia.openevent.general.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.attendees.Attendee
import org.fossasia.openevent.general.attendees.AttendeeService
import org.fossasia.openevent.general.common.SingleLiveEvent
import org.fossasia.openevent.general.data.Resource
import org.fossasia.openevent.general.event.Event
import org.fossasia.openevent.general.event.EventService
import timber.log.Timber

class OrderDetailsViewModel(
    private val eventService: EventService,
    private val orderService: OrderService,
    private val attendeeService: AttendeeService,
    private val resource: Resource
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val message = SingleLiveEvent<String>()
    private val mutableEvent = MutableLiveData<Event>()
    val event: LiveData<Event> = mutableEvent
    private val mutableAttendees = MutableLiveData<List<Attendee>>()
    val attendees: LiveData<List<Attendee>> = mutableAttendees
    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    fun loadEvent(id: Long) {
        if (id == -1L) {
            throw IllegalStateException("ID should never be -1")
        }

        compositeDisposable.add(eventService.getEvent(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mutableEvent.value = it
            }, {
                Timber.e(it, "Error fetching event %d", id)
                message.value = resource.getString(R.string.error_fetching_event_message)
            })
        )
    }

    fun loadAttendeeDetailsFromIdentifier(identifier:String) {
        compositeDisposable.add(orderService.getOrderFromIdentifier(identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val ids = it.attendees.map { attendeeId ->  attendeeId.id }
                loadAttendeeDetails(ids)
            },{

            })
        )

    }

    private fun loadAttendeeDetails(ids:List<Long>){
        compositeDisposable.add(attendeeService.getAttendeesWithIds(ids)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                mutableProgress.value = true
            }.doFinally {
                mutableProgress.value = false
            }.subscribe({
                mutableAttendees.value = it
            }, {
                Timber.e(it, "Error fetching attendee details")
                message.value = resource.getString(R.string.error_fetching_attendee_details_message)
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
