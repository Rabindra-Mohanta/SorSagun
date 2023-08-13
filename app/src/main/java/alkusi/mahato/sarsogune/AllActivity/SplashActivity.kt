package alkusi.mahato.sarsogune.AllActivity


import alkusi.mahato.sarsogune.databinding.ActivitySplashBinding
import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.RelativeLayout


class SplashActivity : BaseActivity() {
    lateinit var relativeLayout: RelativeLayout;
    lateinit var binding:ActivitySplashBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + alkusi.mahato.sarsogune.R.raw.intro_video)

        binding.videoView.setVideoURI(uri)
        binding.videoView.start()
        if(intent!=null && intent.extras!=null)
        {
            if(intent.hasExtra("email"))
            {
                val intent = Intent(this,ActivityNotificationClick::class.java)
                intent.putExtra("email",intent.extras!!.getString("email"))
                startActivity(intent)
                finish()
            }
            else
            {
                binding.videoView.stopPlayback()
                val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                startActivity(intent)
                finish();
            }
        }
        init()
    }

    private fun init()
    {



        val handler = Handler().postDelayed(object :Runnable
        {
            override fun run() {
                binding.videoView.stopPlayback()
            val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                startActivity(intent)
                finish();
            }

        },3000)
    }


}