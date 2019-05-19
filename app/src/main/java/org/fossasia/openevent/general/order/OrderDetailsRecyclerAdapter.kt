package org.fossasia.openevent.general.order

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.attendees.Attendee
import org.fossasia.openevent.general.event.Event
import kotlin.collections.ArrayList

class OrderDetailsRecyclerAdapter : RecyclerView.Adapter<OrderDetailsViewHolder>() {

    private val attendees = ArrayList<Attendee>()
    private var event: Event? = null
    private var orderIdentifier: String? = null
    private var eventDetailsListener: EventDetailsListener? = null
    private var onQrImageClicked: QrImageClickListener? = null

    fun addAll(attendeeList: List<Attendee>) {
        if (attendees.isNotEmpty())
            this.attendees.clear()
        this.attendees.addAll(attendeeList)
        notifyDataSetChanged()
    }

    fun setEvent(event: Event?) {
        this.event = event
        notifyDataSetChanged()
    }

    fun setSeeEventListener(listener: EventDetailsListener?) {
        eventDetailsListener = listener
    }

    fun setQrImageClickListener(listener: QrImageClickListener?) {
        onQrImageClicked = listener
    }

    fun setOrderIdentifier(orderId: String?) {
        orderIdentifier = orderId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_order_details, parent, false)
        return OrderDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val order = attendees[position]
        holder.bind(order, event, orderIdentifier, eventDetailsListener, onQrImageClicked)
    }

    override fun getItemCount(): Int {
        return attendees.size
    }

    interface EventDetailsListener {
        fun onClick(eventID: Long)
    }

    interface QrImageClickListener {
        fun onClick(qrImage: Bitmap)
    }
}
