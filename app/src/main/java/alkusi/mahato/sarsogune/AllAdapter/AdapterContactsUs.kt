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
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView


class AdapterContactsUs(var context: Context, var userList:ArrayList<QueryDocumentSnapshot>, var storageReference: StorageReference?, var listener:OnUserClick):
    RecyclerView.Adapter<AdapterContactsUs.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_us,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var a = position;
        var item = userList.get(a);
        var profileImage = item.get(context.resources.getString(R.string.fir_profileImage)).toString()
        if(!TextUtils.isEmpty(profileImage))
        {
            storageReference = FirebaseStorage.getInstance().getReference("images/"+profileImage)
            if(storageReference!=null)
            {
                storageReference!!.downloadUrl.addOnSuccessListener {
                    Glide.with(context).load(it).placeholder(R.drawable.splash).into(holder.circularImageView);
                }

            }
        }
        holder.texName.setText(item.get(context.resources.getString(R.string.fir_CandidateName)).toString())



        holder.itemView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                listener.onClickUser(item.id)
            }

        })
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val circularImageView = itemView.findViewById<CircleImageView>(R.id.circularImageView);
        val texName = itemView.findViewById<TextView>(R.id.texName);
    }

    interface OnUserClick
    {
        fun onClickUser(email:String)
    }
}