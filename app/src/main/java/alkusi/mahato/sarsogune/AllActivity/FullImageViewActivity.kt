package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityFullImageViewBinding
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide



class FullImageViewActivity : BaseActivity() {
    lateinit var binding:ActivityFullImageViewBinding;
    lateinit var toolbar: Toolbar;
    var isUri = false;
    var url:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnable(true)
        setTitle(resources.getString(R.string.txt_fullImage))
        if(intent.hasExtra("isUri"))
        {
            isUri = intent.getBooleanExtra("isUri",false)
        }

        if(intent.hasExtra("url"))
        {
            url = intent.getStringExtra("url").toString()
        }
        init();
    }
    private fun init()
    {

        if(isUri)
        {
            Glide.with(this@FullImageViewActivity).load(Uri.parse(url)).into(binding.imageZoomView)
        }
        else
        {
            Glide.with(this@FullImageViewActivity).load(url).into(binding.imageZoomView)

        }

    }
}