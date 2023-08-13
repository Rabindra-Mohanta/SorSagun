package alkusi.mahato.sarsogune.AllAdapter

import alkusi.mahato.sarsogune.AllActivity.ActivityNotificationClick
import alkusi.mahato.sarsogune.AllActivity.HomeActivity
import alkusi.mahato.sarsogune.R
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class AdapterRequested(var context:Context,var requestedList:ArrayList<DocumentSnapshot>,var storageReference: StorageReference?):RecyclerView.Adapter<AdapterRequested.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_requested,parent,false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
              var a = position;
              var item = requestedList.get(a);
               if(item.data==null)
               {
                   return;
               }
              var image = item.data!!.get(context.getString(R.string.fir_profileImage)).toString();
              if(image!=null && !TextUtils.isEmpty(image))
              {
                  storageReference = FirebaseStorage.getInstance().getReference("images/"+image)
                  if(storageReference!=null)
                  {
                      storageReference!!.downloadUrl.addOnSuccessListener {
                          Glide.with(context).load(it.toString()).placeholder(R.drawable.login_top).into(holder.imgProfile)
                      }
                  }
              }
        holder.textName.setText(item.data!!.get(context.getString(R.string.fir_Name)).toString())

        holder.itemView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent(context, ActivityNotificationClick::class.java)
                intent.putExtra("email",item.id)
                context.startActivity(intent)
            }

        })

    }
    override fun getItemCount(): Int {
        return  requestedList.size;
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
       val imgProfile = itemView.findViewById<CircleImageView>(R.id.imgProfile)
       val textName = itemView.findViewById<TextView>(R.id.textName)
    }

}