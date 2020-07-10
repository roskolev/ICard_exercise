package com.example.icard_ex

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class RecyclerAdapter(private val contacts: MutableList<String>, private val ids: MutableList<Int>, private val inAppNumber: Int) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var ctx : Context
    private var appContactsLength = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        dbHelper = DatabaseHelper(ctx)
        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.recyclerview_item_row, parent, false)
        appContactsLength = contacts.size
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setContact(contacts[position])
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnLongClickListener {
        private var view: View = v

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }
        override fun onClick(v: View) {
            val intent = Intent(ctx, DetailsActivity::class.java)
            if(adapterPosition < inAppNumber)
                intent.putExtra("ID", ids[adapterPosition])
            else
                intent.putExtra("IDDevice", ids[adapterPosition])
            ctx.startActivity(intent)
        }
        fun setContact(contact: String){
            view.contactName.text = contact
            view.contactName.setTextColor(Color.BLACK)
            view.contactName.textSize = 18F
            view.setBackgroundColor(Color.WHITE)
            if(adapterPosition >= inAppNumber)
                view.onDeviceIcon.visibility = View.INVISIBLE
            else
                view.onDeviceIcon.visibility = View.VISIBLE
        }
        override fun onLongClick(v: View): Boolean {
            showPopup(view, adapterPosition)
            view.setBackgroundColor(Color.parseColor("#ededed"))
            return true
        }
        private fun showPopup(view: View, position: Int) {
            val popup = PopupMenu(ctx, view)
            popup.inflate(R.menu.contact_menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        dbHelper.deleteContact(ids[position])
                        contacts.removeAt(position)
                        ids.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, contacts.size)
                        notifyDataSetChanged()
                    }
                    R.id.action_edit -> {
                        val intent = Intent(ctx, EditActivity::class.java)
                        if(adapterPosition >= inAppNumber) {
                            intent.putExtra("ID", -2)
                            intent.putExtra("IDDevice", ids[position])
                        }
                        else
                            intent.putExtra("ID", ids[position])
                        ctx.startActivity(intent)
                    }
                }
                true
            }
            popup.setOnDismissListener {
                view.setBackgroundColor(Color.WHITE) }
            popup.show()
        }
    }
}
