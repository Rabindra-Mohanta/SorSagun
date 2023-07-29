package alkusi.mahato.sarsogune.AllFragments
import alkusi.mahato.sarsogune.AllActivity.FullImageViewActivity
import alkusi.mahato.sarsogune.AllActivity.HomeActivity
import alkusi.mahato.sarsogune.AllActivity.MapsActivity
import alkusi.mahato.sarsogune.AllAdapter.AdapterHome
import alkusi.mahato.sarsogune.AllAdapter.AdapterImageList
import alkusi.mahato.sarsogune.NotificationByFcm.Api.ApiUtilities
import alkusi.mahato.sarsogune.NotificationByFcm.Model.NotificationData
import alkusi.mahato.sarsogune.NotificationByFcm.Model.PushNotification
import android.os.Bundle
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.FragmentHomeBinding
import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.Calendar
import java.util.Collections
class HomeFragment : BaseFragment(),AdapterHome.OnItemClick,AdapterImageList.OnClickListener{
  lateinit var binding:FragmentHomeBinding;
    var adapterHome:AdapterHome?=null;
    lateinit var db:FirebaseFirestore;
     var storageReference: StorageReference?=null;
     val TAG = "HomeFragment"
    var switchMaleBo = false;
    var switchFemaleBo = false;
    var switchAllBo = true;
    var querySnapshot1:QuerySnapshot?=null;
    lateinit var layoutManager: LinearLayoutManager;
    var profileImage = ""
    var allImages = ArrayList<String>();
    var bottomSheetDialog:BottomSheetDialog? = null;
    val dataList:ArrayList<DocumentSnapshot> = ArrayList<DocumentSnapshot>()
    var lastVisible:DocumentSnapshot?=null;
    var startPagination = 1;
    var previousScrollPosition = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  setHasOptionsMenu(true)
    }
    private fun getDataFromFirebase()
    {
        Log.e(TAG,"rabindra->"+true)
        binding.progressbar.visibility = View.VISIBLE;
        db.collection(resources.getString(R.string.fir_Users)).orderBy("TimeStamp").get().addOnCompleteListener(object :OnCompleteListener<QuerySnapshot>
        {
            override fun onComplete(querySnapshot: Task<QuerySnapshot>) {

                   val result =  querySnapshot.result
                dataList.clear()
                adapterHome!!.dataList.clear()
                 if(result!=null && result.documents.size>0)
                 {

                     val documentList = ArrayList<DocumentSnapshot>();
                     for(i in result.documents.size-1 downTo 0)
                     {


                             var data = result.documents.get(i)


                             if(data.get(resources.getString(R.string.fir_CandidateName)) != null )
                             {

                                 documentList.add(result.documents.get(i))
                             }

                     }
                     adapterHome!!.addData(documentList)
                     binding.progressbar.visibility = View.GONE;
                     binding.swipeRefreshLayout.isRefreshing = false;

                 }
                else
                 {
                     binding.progressbar.visibility = View.GONE;
                 }


            }

        })
            .addOnFailureListener(object :OnFailureListener
            {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(requireContext(),resources.getString(R.string.msg_something_went),Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE;
                    binding.swipeRefreshLayout.isRefreshing = false;
                }
            })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.swipeRefreshLayout.isRefreshing = true;
        initRv()
        init();
        init2();
        return binding.root;
    }
    private fun init2()
    {
        binding.swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener
        {
            override fun onRefresh() {
                startPagination =1;
               lastVisible = null
                dataList.clear()
                getDataFromFirebase();
            }

        })
    }
private fun init()
{
//    binding.recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener()
//    {
//
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//
//        }
//
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val totalItem = layoutManager!!.itemCount;
//            val visibleItem = layoutManager!!.childCount
//            val firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
//            val lastVisible = layoutManager!!.findLastVisibleItemPosition()
//
//
//
//            if((firstVisibleItem+visibleItem)>=totalItem && dy > 0 && previousScrollPosition<=binding.recyclerView.computeVerticalScrollOffset())
//            {
//                if(startPagination*10 >= totalItem )
//                {
//                    if(!binding.progressbar.isVisible)
//                    {
//                        startPagination++;
//                        getDataFromFirebase();
//                    }
//                }
//
//
//            }
//            previousScrollPosition = recyclerView.computeVerticalScrollOffset();
//        }
//
//    })

    db = FirebaseFirestore.getInstance();
    getDataFromFirebase();
}
    private fun initRv()
    {


        binding.recyclerView.setHasFixedSize(true);
        adapterHome = AdapterHome(requireContext(),dataList,storageReference,this)
        layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapterHome;
        adapterHome!!.notifyDataSetChanged();
        binding.progressbar.visibility = View.GONE;

    }

    override fun onItemClick(email: String) {
        if(isConnectionAvailable())
        {
            binding.progressbar.visibility = View.VISIBLE;
            db.collection(resources.getString(R.string.fir_Users)).document(email).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>
            {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                   if(task.isSuccessful)
                   {
                       if(task!=null&& task.result!=null)
                       {

                           showBottomDialog(task.result)
                       }

                       binding.progressbar.visibility = View.GONE
                   }
                    else
                   {
                       Toast.makeText(requireContext(),resources.getString(R.string.msg_something_went),Toast.LENGTH_SHORT).show()
                   }
                }

            })


        }
        else
        {
            binding.progressbar.visibility = View.GONE
        }

   }


private fun showBottomDialog(documentSnapshot: DocumentSnapshot)
    {
        if(bottomSheetDialog!=null && bottomSheetDialog!!.isShowing) return
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_details,null);
        val edtName = dialogView.findViewById<TextView>(R.id.edtName);
        val edtFatherName = dialogView.findViewById<TextView>(R.id.edtFatherName);
        val edtMotherName = dialogView.findViewById<TextView>(R.id.edtMotherName);
        val edtContactNo = dialogView.findViewById<TextView>(R.id.edtContactNo);
        val edtDob = dialogView.findViewById<TextView>(R.id.edtDob);
        val edtGender = dialogView.findViewById<TextView>(R.id.edtGender);
        val edtMaritalStatus = dialogView.findViewById<TextView>(R.id.edtMaritalStatus);
        val edtLanguage = dialogView.findViewById<TextView>(R.id.edtLanguage);
        val edtHeight = dialogView.findViewById<TextView>(R.id.edtHeight);
        val edtGusti = dialogView.findViewById<TextView>(R.id.edtGusti);
        val edtQualification = dialogView.findViewById<TextView>(R.id.edtQualification);
        val edtProfession = dialogView.findViewById<TextView>(R.id.edtProfession);
        val edtAddress = dialogView.findViewById<TextView>(R.id.edtAddress);
       val imageRv = dialogView.findViewById<RecyclerView>(R.id.imageRv);
        val kudmaliMerrageCheckBOx = dialogView.findViewById<CheckBox>(R.id.kudmaliMerrageCheckBOx)
        val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
        val btnProposal = dialogView.findViewById<Button>(R.id.btnProposal)
        val contactNoll = dialogView.findViewById<LinearLayout>(R.id.contactNoll)
        val edtAboutMe = dialogView.findViewById<TextView>(R.id.edtAboutMe)
        val edtConsumeAlcohol = dialogView.findViewById<TextView>(R.id.edtConsumeAlcohol)
        val edtSmoke = dialogView.findViewById<TextView>(R.id.edtSmoke)
        val imgDeleteAccount = dialogView.findViewById<ImageView>(R.id.imgDeleteAccount)
        val btnView = dialogView.findViewById<Button>(R.id.btnView)
        val imgReport = dialogView.findViewById<ImageView>(R.id.imgReport)
       var lat:Double?=null;
        var long:Double?=null;
        if(documentSnapshot.getDouble(resources.getString(R.string.fir_latitude)) !=null)
        {
            lat = documentSnapshot.getDouble(resources.getString(R.string.fir_latitude))
        }

        if(documentSnapshot.getDouble(resources.getString(R.string.fir_longitude))!=null)
        {
            long = documentSnapshot.getDouble(resources.getString(R.string.fir_longitude))
        }

        btnView.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val intent = Intent(requireContext(), MapsActivity::class.java)
                intent.putExtra("isViewLocation",true);
                intent.putExtra("latitude",lat)
                intent.putExtra("longitude",long)
                startActivity(intent)
            }

        })

        if(HomeActivity.isAdmin|| HomeActivity.isSuperAdamin)
        {
           contactNoll.visibility = View.VISIBLE
            imgDeleteAccount.visibility = View.VISIBLE
        }
        else
        {
           contactNoll.visibility = View.GONE
            imgDeleteAccount.visibility = View.GONE
        }

        edtName.setText(documentSnapshot.get(resources.getString(R.string.fir_CandidateName)).toString())
        edtSmoke.setText(documentSnapshot.get(resources.getString(R.string.fir_smoke)).toString())
        edtConsumeAlcohol.setText(documentSnapshot.get(resources.getString(R.string.fir_consume_alcohol)).toString())
        edtAboutMe.setText(documentSnapshot.get(resources.getString(R.string.fir_aboutMe)).toString())
        edtFatherName.setText(documentSnapshot.get(resources.getString(R.string.fir_FatherName)).toString())
        edtMotherName.setText(documentSnapshot.get(resources.getString(R.string.fir_MotherName)).toString())
        edtContactNo.setText(documentSnapshot.get(resources.getString(R.string.fir_ContactNo)).toString())
        edtDob.setText(documentSnapshot.get(resources.getString(R.string.fir_DOB)).toString())
        edtGender.setText(documentSnapshot.get(resources.getString(R.string.fir_Gender)).toString())
        edtMaritalStatus.setText(documentSnapshot.get(resources.getString(R.string.fir_MartialStatus)).toString())
        edtLanguage.setText(documentSnapshot.get(resources.getString(R.string.fir_LanguageKnown)).toString())
        edtHeight.setText(documentSnapshot.get(resources.getString(R.string.fir_CandidateHeight)).toString())
        edtGusti.setText(documentSnapshot.get(resources.getString(R.string.fir_Gusti)).toString())
        edtQualification.setText(documentSnapshot.get(resources.getString(R.string.fir_Qualification)).toString())
        edtProfession.setText(documentSnapshot.get(resources.getString(R.string.fir_Profession)).toString())
        edtAddress.setText(documentSnapshot.get(resources.getString(R.string.fir_Address)).toString())


        imgDeleteAccount.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(v: View?) {

                showAlertDialog(documentSnapshot)
            }

        })
        if(documentSnapshot.get(resources.getString(R.string.txt_Kudmali_marraige))==null)
        {
            if(HomeActivity.isAdmin||HomeActivity.isSuperAdamin)
            {
                Toast.makeText(requireContext(),"Profile data not available",Toast.LENGTH_SHORT).show()
                showAlertDialog(documentSnapshot)
            }
            else
            {
                Toast.makeText(requireContext(),"Profile data not available",Toast.LENGTH_SHORT).show()

            }
            return
        }
        kudmaliMerrageCheckBOx.isChecked = documentSnapshot.get(resources.getString(R.string.txt_Kudmali_marraige)) as Boolean



            db.collection(resources.getString(R.string.fir_Users)).document(documentSnapshot.id).collection(resources.getString(R.string.fir_requested)).document(HomeActivity.email).get().addOnCompleteListener(object :OnCompleteListener<DocumentSnapshot>
            {
                override fun onComplete(task: Task<DocumentSnapshot>) {

                    if(task.isSuccessful)
                    {

                        if(task.result.data!=null)
                        {
                            btnProposal.setText(resources.getString(R.string.txt_requested))
                        }
                        else
                        {
                            btnProposal.setText(resources.getString(R.string.txt_send_proposal))
                        }

                    }
                    else
                    {

                    }

                }

            })


        btnProposal.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                if(btnProposal.text.toString().equals(resources.getString(R.string.txt_send_proposal),ignoreCase = true))
                {
                    btnProposal.setText(resources.getString(R.string.txt_requested))
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


                Glide.with(requireContext()).load(uri).into(imageView)
                imageView.setOnClickListener(object :View.OnClickListener
                {
                    override fun onClick(p0: View?) {
                        val intent = Intent(context, FullImageViewActivity::class.java)
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
            val adapterSelectedImage = AdapterImageList(requireContext(),candidateImagesListUrl,false,this,false);
            imageRv.setHasFixedSize(true);
            imageRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false);
            imageRv.adapter = adapterSelectedImage;



            for(i in allImageList.indices)
            {
                storageReference =  FirebaseStorage.getInstance().getReference("images/"+allImageList.get(i));
                storageReference!!.downloadUrl.addOnSuccessListener {uri->

                    candidateImagesListUrl.add(uri.toString())
                    adapterSelectedImage!!.notifyDataSetChanged();

                }
            }

        }

        imgReport.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                showReportDialog();
            }

        })

         bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        bottomSheetDialog!!.setContentView(dialogView)
        bottomSheetDialog!!.show()
     }

    override fun onCancelImage(pos: Int) {

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
        db.collection(resources.getString(R.string.fir_Users)).document(documentSnapshot.id).collection(resources.getString(R.string.fir_requested)).document(HomeActivity.email).set(map).addOnCompleteListener(object :OnCompleteListener<Void>
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
private fun showAlertDialog(documentSnapshot: DocumentSnapshot?)
{
    val alertDialog = android.app.AlertDialog.Builder(requireContext());
    alertDialog.setCancelable(true);
    alertDialog.setTitle("Account delete");
    alertDialog.setMessage("Are you sure want to delete")
    alertDialog.setPositiveButton(android.R.string.ok,object : DialogInterface.OnClickListener
    {
        override fun onClick(p0: DialogInterface?, p1: Int) {

            p0!!.dismiss()
            deleteAccount(documentSnapshot)
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
    private fun showReportDialog()
    {
        val alertDialog = android.app.AlertDialog.Builder(requireContext());
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Report Account");
        alertDialog.setMessage("Are you sure want to report this user!")
        alertDialog.setPositiveButton(android.R.string.yes,object : DialogInterface.OnClickListener
        {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                     Toast.makeText(requireContext(),"Reported success",Toast.LENGTH_SHORT).show()
                p0!!.dismiss()
                
            }

        })
        alertDialog.setNegativeButton(android.R.string.no,object : DialogInterface.OnClickListener
        {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()

            }

        })
        alertDialog.show();
    }
    private fun deleteAccount(documentSnapshot: DocumentSnapshot?)
    {
              if(documentSnapshot==null) return
        deleteFromFireStore(documentSnapshot.id)
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
            desertRef.delete()

        }
        if(allImages.size>0)
        {
            for(i in allImages.indices)
            {
                val desertRef = storageRef.child("images/$allImages.get(i)")
                desertRef.delete()
                if(i==allImages.size-1)
                {
                    bottomSheetDialog!!.dismiss()
                    startPagination =1;
                    lastVisible = null;
                    getDataFromFirebase()
                }
            }
        }
        else
        {
            Toast.makeText(requireContext(),"Account deleted",Toast.LENGTH_SHORT).show()
            if(bottomSheetDialog!=null && bottomSheetDialog!!.isShowing)
            {
                bottomSheetDialog!!.dismiss()
                lastVisible = null;
                startPagination =1;
                getDataFromFirebase()

            }
            else
            {
                lastVisible = null;
                startPagination =1;
                getDataFromFirebase()
            }

        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_filter).isVisible=true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_filter ->
            {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showFilterDialog()
    {
            val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.filter_dialog)
        val switchMale = dialog.findViewById<Switch>(R.id.switchMale)
        val switchFemale = dialog.findViewById<Switch>(R.id.switchFemale)
        val switchAll = dialog.findViewById<Switch>(R.id.switchAll)

        switchMale.isChecked = switchMaleBo;
        switchFemale.isChecked = switchFemaleBo;
        switchAll.isChecked = switchAllBo;

        switchAll.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
              if(p1)
              {
                  switchMaleBo = false;
                  switchFemaleBo = false;
                  switchAllBo = true;
                  if(adapterHome!=null)
                  {
                      adapterHome!!.dataList.clear()
                      adapterHome!!.notifyDataSetChanged()
                  }
                  getDataFromFirebase();
                  dialog.dismiss()
              }
            }

        })
        switchMale.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1)
                {
                    switchMaleBo = true;
                    switchFemaleBo = false;
                    switchAllBo = false;
                    if(adapterHome!=null)
                    {
                        adapterHome!!.dataList.clear()
                        adapterHome!!.notifyDataSetChanged()
                    }

                    getDataFromFirebase("Male")
                    dialog.dismiss()
                }
            }

        })

        switchFemale.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1)
                {
                    switchMaleBo = false;
                    switchFemaleBo = true;
                    switchAllBo = false;
                    if(adapterHome!=null)
                    {
                        adapterHome!!.dataList.clear()
                        adapterHome!!.notifyDataSetChanged()
                    }
                    getDataFromFirebase("Female")
                    dialog.dismiss()
                }
            }

        })
        dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
    private fun getDataFromFirebase(type:String)
    {
        Log.e(TAG,"rabindra->"+true)
        binding.progressbar.visibility = View.VISIBLE;
        db.collection(resources.getString(R.string.fir_Users)).orderBy("TimeStamp").whereEqualTo(resources.getString(R.string.fir_Gender),type).get().addOnCompleteListener(object :OnCompleteListener<QuerySnapshot>
        {
            override fun onComplete(querySnapshot: Task<QuerySnapshot>) {

                val result =  querySnapshot.result
                dataList.clear()
                adapterHome!!.dataList.clear()
                if(result!=null && result.documents.size>0)
                {

                    val documentList = ArrayList<DocumentSnapshot>();
                    for(i in result.documents.size-1 downTo 0)
                    {


                        var data = result.documents.get(i)


                        if(data.get(resources.getString(R.string.fir_CandidateName)) != null )
                        {

                            documentList.add(result.documents.get(i))
                        }

                    }
                    adapterHome!!.addData(documentList)
                    binding.progressbar.visibility = View.GONE;
                    binding.swipeRefreshLayout.isRefreshing = false;
                    binding.txtEmpty.visibility = View.GONE
                }
                else
                {
                    binding.txtEmpty.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE;
                }


            }

        })
            .addOnFailureListener(object :OnFailureListener
            {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(requireContext(),resources.getString(R.string.msg_something_went),Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE;
                    binding.swipeRefreshLayout.isRefreshing = false;
                }
            })
    }
}