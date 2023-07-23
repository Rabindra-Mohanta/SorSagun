package alkusi.mahato.sarsogune.AllAdapter

import alkusi.mahato.sarsogune.AllActivity.FullImageViewActivity
import alkusi.mahato.sarsogune.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdapterImageList(var context: Context, var list:ArrayList<String>,var isFormUri:Boolean,var listener:OnClickListener,var isCancelBtn:Boolean):RecyclerView.Adapter<AdapterImageList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = position
        if(isFormUri)
        {
            Glide.with(context).load(Uri.parse(list.get(a))).placeholder(R.drawable.icon_image).centerCrop().into(holder.imageView);
        }
        else
        {
            Glide.with(context).load(list.get(a)).placeholder(R.drawable.icon_image).centerCrop().into(holder.imageView);
        }

if(isCancelBtn)
{
    holder.imgCancel.visibility = View.VISIBLE
}
        else
{
    holder.imgCancel.visibility = View.GONE
}

            holder.imgCancel.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(p0: View?) {
                    list.removeAt(a);
                    notifyDataSetChanged();
                    if(!isFormUri)
                    {
                        listener.onCancelImage(a)
                    }
                }

            })

        holder.imageView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent(context,FullImageViewActivity::class.java)
                intent.putExtra("url",list.get(a));
                intent.putExtra("isUri",isFormUri)
                context.startActivity(intent)
            }

        })


    }
    override fun getItemCount(): Int {
        return list.size;
    }

    fun getData():List<String>
    {
        return list;
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
         val imageView = itemView.findViewById<ImageView>(R.id.imageView);
        val imgCancel = itemView.findViewById<ImageView>(R.id.imgCancel);
    }
    interface OnClickListener
    {
        fun onCancelImage(pos:Int)
    }
}