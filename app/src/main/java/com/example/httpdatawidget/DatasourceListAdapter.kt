package com.example.httpdatawidget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.httpdatawidget.storage.DatasourceInfo


class DatasourceListAdapter : ArrayAdapter<DatasourceInfo> {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    constructor(context: Context, resource: Int) : super(context, resource) {

    }

//    fun setItems(items: List<DatasourceInfo>){
//        this.
//    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.list_item, parent, false)

        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.listitem_name)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        textView.text = item?.name

        if (item?.online == true) {
            imageView.setImageResource(R.drawable.ic_cloud_done_black_24dp)
        } else {
            imageView.setImageResource(R.drawable.ic_cloud_off_black_24dp)
        }
        return view
    }
}