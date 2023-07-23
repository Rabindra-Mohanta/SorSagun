package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterSearchUser
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivitySearchUserBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference

class SearchUserActivity : BaseActivity() {
    lateinit var binding:ActivitySearchUserBinding;
    lateinit var toolbar: Toolbar;
    var adapter:AdapterSearchUser?=null;
    var dataList = ArrayList<QueryDocumentSnapshot>()
    lateinit var firestore: FirebaseFirestore;
    var storageReference: StorageReference?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater);
        setContentView(binding.root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true);
        setTitle("Search Candidate")
        firestore = FirebaseFirestore.getInstance();
        init();
        init2();
    }
    private fun init()
    {
        binding.recyclerView.setHasFixedSize(true);
        adapter = AdapterSearchUser(this,dataList,storageReference)
        binding.recyclerView.adapter = adapter;
        binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter!!.notifyDataSetChanged();
    }
    private fun init2()
    {
       binding.edtSearch.setOnEditorActionListener(object :TextView.OnEditorActionListener
       {
           override fun onEditorAction(text: TextView?, p1: Int, p2: KeyEvent?): Boolean {
               if(p1== EditorInfo.IME_ACTION_SEARCH)
               {
                     if(text!=null && !TextUtils.isEmpty(text.text.toString()))
                     {
                         binding.progressbar.visibility = View.VISIBLE;
                         dataList.clear();
                         firestore.collection(resources.getString(R.string.fir_Users)).whereEqualTo(resources.getString(R.string.fir_CandidateName),text.text.toString()).get()
                             .addOnCompleteListener(object :OnCompleteListener<QuerySnapshot>
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
                                             binding.txtEmpty.visibility=View.GONE
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
               }

               hideKeyboard()
               return true

           }

       })
    }
}