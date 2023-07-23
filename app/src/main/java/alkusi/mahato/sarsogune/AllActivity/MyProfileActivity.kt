package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterImageList
import alkusi.mahato.sarsogune.AllFragments.SelectGustiFragment
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityMyProfileBinding
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyProfileActivity : BaseActivity(),SelectGustiFragment.OnGustiSelected,AdapterImageList.OnClickListener {
    lateinit var binding:ActivityMyProfileBinding;
    lateinit var toolbar: Toolbar;
    val REQ_FOR_PROFILE_IMAGE = 12;
    val REQ_FOR_MULTIPLE_IMAGE = 13;
    val REQ_ADD_LOCATION = 14;
    var genderAdapter:ArrayAdapter<String>?=null;
    var manglikAdapter:ArrayAdapter<String>?=null;
    var alcoholAdapter:ArrayAdapter<String>?=null;
    var somkeAdapter:ArrayAdapter<String>?=null;
    var maritalStatusAdapter:ArrayAdapter<String>?=null;
    var selectGustiFragment:SelectGustiFragment?=null;
    var imgSelectedList = ArrayList<String>();
    var adapterSelectedImage:AdapterImageList?=null;
    lateinit var db:FirebaseFirestore;
    lateinit var storageReference:StorageReference;
    var profileUri:Uri? =null;
    var profileImage:String?=null;
    var profileImageUrlLinke:String = ""
    var candidateImagesListUrl = ArrayList<String>();
    var candidateImagesListUrlNew = ArrayList<String>();
    val TAG = "MyProfileActivity"
    var notificationId:String = ""
    var myData:DocumentSnapshot?=null;
    var lat:Double? = null;
    var long:Double? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater);
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
        setTitle(resources.getString(R.string.txt_myAccount))
        selectGustiFragment = SelectGustiFragment(this)
        db = FirebaseFirestore.getInstance();

        init();
        init2();
        setData()
        init3()
    }

    override fun onResume() {
        super.onResume()

    }
    private fun init3()
    {
        binding.btnAddLocation.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
              val intent = Intent(this@MyProfileActivity,MapsActivity::class.java)
                intent.putExtra("isAddLocation",true);
                startActivityForResult(intent,REQ_ADD_LOCATION)

            }

        })

        binding.btnViewLocation.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                if(lat!=null && long!=null)
                {
                    val intent = Intent(this@MyProfileActivity,MapsActivity::class.java)
                    intent.putExtra("isViewLocation",true);
                    intent.putExtra("latitude",lat)
                    intent.putExtra("longitude",long)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this@MyProfileActivity,"Your location data not available",Toast.LENGTH_SHORT).show()
                }

            }
        })

    }
    private fun init()
    {
        genderAdapter = ArrayAdapter<String>(this,R.layout.item_spinner,R.id.edtSpinner,resources.getStringArray(R.array.array_gender));
        binding.spinnerGender.adapter = genderAdapter;
        maritalStatusAdapter = ArrayAdapter<String>(this,R.layout.item_spinner,R.id.edtSpinner,resources.getStringArray(R.array.array_yes_no));
        binding.spinnerMartialStatus.adapter = maritalStatusAdapter;


        alcoholAdapter = ArrayAdapter<String>(this,R.layout.item_spinner,R.id.edtSpinner,resources.getStringArray(R.array.array_alcohol));
        binding.spinnerConsumeAlcohol.adapter = alcoholAdapter;


        somkeAdapter = ArrayAdapter<String>(this,R.layout.item_spinner,R.id.edtSpinner,resources.getStringArray(R.array.array_smoke));
        binding.spinnerSmoke.adapter = somkeAdapter;



        binding.imgUploadProfile.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(p0: View?) {
               val intent = Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQ_FOR_PROFILE_IMAGE);
            }

        })
        binding.imgEditName.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                DialogeditName();
            }

        })

        binding.edtGusti.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                selectGustiFragment!!.show(supportFragmentManager,"Select Gusti")
            }

        })

        binding.btnUploadImages.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent();
                intent.setType("image/*")
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select 3 Picture"),REQ_FOR_MULTIPLE_IMAGE);

            }

        })

        binding.edtDob.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val calendar = Calendar.getInstance();
                val year = calendar.get(Calendar.YEAR);
                val month = calendar.get(Calendar.MONTH);
                val date = calendar.get(Calendar.DAY_OF_MONTH);


            }

        })

    }

private fun init2()
{
    FirebaseMessaging.getInstance().token.addOnCompleteListener {
        if(it.isSuccessful) {
           notificationId = it.result.toString()
        }
    }

    binding.btnUpdateProfile.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            updateData();
        }

    })

    binding.edtDob.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val calendar = Calendar.getInstance();
            val year = calendar.get(Calendar.YEAR);
            val month = calendar.get(Calendar.MONTH);
            val date = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this@MyProfileActivity,object :DatePickerDialog.OnDateSetListener
            {
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                     binding.edtDob.setText(""+p3+"-"+(p2+1)+"-"+p1)
                }

            },year,month,date)
            datePickerDialog.show();

        }

    })

    binding.imgProfile.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            if(profileImage!=null && !TextUtils.isEmpty(profileImage))
            {
                val intent = Intent(this@MyProfileActivity,FullImageViewActivity::class.java)
                intent.putExtra("url",profileImageUrlLinke);
                intent.putExtra("isUri",false)
                startActivity(intent)
            }
            else if(profileUri!=null)
            {
                val intent = Intent(this@MyProfileActivity,FullImageViewActivity::class.java)
                intent.putExtra("url",profileUri.toString());
                intent.putExtra("isUri",true)
                startActivity(intent)
            }
        }

    })
}
    private fun setData()
    {
        if(!isConnectionAvailable())
        {
            showNoNetworkMsg()
            return

        }
        binding.progressbar.visibility = View.VISIBLE
             db.collection(resources.getString(R.string.fir_Users)).document(HomeActivity.email).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>
             {
                 override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful)
                     {
                      val result = task.result;
                         myData = result;
                         binding.txtName.setText(result.getString(resources.getString(R.string.fir_Name)))
                         var checkedMarraige:Boolean? = result.getBoolean(resources.getString(R.string.txt_Kudmali_marraige)) as Boolean?
                         if(checkedMarraige==null)
                         {
                             checkedMarraige = false
                         }
                         binding.kudmaliMerrageCheckBOx.isChecked = checkedMarraige
                      binding.edtCandidateName.setText(result.getString(resources.getString(R.string.fir_CandidateName)))
                      binding.edtFatherName.setText(result.getString(resources.getString(R.string.fir_FatherName)))
                      binding.edtMotherName.setText(result.getString(resources.getString(R.string.fir_MotherName)))
                      binding.edtContact.setText(result.getString(resources.getString(R.string.fir_ContactNo)))
                      binding.edtDob.setText(result.getString(resources.getString(R.string.fir_DOB)))
                         var genderStr = result.getString(resources.getString(R.string.fir_Gender));
                         binding.spinnerGender.setSelection(genderAdapter!!.getPosition(genderStr))
                         var alchoholStr = result.getString(resources.getString(R.string.fir_consume_alcohol))
                         binding.spinnerConsumeAlcohol.setSelection(alcoholAdapter!!.getPosition(alchoholStr))
                         var smokeStr = result.getString(resources.getString(R.string.fir_smoke));
                         binding.spinnerSmoke.setSelection(somkeAdapter!!.getPosition(smokeStr))
                         var martialStatus = result.getString(resources.getString(R.string.fir_MartialStatus))
                         binding.spinnerMartialStatus.setSelection(maritalStatusAdapter!!.getPosition(martialStatus))
                         binding.edtLanguageKnown.setText(result.getString(resources.getString(R.string.fir_LanguageKnown)))
                         binding.edtCandidateHeight.setText(result.getString(resources.getString(R.string.fir_CandidateHeight)))
                         binding.edtGusti.setText(result.getString(resources.getString(R.string.fir_Gusti)))
                         binding.edtQualification.setText(result.getString(resources.getString(R.string.fir_Qualification)))
                         binding.edtProfession.setText(result.getString(resources.getString(R.string.fir_Profession)))
                         binding.edtAddress.setText(result.getString(resources.getString(R.string.fir_Address)))
                         binding.edtAboutMe.setText(result.getString(resources.getString(R.string.fir_aboutMe)));
                         profileImage = result.getString(resources.getString(R.string.fir_profileImage));
                         if(result.getDouble(resources.getString(R.string.fir_latitude)) !=null)
                         {
                             lat = result.getDouble(resources.getString(R.string.fir_latitude))
                         }

                         if(result.getDouble(resources.getString(R.string.fir_longitude))!=null)
                         {
                             long = result.getDouble(resources.getString(R.string.fir_longitude))
                         }
                         if(lat!=null && long!=null)
                         {
                             binding.btnViewLocation.visibility=View.VISIBLE
                         }
                         if(!TextUtils.isEmpty(profileImage))
                         {
                             storageReference =  FirebaseStorage.getInstance().getReference("images/"+profileImage);
                             storageReference.downloadUrl.addOnSuccessListener {uri->
                                 profileImageUrlLinke = uri.toString();
                                 Glide.with(this@MyProfileActivity).asBitmap().load(uri.toString()).placeholder(R.drawable.splash).centerCrop().into(binding.imgProfile)
                             }
                         }
                            if(result.get(resources.getString(R.string.fir_candidateImages)) !=null)
                            {
                                candidateImagesListUrl.clear()
                                candidateImagesListUrlNew.clear()
                                imgSelectedList.clear();
                                adapterSelectedImage = AdapterImageList(this@MyProfileActivity,candidateImagesListUrl,false,this@MyProfileActivity,true);
                                binding.imgRecyclerView.setHasFixedSize(true);
                                binding.imgRecyclerView.layoutManager = LinearLayoutManager(this@MyProfileActivity,LinearLayoutManager.HORIZONTAL,false);
                                binding.imgRecyclerView.adapter = adapterSelectedImage;

                             var  allImageList = result.get(resources.getString(R.string.fir_candidateImages)) as ArrayList<String>;
                                candidateImagesListUrlNew.addAll(allImageList);

                                for(i in allImageList.indices)
                                {
                                    storageReference =  FirebaseStorage.getInstance().getReference("images/"+allImageList.get(i));
                                    storageReference.downloadUrl.addOnSuccessListener {uri->

                                        candidateImagesListUrl.add(uri.toString())
                                        adapterSelectedImage!!.notifyDataSetChanged();

                                    }
                                }

                            }



                         binding.progressbar.visibility = View.GONE
                     }
                     else
                    {
                        binding.progressbar.visibility = View.GONE
                        Toast.makeText(this@MyProfileActivity,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
                        finish()
                    }
                 }

             })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            REQ_ADD_LOCATION ->
            {
                if(resultCode==RESULT_OK)
                {
                    if(data==null)
                    {
                        return
                    }
                    lat = data.getDoubleExtra("latitude",0.0)
                    long = data.getDoubleExtra("longitude",0.0);
                    if(lat!=null && long!=null)
                    {
                        binding.btnViewLocation.visibility = View.VISIBLE;
                    }
                }
            }
            REQ_FOR_PROFILE_IMAGE ->
            {
                if(resultCode == RESULT_OK)
                {
                  val uri = data!!.data;
                    if(uri!=null)
                    {
                        profileUri = uri
                        binding.imgProfile.setImageURI(uri);
                    }

                }

            }
            REQ_FOR_MULTIPLE_IMAGE->
            {
                imgSelectedList.clear();
                candidateImagesListUrl.clear();
                candidateImagesListUrlNew.clear();
                adapterSelectedImage = AdapterImageList(this@MyProfileActivity,imgSelectedList,true,this,true);
                binding.imgRecyclerView.setHasFixedSize(true);
                binding.imgRecyclerView.layoutManager = LinearLayoutManager(this@MyProfileActivity,LinearLayoutManager.HORIZONTAL,false);
                binding.imgRecyclerView.adapter = adapterSelectedImage;
                adapterSelectedImage!!.notifyDataSetChanged();

                if(data==null)
                {
                    return;
                }

             var singleImage = data.data;
                if(singleImage!=null)
                {
                    imgSelectedList.add(singleImage.toString());
                }
                else  if(data!!.clipData!=null)
                {
                    var count = 3;
                    if(data!!.clipData!!.itemCount<3)
                    {
                        count = data!!.clipData!!.itemCount
                    }
                    if(data!!.clipData!!.itemCount>3)
                    {
                        Toast.makeText(this,"We are allowing only 3 images",Toast.LENGTH_SHORT).show()
                    }
                    for(i in 0 .. (count-1))
                    {
                        val uri = data.clipData!!.getItemAt(i).uri
                        imgSelectedList.add(uri.toString())
                    }
                }

                adapterSelectedImage!!.notifyDataSetChanged();
            }
        }
    }
private fun DialogeditName()
{
    var dialog = Dialog(this);
     dialog.setContentView(R.layout.edit_text_dialog);
    dialog.setCancelable(true);
    val edtName:TextInputEditText = dialog.findViewById(R.id.edtName);
    val btnSave:Button = dialog.findViewById(R.id.btnSave);
    dialog.show();
    btnSave.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {
            if(TextUtils.isEmpty(edtName.text.toString()))
            {
                edtName.setError(resources.getString(R.string.txt_enterValid_name));
            }
            else
            {
                binding.txtName.setText(edtName.text.toString());
                dialog.dismiss();
            }
        }

    })

}

    override fun onGustiSelected(gusti: String) {

       binding.edtGusti.setText(gusti)

    }
    private fun updateLatLong(lat:Double,long:Double)
    {
        val map:MutableMap<String,Any> = HashMap();
        map.put(resources.getString(R.string.fir_latitude),lat!!);
        map.put(resources.getString(R.string.fir_longitude),long!!)

        db.collection(resources.getString(R.string.fir_locations)).document(HomeActivity.email).set(map).addOnSuccessListener(object :OnSuccessListener<Void>
        {
            override fun onSuccess(p0: Void?) {
                binding.progressbar.visibility = View.GONE;
                Toast.makeText(this@MyProfileActivity,"success",Toast.LENGTH_SHORT).show();
               val intent = Intent(this@MyProfileActivity,HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            }

        })
            .addOnFailureListener()
            {
                binding.progressbar.visibility = View.GONE;
                Toast.makeText(this,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
            }
    }
    private fun updateData()
    {

        if(isConnectionAvailable())
        {
           if(isValid())
           {
               binding.progressbar.visibility = View.VISIBLE
               val map:MutableMap<String,Any> = HashMap();
               map.put(resources.getString(R.string.fir_Name),binding.txtName.text.toString())
               map.put(resources.getString(R.string.fir_CandidateName),binding.edtCandidateName.text.toString())
               map.put(resources.getString(R.string.fir_FatherName),binding.edtFatherName.text.toString())
               map.put(resources.getString(R.string.fir_MotherName),binding.edtMotherName.text.toString())
               map.put(resources.getString(R.string.fir_ContactNo),binding.edtContact.text.toString())
               map.put(resources.getString(R.string.fir_DOB),binding.edtDob.text.toString())
               map.put(resources.getString(R.string.fir_Gender),binding.spinnerGender.selectedItem.toString())
               map.put(resources.getString(R.string.fir_MartialStatus),binding.spinnerMartialStatus.selectedItem.toString())
               map.put(resources.getString(R.string.fir_LanguageKnown),binding.edtLanguageKnown.text.toString());
               map.put(resources.getString(R.string.fir_CandidateHeight),binding.edtCandidateHeight.text.toString());
               map.put(resources.getString(R.string.fir_Gusti),binding.edtGusti.text.toString());
               map.put(resources.getString(R.string.fir_Qualification),binding.edtQualification.text.toString());
               map.put(resources.getString(R.string.fir_Profession),binding.edtProfession.text.toString());
               map.put(resources.getString(R.string.fir_Address),binding.edtAddress.text.toString())
               map.put(resources.getString(R.string.txt_Kudmali_marraige),binding.kudmaliMerrageCheckBOx.isChecked)
               map.put(resources.getString(R.string.txt_notification),notificationId)
               map.put(resources.getString(R.string.fir_aboutMe),binding.edtAboutMe.text.toString())
               map.put(resources.getString(R.string.fir_consume_alcohol),binding.spinnerConsumeAlcohol.selectedItem.toString());
               map.put(resources.getString(R.string.fir_smoke),binding.spinnerSmoke.selectedItem.toString())
               val sharedPreferences = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

               var passWord = sharedPreferences.getString(resources.getString(R.string.shard_pre_pass),"");
               map.put(resources.getString(R.string.fir_account_password),passWord.toString())
               map.put(resources.getString(R.string.fir_time_stamp),Calendar.getInstance().timeInMillis)
               map.put(resources.getString(R.string.fir_latitude),lat!!);
               map.put(resources.getString(R.string.fir_longitude),long!!)

               if(myData!=null && myData!!.data!=null )
               {

                   map.put(resources.getString(R.string.fir_isSuperAdmin),myData!!.data!!.get(resources.getString(R.string.fir_isSuperAdmin))as Boolean)
                   map.put(resources.getString(R.string.fir_isAdmin),myData!!.data!!.get(resources.getString(R.string.fir_isAdmin))as Boolean)
               }
               else
               {
                   map.put(resources.getString(R.string.fir_isSuperAdmin),false)
                   map.put(resources.getString(R.string.fir_isAdmin),false)
               }

               if(profileUri!=null)
               {
                   val simpleDateFormat = SimpleDateFormat("yyyy_MM-dd_HH_mm_ss",Locale.CANADA)
                   val now = Date();
                   val fileName = "Profile "+binding.edtCandidateName.text+simpleDateFormat.format(now);
                   uploadImage(fileName,profileUri!!);
                   map.put(resources.getString(R.string.fir_profileImage),fileName);
               }
               else
               {
                   if(profileImage!=null)
                   {
                       map.put(resources.getString(R.string.fir_profileImage),profileImage!!);
                   }
               }
               if(imgSelectedList.size>0)
               {
                   val simpleDateFormat = SimpleDateFormat("yyyy_MM-dd_HH_mm_ss",Locale.CANADA)
                   val now = Date()
                   val newImageList = kotlin.collections.ArrayList<String>();
                   for(i in imgSelectedList.indices)
                   {

                       val fileName = "image$i"+binding.edtCandidateName.text+simpleDateFormat.format(now);
                        uploadImage(fileName,Uri.parse(imgSelectedList.get(i)));
                       newImageList.add(fileName)
                   }
                   map.put(resources.getString(R.string.fir_candidateImages),newImageList)
               }

               else if(candidateImagesListUrlNew.size>0)
               {
                   map.put(resources.getString(R.string.fir_candidateImages),candidateImagesListUrlNew)
               }

               if(map!=null)
               {
                   db.collection(resources.getString(R.string.fir_Users)).document(HomeActivity.email).set(map).addOnSuccessListener(object :OnSuccessListener<Void>
                   {
                       override fun onSuccess(p0: Void?) {
                           binding.progressbar.visibility = View.GONE;
                           updateLatLong(lat!!,long!!)
                       }

                   })
                       .addOnFailureListener()
                       {
                           binding.progressbar.visibility = View.GONE;
                           Toast.makeText(this,resources.getString(R.string.msg_server_error),Toast.LENGTH_SHORT).show();
                       }
               }

           }
        }
        else
        {
            binding.progressbar.visibility = View.GONE
            showNoNetworkMsg()
        }
    }
    private fun isValid():Boolean
    {
        var isValid = true;
        if(TextUtils.isEmpty(binding.edtCandidateName.text))
        {
            isValid = false;
            binding.edtCandidateName.setError(resources.getString(R.string.txt_enter_candidate_name))
            Toast.makeText(this,resources.getString(R.string.txt_enter_candidate_name),Toast.LENGTH_SHORT).show()
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtFatherName.text))
        {
            isValid = false;
            binding.edtFatherName.setError(resources.getString(R.string.txt_enter_father_name))
            Toast.makeText(this,resources.getString(R.string.txt_enter_father_name),Toast.LENGTH_SHORT).show()
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtMotherName.text))
        {
            isValid = false;
            binding.edtMotherName.setError(resources.getString(R.string.txt_enter_mother_name))
            Toast.makeText(this,resources.getString(R.string.txt_enter_mother_name),Toast.LENGTH_SHORT).show()
            return isValid
        }

        if(TextUtils.isEmpty(binding.edtContact.text) || binding.edtContact.text!!.length<10)
        {
            isValid = false;
            binding.edtContact.setError(resources.getString(R.string.txt_enter_contact_no))
            Toast.makeText(this,resources.getString(R.string.txt_enter_contact_no),Toast.LENGTH_SHORT).show()
            return isValid
        }

        if(TextUtils.isEmpty(binding.edtDob.text))
        {
            isValid = false;
            binding.edtDob.setError(resources.getString(R.string.text_enter_dob))
            Toast.makeText(this,resources.getString(R.string.text_enter_dob),Toast.LENGTH_SHORT).show()
            return isValid
        }
        if(binding.spinnerGender.selectedItem.toString().equals("Select Gender",ignoreCase = true))
        {
            isValid = false;
            Toast.makeText(this,"please select gender",Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(binding.spinnerMartialStatus.selectedItem.toString().equals("Select status"))
        {
            isValid = false;
            Toast.makeText(this,"Select status",Toast.LENGTH_SHORT).show()
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtLanguageKnown.text))
        {
            isValid = false;
            binding.edtLanguageKnown.setError("Enter "+resources.getString(R.string.txt_what_language_known))
            Toast.makeText(this,"Enter "+resources.getString(R.string.txt_what_language_known),Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtCandidateHeight.text))
        {
            isValid = false;
            binding.edtCandidateHeight.setError(resources.getString(R.string.txt_enter_candidate_height))
            Toast.makeText(this,resources.getString(R.string.txt_enter_candidate_height),Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtGusti.text))
        {
            isValid = false;
            binding.edtGusti.setError(resources.getString(R.string.text_enter_gusti))
            Toast.makeText(this,resources.getString(R.string.text_enter_gusti),Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtQualification.text))
        {
            isValid = false;
            binding.edtQualification.setError(resources.getString(R.string.txt_enter_qualification))
            Toast.makeText(this,resources.getString(R.string.txt_enter_qualification),Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtProfession.text))
        {
            isValid = false;
            binding.edtProfession.setError(resources.getString(R.string.txt_enter_profession))
            Toast.makeText(this,resources.getString(R.string.txt_enter_profession),Toast.LENGTH_SHORT).show();
            return isValid
        }
        if(TextUtils.isEmpty(binding.edtAddress.text))
        {
            isValid = false;
            binding.edtAddress.setError(resources.getString(R.string.txt_enter_your_address))
            Toast.makeText(this,resources.getString(R.string.txt_enter_your_address),Toast.LENGTH_SHORT).show();
            return isValid
        }

        if(TextUtils.isEmpty(binding.edtAboutMe.text))
        {
            isValid = false;
            binding.edtAboutMe.setError("Enter "+resources.getString(R.string.fir_aboutMe))
            Toast.makeText(this,"Enter "+resources.getString(R.string.fir_aboutMe),Toast.LENGTH_SHORT).show();
            return isValid
        }
       if(lat==null || long==null ||TextUtils.isEmpty(lat.toString())||TextUtils.isEmpty(long.toString()))
       {
           isValid = false;
           Toast.makeText(this,"Plz add your location",Toast.LENGTH_SHORT).show()
           return isValid
       }
       

        return isValid;
    }

    private fun uploadImage(fileName:String,uri:Uri)
    {
        if(isConnectionAvailable())
        {

            storageReference =  FirebaseStorage.getInstance().getReference("images/"+fileName);
           val imageSize = File(uri.path).length()


            var bitMap:Bitmap?=null;
            try {

                bitMap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            }
            catch (e:IOException)
            {
                e.printStackTrace()
            }
            var bitArray = ByteArrayOutputStream();
            if(bitMap!=null)
            {
                if(imageSize>300)
                {
                    bitMap.compress(Bitmap.CompressFormat.JPEG,20,bitArray)

                }
                else
                {
                    bitMap.compress(Bitmap.CompressFormat.JPEG,100,bitArray)
                }

                
            }

            var bi = bitArray.toByteArray();


            if(bi==null || bitMap==null)
            {
                return
            }

            storageReference.putBytes(bi).addOnSuccessListener(object :OnSuccessListener<UploadTask.TaskSnapshot>
            {
                override fun onSuccess(task: UploadTask.TaskSnapshot?) {
                    storageReference.downloadUrl.addOnSuccessListener(object :OnSuccessListener<Uri>
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

    override fun onCancelImage(pos: Int) {
        candidateImagesListUrlNew.removeAt(pos)
    }

}
