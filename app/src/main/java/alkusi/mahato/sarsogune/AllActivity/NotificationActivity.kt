package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.AllAdapter.AdapterNotification
import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityNotificationBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager

class NotificationActivity : BaseActivity() {
     lateinit var binding:ActivityNotificationBinding;
     lateinit var toolbar: Toolbar;
     var adapterNotification:AdapterNotification?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater);
        setContentView(binding.root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
        setTitle(resources.getString(R.string.txt_notification))
        init();
    }

    private fun init()
    {
       binding.recyclerView.setHasFixedSize(true)
        adapterNotification = AdapterNotification(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@NotificationActivity,LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.adapter = adapterNotification;
        adapterNotification!!.notifyDataSetChanged()
    }

}