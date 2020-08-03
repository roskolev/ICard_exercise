package com.example.icard_ex

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class ContactsRecyclerAdapter(private val contacts: MutableList<Contact>, private val inAppNumber: Int) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.ItemViewHolder>() {
    private lateinit var dbHelper   :    DatabaseHelper
    private lateinit var ctx        :    Context
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        ctx                 =   parent.context
        dbHelper            =   DatabaseHelper(ctx)

        val contactItemView =   LayoutInflater.from(ctx)
                                    .inflate(R.layout.recyclerview_item_row, parent, false)
        
        return ItemViewHolder(contactItemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setContact(contacts[position].fullname)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        
        override fun onClick(v: View) {
            val intent = Intent(ctx, DetailsActivity::class.java)
            
            if(adapterPosition < inAppNumber)
                intent.putExtra(ctx.getString(R.string.id), contacts[adapterPosition].id)
            else
                intent.putExtra(ctx.getString(R.string.device_id), contacts[adapterPosition].id)
            
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

        @SuppressLint("ResourceType")
        override fun onLongClick(v: View): Boolean {
            showOptionsPopup(itemView, adapterPosition)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                itemView.setBackgroundColor(ctx.getColor(R.color.colorClicked))
            else
                itemView.setBackgroundColor(Color.parseColor(ctx.getString(R.color.colorClicked)))

            return true
        }

        private fun showOptionsPopup(view: View, position: Int) {
            val optionsPopup = PopupMenu(ctx, view)
            optionsPopup.inflate(R.menu.contact_menu)

            if(adapterPosition >= inAppNumber)
                optionsPopup.menu.getItem(0).title = ctx.getString(R.string.add_contact_to_phonebook)

            optionsPopup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.actionDelete -> {
                        dbHelper.deleteContact(contacts[position].id)
                        contacts.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, contacts.size)
                        notifyDataSetChanged()
                    }
                    R.id.actionEdit -> {
                        if (adapterPosition >= inAppNumber) {
                            ctx.startActivity(Intent(ctx, AddContactActivity::class.java)
                                                    .putExtra(ctx.getString(R.string.device_id), contacts[position].id))
                        } else {
                            ctx.startActivity(Intent(ctx, EditContactActivity::class.java)
                                                    .putExtra(ctx.getString(R.string.id), contacts[position].id))
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
