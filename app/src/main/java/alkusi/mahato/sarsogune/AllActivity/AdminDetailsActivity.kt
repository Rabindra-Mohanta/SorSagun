package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityAdminDetailsBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class AdminDetailsActivity : BaseActivity() {
    lateinit var binding:ActivityAdminDetailsBinding;
    lateinit var toolbar: Toolbar;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailsBinding.inflate(layoutInflater);
        setContentView(binding.root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
    }
}