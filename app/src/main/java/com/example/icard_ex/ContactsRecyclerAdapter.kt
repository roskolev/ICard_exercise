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


class ContactsRecyclerAdapter(private val contacts: MutableList<String>, private val ids: MutableList<Int>, private val inAppNumber: Int) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.ItemViewHolder>() {
    private lateinit var dbHelper   :    DatabaseHelper
    private lateinit var ctx        :    Context

    private var appContactsLength   =    0
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        ctx                 =   parent.context
        dbHelper            =   DatabaseHelper(ctx)
        appContactsLength   =   contacts.size

        val contactItemView =   LayoutInflater.from(ctx)
                                    .inflate(R.layout.recyclerview_item_row, parent, false)
        
        return ItemViewHolder(contactItemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setContact(contacts[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
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
            itemView.contactName.text       =   contact
            itemView.contactName.textSize   =   18F

            itemView.contactName.setTextColor(Color.BLACK)
            itemView.setBackgroundColor(Color.WHITE)

            if(adapterPosition >= inAppNumber)
                itemView.onDeviceIcon.visibility = View.INVISIBLE
            else
                itemView.onDeviceIcon.visibility = View.VISIBLE

        }

        override fun onLongClick(v: View): Boolean {
            showOptionsPopup(itemView, adapterPosition)
            itemView.setBackgroundColor(Color.parseColor("#ededed"))
            return true
        }

        private fun showOptionsPopup(view: View, position: Int) {
            val optionsPopup = PopupMenu(ctx, view)
            optionsPopup.inflate(R.menu.contact_menu)

            if(adapterPosition >= inAppNumber)
                optionsPopup.menu.getItem(0).title = "Add contact to phone book"

            optionsPopup.setOnMenuItemClickListener { item: MenuItem ->
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
                        if (adapterPosition >= inAppNumber) {
                            ctx.startActivity(Intent(ctx, AddContactActivity::class.java)
                                                    .putExtra("IDDevice", ids[position]))
                        } else {
                            ctx.startActivity(Intent(ctx, EditContactActivity::class.java)
                                                    .putExtra("ID", ids[position]))
                        }
                    }
                }
                true
            }

            optionsPopup.setOnDismissListener {
                view.setBackgroundColor(Color.WHITE) }
            optionsPopup.show()
        }
    }
}
