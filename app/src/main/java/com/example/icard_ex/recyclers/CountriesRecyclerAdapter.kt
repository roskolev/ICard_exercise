package com.example.icard_ex.recyclers

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.icard_ex.DatabaseHelper
import com.example.icard_ex.R
import com.example.icard_ex.models.Country
import kotlinx.android.synthetic.main.activity_base_contact.*
import kotlinx.android.synthetic.main.country_spinner_row.view.*


class CountriesRecyclerAdapter(private val context: Activity, private var countries: MutableList<Country>, private val dialog: Dialog)
    :RecyclerView.Adapter<CountriesRecyclerAdapter.ItemViewHolder>() {
    private lateinit var dbHelper   :   DatabaseHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        dbHelper            =   DatabaseHelper(context)
        val countryItemView =   LayoutInflater.from(context).inflate(R.layout.country_spinner_row, parent, false )

        return ItemViewHolder(countryItemView)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setRow(countries[position])

    }

    fun updateList(newList: MutableList<Country>) {
        countries = newList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private var country =   Country()

        fun setRow(country: Country) {
            this.country                = country
            itemView.countryName.text   = country.name
            itemView.countryCode.text   = context.getString(R.string.country_code, country.codeStr)
            itemView.setBackgroundColor(Color.WHITE)
        }

        override fun onClick(v: View?) {
            context.searchCountryDialog.id = country.id
            context.searchCountryDialog.setText(country.name)
            context.editTextPhoneCode.setText(context.getString(R.string.country_code, country.codeStr))
            dialog.dismiss()
        }
    }
}