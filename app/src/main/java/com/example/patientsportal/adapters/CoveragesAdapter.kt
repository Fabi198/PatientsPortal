package com.example.patientsportal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemCoverageBinding
import com.example.patientsportal.entities.dbEntities.PatientCoveragePlan


class CoveragesAdapter(private val listCoverages: ArrayList<PatientCoveragePlan>, private val context: Context, private var positionSelected: Int, private val onClick: (PatientCoveragePlan, Int, String) -> Unit): RecyclerView.Adapter<CoveragesAdapter.CoveragesViewHolder>() {

    fun updateData(newPositionSelected: Int) {
        positionSelected = newPositionSelected
    }


    inner class CoveragesViewHolder(v: View): RecyclerView.ViewHolder(v), GestureDetector.OnGestureListener {

        private val binding = ItemCoverageBinding.bind(v)
        private lateinit var gestureDetector: GestureDetector
        private var isSliderOpen = false
        private val swipeThreshold = 100

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(pcp: PatientCoveragePlan, size: Int, context: Context, positionSelected: Int, onClick: (PatientCoveragePlan, Int, String) -> Unit) {
            if (size == 1) { binding.ivCheck.setImageResource(R.drawable.baseline_check_24) } else {
                if (positionSelected == bindingAdapterPosition) {
                    binding.ivCheck.setImageResource(R.drawable.baseline_check_24)
                } else {
                    binding.ivCheck.setImageResource(R.drawable.baseline_health_and_safety_24)
                }
            }
            binding.tvCoverageName.text = pcp.planType.coverage.name
            binding.tvCoveragePlanType.text = context.getString(R.string.plan, pcp.planType.planType)
            binding.tvCoverageAssociateNumber.text = context.getString(R.string.nro_de_socio, pcp.affiliateNumber)

            gestureDetector = GestureDetector(context, this)

            binding.clMain.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                v.performClick()
                true
            }

            binding.btnCoverageEdit.setOnClickListener { onClick(pcp, bindingAdapterPosition, context.getString(R.string.edit)) }
            binding.btnCoverageDelete.setOnClickListener { onClick(pcp, bindingAdapterPosition, context.getString(R.string.delete)) }
            binding.clMain.setOnClickListener { onClick(pcp, bindingAdapterPosition, context.getString(R.string.select)) }
        }

        override fun onDown(e: MotionEvent): Boolean { return true }
        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean { return false }
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val deltaX = e2.x - e1!!.x

            if (!isSliderOpen) {
                if (deltaX < -swipeThreshold) {
                    // Abre la vista deslizando hacia la izquierda
                    binding.clMain.animate()
                        .translationX(-(binding.btnCoverageEdit.width.toFloat()+binding.btnCoverageDelete.width.toFloat()))
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()

                    binding.btnCoverageEdit.animate()
                        .translationX(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withStartAction { binding.btnCoverageEdit.isVisible = true }
                        .start()

                    binding.btnCoverageDelete.animate()
                        .translationX(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withStartAction { binding.btnCoverageDelete.isVisible = true }
                        .start()
                    isSliderOpen = true
                }
            } else {
                if (deltaX > swipeThreshold) {
                    // Cierra la vista deslizando hacia la derecha
                    binding.clMain.animate()
                        .translationX(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()

                    binding.btnCoverageEdit.animate()
                        .translationX(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withEndAction { binding.btnCoverageEdit.isVisible = false }
                        .start()

                    binding.btnCoverageDelete.animate()
                        .translationX(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withEndAction { binding.btnCoverageDelete.isVisible = false }
                        .start()

                    isSliderOpen = false
                }
            }
            return true
        }
        override fun onLongPress(e: MotionEvent) {}
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean { return false }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoveragesViewHolder {
        return CoveragesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_coverage, parent, false))
    }

    override fun getItemCount(): Int {
        return listCoverages.size
    }

    override fun onBindViewHolder(holder: CoveragesViewHolder, position: Int) {
        holder.bind(listCoverages[position], listCoverages.size, context, positionSelected, onClick)
    }


}