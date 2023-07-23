package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivitySplashBinding
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout


class SplashActivity : BaseActivity() {
    lateinit var relativeLayout: RelativeLayout;
    lateinit var binding:ActivitySplashBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        relativeLayout = findViewById(R.id.topLl)
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
            val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                startActivity(intent)
                finish();
            }

        },3000)
    }


}