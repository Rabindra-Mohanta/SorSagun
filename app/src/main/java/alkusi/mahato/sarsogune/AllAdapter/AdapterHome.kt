package alkusi.mahato.sarsogune.AllAdapter
import alkusi.mahato.sarsogune.R
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.RecyclerView
import com.adwardstark.mtextdrawable.MaterialTextDrawable
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
class AdapterHome(var context: Context,var dataList:ArrayList<DocumentSnapshot>,var storageReference:StorageReference?,var listener:OnItemClick):RecyclerView.Adapter<AdapterHome.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home,parent,false);
        return ViewHolder(view);
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var a = position;
        var item = dataList.get(a)
        var data = item.data


        if(data!=null)
        {
            var profileImage:String? = data.get(context.resources.getString(R.string.fir_profileImage)) as String?;
            var name = data.get(context.getString(R.string.fir_CandidateName)) as String?
            var gusti = data.get(context.getString(R.string.fir_Gusti)) as String?
                var address = data.get(context.getString(R.string.fir_Address)) as String?
            var aboutUser = data.get(context.getString(R.string.fir_aboutMe)) as String?
            holder.txtUserName.setText(name+","+address+"\n"+"About- "+aboutUser)
            holder.btnGusti.setText(gusti)
            if(TextUtils.isEmpty(name))
            {
                name = "."
            }
            if(!TextUtils.isEmpty(profileImage))
            {
                storageReference = FirebaseStorage.getInstance().getReference("images/"+profileImage);
              if(storageReference!=null)
              {
                  storageReference!!.downloadUrl.addOnSuccessListener {
                      var url = it.toString();
                      Glide.with(context).load(url).placeholder(context.resources.getDrawable(R.drawable.login_top)).centerCrop().into(holder.imageView)
                      holder.imgUserCircle.visibility = View.VISIBLE
                      holder.userImg.visibility = View.GONE
                      Glide.with(context).load(url).placeholder(context.resources.getDrawable(R.drawable.login_top)).centerCrop().into(holder.imgUserCircle)

                  }
              }

                }
            else
            {
                holder.imgUserCircle.visibility = View.GONE
                holder.userImg.visibility = View.VISIBLE
                MaterialTextDrawable.with(context).shape(MaterialTextDrawable.MaterialShape.CIRCLE).text(name!!.get(0).toString()).into(holder.userImg)
                Glide.with(context).load(context.resources.getDrawable(R.drawable.login_top)).centerCrop().into(holder.imageView)
            }


            }


      holder.itemView.setOnClickListener(object :View.OnClickListener
      {
          override fun onClick(p0: View?) {
                if(dataList.size==0) return
             listener.onItemClick(dataList.get(a).id)




          }

      })



    }
    override fun getItemCount(): Int {
        if(dataList==null)
        {
            return 0;
        }
        else
        {
            return dataList.size;
        }

    }


    fun addData(listOfDocument:List<DocumentSnapshot>)
    {
        dataList.addAll(listOfDocument)
        notifyDataSetChanged()
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
    val imageView = itemView.findViewById<ImageView>(R.id.imageView)
    val imgUserCircle = itemView.findViewById<CircleImageView>(R.id.imgUserCircle)
        val userImg = itemView.findViewById<ImageView>(R.id.userImg)
        val txtUserName = itemView.findViewById<TextView>(R.id.txtUserName)
        val btnGusti = itemView.findViewById<Button>(R.id.btnGusti)

    }

interface  OnItemClick
    {
        fun onItemClick(email:String);
    }
}