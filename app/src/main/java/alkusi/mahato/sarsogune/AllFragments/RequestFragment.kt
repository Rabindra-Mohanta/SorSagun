package alkusi.mahato.sarsogune.AllFragments
import alkusi.mahato.sarsogune.AllActivity.HomeActivity
import alkusi.mahato.sarsogune.AllAdapter.AdapterRequested
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.FragmentRequestBinding
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
class RequestFragment : BaseFragment() {
lateinit var binding:FragmentRequestBinding;
var adapter:AdapterRequested?=null;
    val TAG = "RequestFragment"
    val requestedList = ArrayList<DocumentSnapshot>();
    lateinit var firestore: FirebaseFirestore
    var storageReference:StorageReference?=null;
    var lastVisible:DocumentSnapshot?=null;
    lateinit var layoutManager: LinearLayoutManager;
    var startPagination = 1;
    var previousScrollPosition = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRequestBinding.inflate(layoutInflater)
        binding.swipeRefreshLayout.isRefreshing = true
        init();
        init2();
        getAllRequest()
        return binding.root;
    }
    private fun init2()
    {
         binding.swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener
         {
             override fun onRefresh() {
                  startPagination = 1;
                 previousScrollPosition = 0;
                 lastVisible = null;
                 getAllRequest()

             }

         })
    }
private fun getAllRequest()
{

    binding.progressbar.visibility = View.VISIBLE;
    firestore.collection(resources.getString(R.string.fir_Users)).document(HomeActivity.email).collection(resources.getString(R.string.fir_requested)).orderBy("TimeStamp").startAfter(lastVisible).limit(10).get().addOnCompleteListener(object :OnCompleteListener<QuerySnapshot>
    {
      override fun onComplete(task: Task<QuerySnapshot>) {
           if(task.isSuccessful)
           {

                    if(task==null || task.result==null)
                    {
                        binding.txtEmpty.visibility = View.GONE;
                        binding.swipeRefreshLayout.isRefreshing = false;
                        return
                    }

               if(lastVisible==null)
               {
                   requestedList.clear()
               }

             val dataList = ArrayList<DocumentSnapshot>();
               for(i in task.result.documents.size-1 downTo  0)
               {
                   dataList.add(task.result.documents.get(i))
                   if(i==task.result.documents.size-1)
                   {
                       lastVisible = task.result.documents.get(i)
                   }
               }
               requestedList.addAll(dataList)
               adapter!!.notifyDataSetChanged();


                if(requestedList.size==0 && lastVisible == null)
                {
                    startPagination = 1;
                    previousScrollPosition = 0;
                    binding.txtEmpty.visibility = View.VISIBLE;

                }
                else
                {
                    binding.txtEmpty.visibility = View.GONE;
                }

                binding.progressbar.visibility = View.GONE;
           }
            else
           {

           }
            binding.swipeRefreshLayout.isRefreshing = false;
        }
    })
}
private fun init()
{
    firestore = FirebaseFirestore.getInstance();
    adapter = AdapterRequested(requireContext(),requestedList,storageReference);
    binding.recyclerView.setHasFixedSize(true);
    layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
    binding.recyclerView.layoutManager = layoutManager
    binding.recyclerView.adapter = adapter;
    adapter!!.notifyDataSetChanged();




    binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
    {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalItem = layoutManager!!.itemCount;
            val visibleItem = layoutManager!!.childCount
            val firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()

            if((firstVisibleItem+visibleItem)>=totalItem && dy > 0 && previousScrollPosition<=binding.recyclerView.computeVerticalScrollOffset())

            {

                if(startPagination*10 >= totalItem )
                {
                    if(!binding.progressbar.isVisible)
                    {
                        startPagination++;
                        getAllRequest()
                    }
                }

            }

        }

    })
}
}