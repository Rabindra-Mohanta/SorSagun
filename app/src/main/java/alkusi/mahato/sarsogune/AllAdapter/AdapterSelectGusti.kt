package alkusi.mahato.sarsogune.AllAdapter

import alkusi.mahato.sarsogune.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterSelectGusti(var context: Context,var gustiList:ArrayList<String>,var listener:OnGustiCLick):RecyclerView.Adapter<AdapterSelectGusti.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_gusti,parent,false);
        return ViewHolder(view);
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = position;
        holder.txtGustiName.setText(gustiList.get(a));
        holder.itemView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                listener.onGustiClick(gustiList.get(a))
            }

        })

    }
    override fun getItemCount(): Int {
         return gustiList.size;
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
       val txtGustiName = itemView.findViewById<TextView>(R.id.txtGustiName);
    }
interface OnGustiCLick
{
    fun onGustiClick(gusti:String);
}
}