package alkusi.mahato.sarsogune.AllAdapter

import alkusi.mahato.sarsogune.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterNotification(var context: Context):RecyclerView.Adapter<AdapterNotification.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return  10;
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {

    }
}