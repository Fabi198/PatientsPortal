package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

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
import com.example.patientsportal.databinding.FragmentAppointmentsStep5Binding
import com.example.patientsportal.objects.Refresh.refresh


class AppointmentsStep5 : Fragment(R.layout.fragment_appointments_step5) {

    private lateinit var binding: FragmentAppointmentsStep5Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsStep5Binding.bind(view)

        val appointmentCreatedOrDeleted = arguments?.getBoolean(getString(R.string.appointmentcreatedordeleted))

        if (appointmentCreatedOrDeleted != null) {
            val gif = if (appointmentCreatedOrDeleted) {
                R.raw.animated_check
            } else {
                R.raw.animated_error
            }
            binding.btnBack.text = if (appointmentCreatedOrDeleted) {
                getString(R.string.turno_confirmado)
            } else {
                getString(R.string.turno_eliminado)
            }
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
                                refresh(requireActivity(), requireContext())
                            }
                        })

                        return false
                    }
                })
                .into(binding.animatedCheck)
        }
    }
}