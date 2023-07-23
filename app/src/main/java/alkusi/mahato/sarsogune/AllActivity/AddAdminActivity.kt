package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterAddAdminList
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityAddAdminBinding
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog


class AddAminActivity : BaseActivity(),AdapterAddAdminList.OnUserClick {
    lateinit var toolbar: Toolbar;
    lateinit var binding:ActivityAddAdminBinding;
    lateinit var db:FirebaseFirestore;
     var storageReference: StorageReference?=null;
    var adapterAddAdminList:AdapterAddAdminList?=null;
    var dataList = ArrayList<QueryDocumentSnapshot>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdminBinding.inflate(layoutInflater);
        setContentView(binding.root)
        toolbar = findViewById(alkusi.mahato.sarsogune.R.id.toolbar);
        setSupportActionBar(toolbar)
        setBackEnable(true)
        setTitle("Add Admin")
        adapterAddAdminList = AdapterAddAdminList(this,dataList,storageReference,this)
        initRv();
        db = FirebaseFirestore.getInstance();
        init();
    }

    private fun init()
    {
        if(!isConnectionAvailable())
        {
            showNoNetworkMsg()
            return
        }
        binding.edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener
        {
            override fun onEditorAction(text: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if(p1== EditorInfo.IME_ACTION_SEARCH)
                {
                    if(text!=null && !TextUtils.isEmpty(text.text.toString()))
                    {
                        binding.progressbar.visibility = View.VISIBLE;
                        dataList.clear();
                        db.collection(resources.getString(alkusi.mahato.sarsogune.R.string.fir_Users)).whereEqualTo(resources.getString(R.string.fir_CandidateName),text.text.toString()).get()
                            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot>
                            {
                                override fun onComplete(task: Task<QuerySnapshot>) {
                                    if(task.isSuccessful)
                                    {
                                        for(items in task.result)
                                        {
                                            dataList.add(items)
                                        }

                                        if(dataList.size>0)
                                        {
                                            binding.txtEmpty.visibility= View.GONE
                                        }
                                        else
                                        {
                                            binding.txtEmpty.visibility= View.VISIBLE
                                        }
                                        adapterAddAdminList!!.notifyDataSetChanged();
                                    }
                                    else{
                                        binding.progressbar.visibility = View.GONE;
                                        binding.txtEmpty.visibility= View.VISIBLE
                                        adapterAddAdminList!!.notifyDataSetChanged()
                                    }

                                    binding.progressbar.visibility = View.GONE
                                }

                            })
                    }
                }

                hideKeyboard()
                return true

            }

        })
    }
    private fun initRv()
    {

                binding.recyclerView.setHasFixedSize(true);
               binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
         binding.recyclerView.adapter = adapterAddAdminList;
        adapterAddAdminList!!.notifyDataSetChanged();
    }

    override fun onClickUser(email: String) {
        val mDialog = MaterialDialog.Builder(this)
            .setTitle("Make admin?")
            .setMessage("Are you sure want to make admin this user?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes",
               R.drawable.icon_user,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(
                        dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface?,
                        which: Int
                    ) {
                        makeAdmin();
                        dialogInterface!!.dismiss()
                    }

                })
            .setNegativeButton(
                "Cancel",
                alkusi.mahato.sarsogune.R.drawable.icon_close,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(
                        dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface?,
                        which: Int
                    ) {
                        dialogInterface!!.dismiss()
                    }

                })
            .build()

        // Show Dialog

        // Show Dialog
        mDialog.show()
    }
    private fun makeAdmin() {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg()
            return;
        }
        val map: MutableMap<String, Any> = HashMap();
        map.put(resources.getString(R.string.fir_isAdmin), true)
        db.collection(resources.getString(R.string.fir_Users)).document(HomeActivity.email)
            .update(map).addOnCompleteListener {
                   if(it.isSuccessful)
                   {
                       Toast.makeText(this@AddAminActivity,"Success",Toast.LENGTH_SHORT).show()
                   }
                else
                   {
                       Toast.makeText(this@AddAminActivity,resources.getString(R.string.msg_something_went),Toast.LENGTH_SHORT).show()

                   }
        }

    }
}