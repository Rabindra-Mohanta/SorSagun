package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterContactsUs
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityContactUsBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference

class ContactUsActivity : BaseActivity(),AdapterContactsUs.OnUserClick {
    lateinit var binding:ActivityContactUsBinding;
    lateinit var toolbar: Toolbar;
    var adapter:AdapterContactsUs?=null;
    var dataList = ArrayList<QueryDocumentSnapshot>()
    lateinit var db:FirebaseFirestore;
    var storageReference:StorageReference?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)
        setBackEnable(true)
        db = FirebaseFirestore.getInstance();
        setTitle(resources.getString(R.string.txt_contact_us))
        adapter = AdapterContactsUs(this,dataList,storageReference,this);
        init();
        init2()
        getAdminList();
        var isSuperAdmin = HomeActivity.myData!!.data!!.get(resources.getString(R.string.fir_isSuperAdmin))

        if(isSuperAdmin!=null && isSuperAdmin as Boolean)
        {
            binding.iconAdd.visibility = View.VISIBLE;
        }
        else
        {
            binding.iconAdd.visibility = View.GONE;
        }
    }

    private fun init()
    {

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.adapter = adapter;
        adapter!!.notifyDataSetChanged()

    }
    private fun init2()
    {
        binding.iconAdd.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(p0: View?) {
                  val intent = Intent(this@ContactUsActivity,AddAminActivity::class.java);
                startActivity(intent)
            }

        })
    }

    private fun getAdminList()
    {
        if(!isConnectionAvailable())
        {
            showNoNetworkMsg()
            return
        }
        binding.progressbar.visibility = View.VISIBLE;
        dataList.clear();
        db.collection(resources.getString(alkusi.mahato.sarsogune.R.string.fir_Users)).whereEqualTo(resources.getString(R.string.fir_isAdmin),true).get()
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
                       adapter!!.notifyDataSetChanged();
                    }
                    else{
                        binding.progressbar.visibility = View.GONE;
                        binding.txtEmpty.visibility= View.VISIBLE
                        adapter!!.notifyDataSetChanged()
                    }

                    binding.progressbar.visibility = View.GONE
                }

            })
    }

    override fun onClickUser(email: String) {
        val intent = Intent(this@ContactUsActivity,ActivityNotificationClick::class.java)
        intent.putExtra("email",email)
        intent.putExtra("isAdmin",true)
        intent.putExtra("isContactUs",true)
        startActivity(intent)

    }

}