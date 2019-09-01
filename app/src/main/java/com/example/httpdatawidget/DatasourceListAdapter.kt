package com.example.httpdatawidget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.TextView


class DatasourceListAdapter : ArrayAdapter<DatasourceInfo> {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    constructor(context: Context, resource: Int) : super(context, resource) {

    }

//    fun setItems(items: List<DatasourceInfo>){
//        this.
//    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.list_item, parent, false)

        var textView = view.findViewById(R.id.textView) as TextView
        textView.text = getItem(position)?.name
        return view
    }
}