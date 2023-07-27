package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterImageList
import alkusi.mahato.sarsogune.NotificationByFcm.Api.ApiUtilities
import alkusi.mahato.sarsogune.NotificationByFcm.Model.NotificationData
import alkusi.mahato.sarsogune.NotificationByFcm.Model.PushNotification
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityNotificationClickBinding
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ActivityNotificationClick : BaseActivity(),AdapterImageList.OnClickListener {
    lateinit var binding:ActivityNotificationClickBinding;
    lateinit var toolbar: Toolbar;
    lateinit var db:FirebaseFirestore;
    var storageReference:StorageReference?=null;
    var email:String?=null;
    var isAdmin:Boolean= false;
    var isContactUs:Boolean= false;
    var lat:Double?=null;
    var long:Double?=null;
    val TAG = "ActivityNotificationClick"
    var profileImage = "";
    var allImages = ArrayList<String>();

    var userPassword:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationClickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
        setTitle(resources.getString(R.string.app_name))
        db =  FirebaseFirestore.getInstance();
        if(intent.hasExtra("isAdmin"))
        {
            isAdmin = intent.getBooleanExtra("isAdmin",false)
        }

        if(intent.hasExtra("isContactUs"))
        {
            isContactUs = intent.getBooleanExtra("isContactUs",false)
        }
        if(intent.hasExtra("email"))
        {

            email = intent.getStringExtra("email")

            db.collection(resources.getString(R.string.fir_Users)).document(email!!).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>

            {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if(task.isSuccessful)
                    {

                        init(task.result)
                    }
                    else
                    {

                    }
                }
            })
        }
init2();
    }
    private fun init2()
    {
        binding.btnView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent(this@ActivityNotificationClick, MapsActivity::class.java)
                intent.putExtra("isViewLocation",true);
                intent.putExtra("latitude",lat)
                intent.putExtra("longitude",long)
                startActivity(intent)
            }

        })
    }
    private fun init(documentSnapshot: DocumentSnapshot)
    {
         if(HomeActivity.isAdmin|| HomeActivity.isSuperAdamin)
         {
             binding.contactNoll.visibility = View.VISIBLE
         }
        else
         {
             binding.contactNoll.visibility = View.GONE
         }
        if(isContactUs)
        {
            binding.contactNoll.visibility = View.VISIBLE
            binding.btnProposal.visibility = View.GONE
            binding.llTraditionBiha.visibility = View.GONE
        }
        binding.edtName.setText(documentSnapshot.get(resources.getString(R.string.fir_CandidateName)).toString())
        setTitle(binding.edtName.text)
        binding.edtFatherName.setText(documentSnapshot.get(resources.getString(R.string.fir_FatherName)).toString())
        binding.edtMotherName.setText(documentSnapshot.get(resources.getString(R.string.fir_MotherName)).toString())
        binding.edtContactNo.setText(documentSnapshot.get(resources.getString(R.string.fir_ContactNo)).toString())
        binding.edtDob.setText(documentSnapshot.get(resources.getString(R.string.fir_DOB)).toString())
        binding.edtGender.setText(documentSnapshot.get(resources.getString(R.string.fir_Gender)).toString())
        binding.edtMaritalStatus.setText(documentSnapshot.get(resources.getString(R.string.fir_MartialStatus)).toString())
        binding.edtLanguage.setText(documentSnapshot.get(resources.getString(R.string.fir_LanguageKnown)).toString())
        binding.edtHeight.setText(documentSnapshot.get(resources.getString(R.string.fir_CandidateHeight)).toString())
        binding.edtGusti.setText(documentSnapshot.get(resources.getString(R.string.fir_Gusti)).toString())
        binding.edtQualification.setText(documentSnapshot.get(resources.getString(R.string.fir_Qualification)).toString())
        binding.edtProfession.setText(documentSnapshot.get(resources.getString(R.string.fir_Profession)).toString())
        binding.edtAddress.setText(documentSnapshot.get(resources.getString(R.string.fir_Address)).toString())
        if(documentSnapshot.get(resources.getString(R.string.txt_Kudmali_marraige))!=null)
        {
            binding.kudmaliMerrageCheckBOx.isChecked = documentSnapshot.get(resources.getString(R.string.txt_Kudmali_marraige)) as Boolean

        }
        userPassword = documentSnapshot.getString(resources.getString(R.string.fir_account_password)).toString()
        binding.edtSmoke.setText(documentSnapshot.get(resources.getString(R.string.fir_smoke)).toString())
        binding.edtConsumeAlcohol.setText(documentSnapshot.get(resources.getString(R.string.fir_consume_alcohol)).toString())
        binding.edtAboutMe.setText(documentSnapshot.get(resources.getString(R.string.fir_aboutMe)).toString())
        if(documentSnapshot.getDouble(resources.getString(R.string.fir_latitude)) !=null)
        {
            lat = documentSnapshot.getDouble(resources.getString(R.string.fir_latitude))
        }

        if(documentSnapshot.getDouble(resources.getString(R.string.fir_longitude))!=null)
        {
            long = documentSnapshot.getDouble(resources.getString(R.string.fir_longitude))
        }
        //check is requested or note

        // binding.progressbar.visibility = View.VISIBLE
        db.collection(resources.getString(R.string.fir_Users)).document(documentSnapshot.id).collection(resources.getString(R.string.fir_requested)).document(email!!).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>
        {
            override fun onComplete(task: Task<DocumentSnapshot>) {

                if(task.isSuccessful)
                {

                    if(task.result.data!=null)
                    {
                        binding.btnProposal.setText(resources.getString(R.string.txt_requested))
                    }
                    else
                    {
                        binding.btnProposal.setText(resources.getString(R.string.txt_send_proposal))
                    }

                }
                else
                {

                }

            }

        })


        binding.btnProposal.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(p0: View?) {
                if(binding.btnProposal.text.toString().equals(resources.getString(R.string.txt_send_proposal),ignoreCase = true))
                {
                    binding.btnProposal.setText(resources.getString(R.string.txt_requested))
                    updateAcceptReject(documentSnapshot)
                }
            }

        })

        if(documentSnapshot.get(resources.getString(R.string.fir_profileImage)) != null)
        {
            var imag = documentSnapshot.get(resources.getString(R.string.fir_profileImage)).toString()
            profileImage = imag
            storageReference =  FirebaseStorage.getInstance().getReference("images/"+imag);
            storageReference!!.downloadUrl.addOnSuccessListener {uri->


                Glide.with(this).load(uri).into(binding.imageView)

                binding.imageView.setOnClickListener(object : View.OnClickListener
                {
                    override fun onClick(p0: View?) {
                        val intent = Intent(this@ActivityNotificationClick, FullImageViewActivity::class.java)
                        intent.putExtra("url",uri.toString());
                        intent.putExtra("isUri",false)
                        startActivity(intent)
                    }

                })

            }
        }

        if(documentSnapshot.get(resources.getString(R.string.fir_candidateImages)) !=null)
        {
            val candidateImagesListUrl = ArrayList<String>();
            var  allImageList = documentSnapshot.get(resources.getString(R.string.fir_candidateImages)) as ArrayList<String>;
            allImages.clear();
            allImages.addAll(allImageList)
            val adapterSelectedImage = AdapterImageList(this,candidateImagesListUrl,false,this,false);
            binding.imageRv.setHasFixedSize(true);
            binding.imageRv.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);
            binding.imageRv.adapter = adapterSelectedImage;



            for(i in allImageList.indices)
            {
                storageReference =  FirebaseStorage.getInstance().getReference("images/"+allImageList.get(i));
                storageReference!!.downloadUrl.addOnSuccessListener {uri->

                    candidateImagesListUrl.add(uri.toString())
                    adapterSelectedImage!!.notifyDataSetChanged();

                }
            }

        }

    }
    private fun updateAcceptReject(documentSnapshot: DocumentSnapshot?)
    {
        if(documentSnapshot==null) return
        if(HomeActivity.myData==null) return;
        if(!isConnectionAvailable())
        {
            showNoNetworkMsg()
            return;
        }

        val map:MutableMap<String,Any> = HashMap();
        map.put(resources.getString(R.string.fir_Name),HomeActivity.myData!!.data!!.get(resources.getString(R.string.fir_CandidateName)).toString())
        map.put(resources.getString(R.string.fir_profileImage), HomeActivity.myData!!.data!!.get(resources.getString(R.string.fir_profileImage)).toString());
        map.put(resources.getString(R.string.fir_time_stamp), Calendar.getInstance().timeInMillis);

        db.collection(resources.getString(R.string.fir_Users)).document(documentSnapshot.id).collection(resources.getString(R.string.fir_requested)).document(HomeActivity.email).set(map).addOnCompleteListener(object :
            OnCompleteListener<Void>
        {
            override fun onComplete(task: Task<Void>) {
                if(task.isSuccessful)
                {
                    sendNotification(documentSnapshot!!.get(resources.getString(R.string.txt_notification)).toString())
                }
                else
                {

                }
            }


        })



    }
    private fun sendNotification(token:String)
    {
        val name = HomeActivity.myData!!.data!!.get(resources.getString(R.string.fir_CandidateName).toString())
        val notificationData = PushNotification(NotificationData("||johar jay goram || New Request","$name Has selected your profile.",HomeActivity.email), token)


        ApiUtilities.getInstance().sendNotification(notificationData)
            .enqueue(object : Callback<PushNotification>
            {
                override fun onResponse(call: Call<PushNotification>, response: Response<PushNotification>) {

                }

                override fun onFailure(call: Call<PushNotification>, t: Throwable) {

                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(HomeActivity.isAdmin||HomeActivity.isSuperAdamin)
        {
            menuInflater.inflate(R.menu.menu_delete,menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menuDelete->
            {
                val alertDialog = android.app.AlertDialog.Builder(this);
                alertDialog.setCancelable(true);
                alertDialog.setTitle("Account delete");
                alertDialog.setMessage("Are you sure want to delete")


                alertDialog.setPositiveButton(android.R.string.ok,object : DialogInterface.OnClickListener
                {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        p0!!.dismiss()
                    }

                })
                alertDialog.setNegativeButton(android.R.string.cancel,object : DialogInterface.OnClickListener
                {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                        deleteAccount()
                    }

                })
                alertDialog.show();

            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun deleteAccount()
    {
        deleteFromFireStore(email!!)


    }
    override fun onCancelImage(pos: Int) {

    }
    private fun deleteFromFireStore(id:String)
    {

        db.collection(resources.getString(R.string.fir_Users)).document(id).delete().addOnCompleteListener {
            if(it.isSuccessful)
            {
                deleteFromLocations(id)
            }
        }
    }
    private fun deleteFromLocations(id:String)
    {
        db.collection(resources.getString(R.string.fir_locations)).document(id).delete().addOnCompleteListener {
            if(it.isSuccessful)
            {
                deleteImages()

            }
        }

    }
    private fun deleteImages()
    {
        val firebaseStorage = FirebaseStorage.getInstance();
        val storageRef = firebaseStorage.reference
        if(profileImage!=null && !TextUtils.isEmpty(profileImage))
        {
            val desertRef = storageRef.child("images/$profileImage")
            desertRef.delete().addOnCompleteListener {

            }
        }
      if(allImages.size>0)
      {
          for(i in allImages.indices)
          {
              val desertRef = storageRef.child("images/${allImages.get(i)}")
              desertRef.delete().addOnCompleteListener {
                   if(i==allImages.size-1)
                   {
                       Toast.makeText(this,"Account deleted",Toast.LENGTH_SHORT).show()
                       finish()
                   }
              }
          }
      }
        else
      {
          Toast.makeText(this,"Account deleted",Toast.LENGTH_SHORT).show()
          finish()
      }

    }

}