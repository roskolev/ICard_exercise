package com.example.icard_ex.recyclers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.icard_ex.*
import com.example.icard_ex.helpers.DatabaseHelper
import com.example.icard_ex.models.Contact
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class ContactsRecyclerAdapter(private val contacts: MutableList<Contact>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.ItemViewHolder>() {
    private lateinit var dbHelper   : DatabaseHelper
    private lateinit var ctx        :    Context
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        ctx                 =   parent.context
        dbHelper            = DatabaseHelper(ctx)

        val contactItemView =   LayoutInflater.from(ctx)
                                    .inflate(R.layout.recyclerview_item_row, parent, false)
        
        return ItemViewHolder(contactItemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.initContact(contacts[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        private lateinit var contact: Contact
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        
        override fun onClick(v: View) {
            val intent = Intent(ctx, DetailsActivity::class.java)
            
            if(contact.inApp)
                intent.putExtra(ctx.getString(R.string.id), contacts[adapterPosition].id)
            else
                intent.putExtra(ctx.getString(R.string.device_id), contacts[adapterPosition].id)
            
            ctx.startActivity(intent)
        }

        fun initContact(contact: Contact){
            this.contact = contact
            itemView.contactName.text       =   contact.fullname
            itemView.contactName.textSize   =   18F

            itemView.contactName.setTextColor(Color.BLACK)
            itemView.setBackgroundColor(Color.WHITE)

            if(contact.inApp)
                itemView.onDeviceIcon.visibility = View.VISIBLE
            else
                itemView.onDeviceIcon.visibility = View.INVISIBLE

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

            if(!contact.inApp)
                optionsPopup.menu.getItem(0).title = ctx.getString(R.string.add_contact_to_phonebook)

            optionsPopup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.actionDelete -> {
                        AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.delete_name, contacts[position].fullname))
                            .setPositiveButton(R.string.yes) { _, _ ->
                                dbHelper.deleteContact(contacts[position].id)
                                contacts.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, contacts.size)
                                notifyDataSetChanged()
                            }
                            .setNegativeButton(R.string.no){_, _ ->
                            }.show()
                    }
                    R.id.actionEdit -> {
                        if (!contact.inApp) {
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
