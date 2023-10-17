package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStep5Binding
import com.example.patientsportal.objects.Refresh


class BuyGoodDrugsStep5 : Fragment(R.layout.fragment_buy_good_drugs_step5) {

    private lateinit var binding: FragmentBuyGoodDrugsStep5Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStep5Binding.bind(view)

        val gif = R.raw.animated_check
        Glide.with(this)
            .asGif()
            .load(gif)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                    resource?.setLoopCount(1)

                    resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            Refresh.refresh(requireActivity(), requireContext())
                        }
                    })

                    return false
                }
            })
            .into(binding.animatedCheck)
    }

}