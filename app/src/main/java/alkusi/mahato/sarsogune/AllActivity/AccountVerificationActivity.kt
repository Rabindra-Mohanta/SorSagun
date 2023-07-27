package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityAccountVerificationBinding
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AccountVerificationActivity : BaseActivity() {
    lateinit var binding:ActivityAccountVerificationBinding;
    lateinit var toolbar: Toolbar;
    val REQ_IMAGE = 12;
    var imageUri:Uri?=null;
    lateinit var db:FirebaseFirestore;
     var storageReference:StorageReference?=null;
    var festivalImage:String? = null;
    val TAG = "AccountVerificationActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
        setTitle(resources.getString(R.string.msg_account_verfica))
        db = FirebaseFirestore.getInstance();

        init();
        isFestival()

    }
    private fun init()
    {
      binding.btnSelect.setOnClickListener(object :View.OnClickListener
      {
          override fun onClick(p0: View?) {
           val intent = Intent();
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(Intent.createChooser(intent,"select picture"),REQ_IMAGE)

          }

      })
        binding.btnSave.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                if(imageUri==null)
                {
                    Toast.makeText(this@AccountVerificationActivity,"Please select image",Toast.LENGTH_SHORT).show()
                       return
                }
                binding.progressbar.visibility = View.VISIBLE;
                val map:MutableMap<String,String> = HashMap();
                val simpleDateFormat = SimpleDateFormat("yyyy_MM-dd_HH_mm_ss", Locale.CANADA)
                val now  = Date();
                val fileName = "festival "+simpleDateFormat.format(now);
                uploadImage(fileName,imageUri!!)
                map.put(resources.getString(R.string.fir_festival_image),fileName)
                db.collection(resources.getString(R.string.fir_festival)).document(resources.getString(R.string.fir_festival_details)).set(map).addOnSuccessListener(object :OnSuccessListener<Void>
                {
                    override fun onSuccess(p0: Void?) {
                        binding.progressbar.visibility = View.GONE;
                        Toast.makeText(this@AccountVerificationActivity,"Success",Toast.LENGTH_SHORT).show();
                        finish()
                    }

                })
                    .addOnFailureListener()
                    {
                        binding.progressbar.visibility = View.GONE;
                        Toast.makeText(this@AccountVerificationActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();

                    }

            }

        })

        binding.imgDelete.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                if(festivalImage==null) return
                val alertDialog = android.app.AlertDialog.Builder(this@AccountVerificationActivity);
                alertDialog.setCancelable(true);
                alertDialog.setTitle("Festival delete");
                alertDialog.setMessage("Are you sure want to delete")
                alertDialog.setPositiveButton(android.R.string.ok,object : DialogInterface.OnClickListener
                {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                        deleteFestival( festivalImage!!)
                    }

                })
                alertDialog.setNegativeButton(android.R.string.cancel,object : DialogInterface.OnClickListener
                {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()

                    }

                })
                alertDialog.show();
            }

        })
    }
    private fun uploadImage(fileName:String,uri:Uri)
    {
        if(isConnectionAvailable())
        {

            storageReference =  FirebaseStorage.getInstance().getReference("images/"+fileName);
            storageReference!!.putFile(uri).addOnSuccessListener(object :OnSuccessListener<UploadTask.TaskSnapshot>
            {
                override fun onSuccess(task: UploadTask.TaskSnapshot?) {
                    storageReference!!.downloadUrl.addOnSuccessListener(object :OnSuccessListener<Uri>
                    {
                        override fun onSuccess(uri: Uri?) {

                        }
                    })
                }

            })
                .addOnFailureListener(object :OnFailureListener
                {
                    override fun onFailure(p0: Exception) {

                    }

                })
        }
        else
        {
            showNoNetworkMsg()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            REQ_IMAGE ->
            {
                if(data!=null && data.data!=null)
                {
                    imageUri = data.data;
                    binding.imageView.setImageURI(imageUri)

                }
            }
        }
    }


    private fun isFestival()
    {
        db.collection(resources.getString(R.string.fir_festival)).document(resources.getString(R.string.fir_festival_details)).get().addOnCompleteListener {
            if(it.isSuccessful)
            {
                val result = it.result
                Log.e(TAG,"data->"+result)
                if(result!=null)
                {
                    val image = result.getString(resources.getString(R.string.fir_festival_image))
                    festivalImage = image.toString()
                    val storageReference = FirebaseStorage.getInstance().getReference("images/"+image)
                    storageReference.downloadUrl.addOnSuccessListener {

                        Glide.with(this).load(it.toString()).into(binding.imageView)
                        binding.btnSave.visibility = View.GONE
                        binding.btnSelect.visibility = View.GONE
                        binding.imgDelete.visibility = View.VISIBLE

                    }
                }
            }
        }
    }
    private fun deleteFestival(image:String)
    {
        binding.progressbar.visibility = View.VISIBLE;
        val map:MutableMap<String,String> = HashMap();
        map.put("image","")

        db.collection(resources.getString(R.string.fir_festival)).document(resources.getString(R.string.fir_festival_details)).set(map).addOnSuccessListener(object :OnSuccessListener<Void>
        {
            override fun onSuccess(p0: Void?) {
                binding.progressbar.visibility = View.GONE;
                deleteImages(image)
            }

        })
            .addOnFailureListener()
            {
                binding.progressbar.visibility = View.GONE;
                Toast.makeText(this@AccountVerificationActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();

            }

    }

    private fun deleteImages(imgName:String)
    {
        val firebaseStorage = FirebaseStorage.getInstance();
        val storageRef = firebaseStorage.reference
        if(imgName!=null && !TextUtils.isEmpty(imgName)) {
            val desertRef = storageRef.child("images/$imgName")
            desertRef.delete().addOnCompleteListener {
                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
                finish()
            }



        }


    }
}