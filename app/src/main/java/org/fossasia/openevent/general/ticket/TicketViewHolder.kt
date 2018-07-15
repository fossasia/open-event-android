package org.fossasia.openevent.general.ticket

import android.R
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_ticket.view.*

class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(ticket: Ticket, selectedListener: TicketSelectedListener?, eventCurrency: String?) {
        itemView.ticketName.text = ticket.name

        if (ticket.minOrder > 0 && ticket.maxOrder > 0) {
            val spinnerList = ArrayList<String>()
            spinnerList.add("0")
            for (i in ticket.minOrder..ticket.maxOrder) {
                spinnerList.add(Integer.toString(i))
            }
            itemView.orderRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    itemView.order.text = spinnerList[pos]
                    selectedListener?.onSelected(ticket.id, spinnerList[pos].toInt())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
            itemView.orderRange.adapter = ArrayAdapter(itemView.context, R.layout.select_dialog_singlechoice, spinnerList)
        }

        val price = StringBuilder()
        if (!eventCurrency.isNullOrEmpty()) {
            price.append(eventCurrency)
        }

        if (ticket.price != null) {
            price.append(ticket.price)
            itemView.price.visibility = View.VISIBLE
            itemView.price.text = price
        }

        if (ticket.price == 0.toFloat()) {
            itemView.price.text = "Free"
        }
    }
}