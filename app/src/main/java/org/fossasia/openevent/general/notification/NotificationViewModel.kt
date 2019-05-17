package org.fossasia.openevent.general.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.auth.AuthHolder
import org.fossasia.openevent.general.common.SingleLiveEvent
import org.fossasia.openevent.general.data.Network
import org.fossasia.openevent.general.data.Resource
import org.fossasia.openevent.general.utils.extensions.withDefaultSchedulers
import timber.log.Timber

class NotificationViewModel(
    private val notificationService: NotificationService,
    private val authHolder: AuthHolder,
    private val network: Network,
    private val resource: Resource
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val mutableNotifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = mutableNotifications

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    private val mutableNoInternet = SingleLiveEvent<Boolean>()
    val noInternet: LiveData<Boolean> = mutableNoInternet

    private val mutableError = SingleLiveEvent<String>()
    val error: LiveData<String> = mutableError

    fun getId() = authHolder.getId()

    fun isLoggedIn() = authHolder.isLoggedIn()

    fun getNotifications() {

        if (!isConnected()) {
            return
        }

        compositeDisposable += notificationService.getNotifications(getId())
            .withDefaultSchedulers()
            .doOnSubscribe {
                mutableProgress.value = true
            }.doFinally {
                mutableProgress.value = false
            }.subscribe({
                mutableNotifications.value = it
                Timber.d("Notification retrieve successful")
            }, {
                mutableError.value = resource.getString(R.string.msg_failed_to_load_notification)
                Timber.d(it, resource.getString(R.string.msg_failed_to_load_notification))
            })
    }

    fun isConnected(): Boolean {
        val isConnected = network.isNetworkConnected()
        mutableNoInternet.value = !isConnected
        return isConnected
    }
}
