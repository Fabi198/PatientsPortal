package com.example.patientsportal.fragmentsDrawerMenu

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.ViewPagerAdapter
import com.example.patientsportal.databinding.CustomTabLayoutBinding
import com.example.patientsportal.databinding.FragmentTwoPagesBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.fragments.NewMedicalTestFragment
import com.example.patientsportal.fragments.ReNewExpiredPrescriptionsFragment
import com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps.BuyGoodDrugsPreStep
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class TwoPagesFragment : Fragment(R.layout.fragment_two_pages) {

    private lateinit var binding: FragmentTwoPagesBinding
    private var initialHeight = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTwoPagesBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val title = arguments?.getString(getString(R.string.title_tag))
        val tabTitles = arguments?.getStringArray(getString(R.string.tabtitles_tag))
        val task = arguments?.getString(getString(R.string.task_tag))

        // Esperar a que la vista esté completamente inflada y lista
        binding.btnReNewExpiredPrescriptions.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Una vez que la vista está lista, obtenemos la altura inicial
                initialHeight = binding.btnReNewExpiredPrescriptions.height

                // Luego, eliminamos el oyente para que no se vuelva a llamar
                binding.btnReNewExpiredPrescriptions.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        setupTabLayout(idContainer, tabTitles, title, task)
    }

    @SuppressLint("InflateParams")
    private fun setupTabLayout(idContainer: Int?, tabTitles: Array<String>?, title: String?, task: String?) {
        if (idContainer != null && tabTitles != null && title != null) {
            if (title == getString(R.string.see_medical_test_title)) {
                binding.addPatientTest.visibility = View.VISIBLE
                binding.addPatientTest.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
                    override fun onMenuExpanded() { binding.modalBackground.visibility = View.VISIBLE }
                    override fun onMenuCollapsed() { binding.modalBackground.visibility = View.GONE }
                })
                binding.loadNewMedicalTest.setOnClickListener {
                    showFragmentFromFragment(requireActivity(), NewMedicalTestFragment(), getString(R.string.newmedicaltestfragment_tag), requireContext(), idContainer, title = getString(R.string.subir_nuevo_estudio))
                    binding.addPatientTest.collapse()
                }
            }
            if (title == getString(R.string.medicamentos) && tabTitles[0] == getString(R.string.vigentes)) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val dbPatientsPortal = DbPatientsPortal(requireContext())
                    val listSize = dbPatientsPortal.readAllPrescriptionsByPatient(getPatient(requireActivity(), requireContext()).idPatient, getString(R.string.current), "").size
                    withContext(Dispatchers.Main) {
                        if (listSize > 0) {
                            binding.myOrders.colorNormal = resources.getColor(R.color.blue, null)
                            binding.myOrders.setOnClickListener {
                                showFragmentFromFragment(requireActivity(), TwoPagesFragment(), "MyPrescriptionsOrdersFragment", requireContext(), idContainer, tabTitles = arrayOf(getString(R.string.activos), getString(R.string.historial)), title = getString(R.string.mis_pedidos))
                                binding.openCurrentPrescriptionsMenu.collapse()
                            }
                            binding.buyGoodDrugs.colorNormal = resources.getColor(R.color.blue, null)
                            binding.buyGoodDrugs.setOnClickListener {
                                showFragmentFromFragment(requireActivity(), BuyGoodDrugsPreStep(), "BuyGoodDrugsPreStep", requireContext(), idContainer)
                                binding.openCurrentPrescriptionsMenu.collapse()
                            }
                            binding.loadPrescription.colorNormal = resources.getColor(R.color.blue, null)
                            binding.loadPrescription.setOnClickListener {
                                showFragmentFromFragment(requireActivity(), ReNewExpiredPrescriptionsFragment(), "ReNewExpiredPrescriptionsFragment", requireContext(), idContainer, title = getString(R.string.current), task = getString(R.string.generar_recetas))
                                binding.openCurrentPrescriptionsMenu.collapse()
                            }
                            binding.renewPrescriptions.colorNormal = resources.getColor(R.color.blue, null)
                            binding.renewPrescriptions.setOnClickListener {
                                showFragmentFromFragment(requireActivity(), ReNewExpiredPrescriptionsFragment(), "ReNewExpiredPrescriptionsFragment", requireContext(), idContainer, title = getString(R.string.current))
                                binding.openCurrentPrescriptionsMenu.collapse()
                            }
                        } else {
                            binding.myOrders.colorNormal = resources.getColor(R.color.blueDisable, null)
                            binding.myOrders.setOnClickListener { binding.openCurrentPrescriptionsMenu.collapse() }
                            binding.buyGoodDrugs.colorNormal = resources.getColor(R.color.blueDisable, null)
                            binding.buyGoodDrugs.setOnClickListener { binding.openCurrentPrescriptionsMenu.collapse() }
                            binding.loadPrescription.colorNormal = resources.getColor(R.color.blueDisable, null)
                            binding.loadPrescription.setOnClickListener { binding.openCurrentPrescriptionsMenu.collapse() }
                            binding.renewPrescriptions.colorNormal = resources.getColor(R.color.blueDisable, null)
                            binding.renewPrescriptions.setOnClickListener { binding.openCurrentPrescriptionsMenu.collapse() }
                        }
                        binding.openCurrentPrescriptionsMenu.visibility = View.VISIBLE
                        binding.openCurrentPrescriptionsMenu.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
                            override fun onMenuExpanded() { binding.modalBackground.visibility = View.VISIBLE }
                            override fun onMenuCollapsed() { binding.modalBackground.visibility = View.GONE }
                        })
                    }
                }
            }
            if (title == getString(R.string.medicamentos) && tabTitles[1] == getString(R.string.vencidos)) {
                binding.btnReNewExpiredPrescriptions.setOnClickListener {
                    showFragmentFromFragment(requireActivity(), ReNewExpiredPrescriptionsFragment(), "ReNewExpiredPrescriptionsFragment", requireContext(), idContainer, title = getString(R.string.expired))
                }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val viewPagerAdapter = ViewPagerAdapter(requireActivity(), requireContext(), idContainer, title, tabTitles, task)
                withContext(Dispatchers.Main) {
                    binding.viewPager.adapter = viewPagerAdapter
                    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                        val bindingTab = CustomTabLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                        // Establecer el texto en la pestaña y en los CardView
                        when (position) {
                            0 -> {
                                bindingTab.tvCustomTabLayout.text = tabTitles[position]
                                bindingTab.tvCustomTabLayout.setTextColor(resources.getColor(R.color.blue))
                                bindingTab.cvCustomTabLayout.setCardBackgroundColor(resources.getColor(R.color.white))
                            }
                            else -> {
                                bindingTab.tvCustomTabLayout.text = tabTitles[position]
                                bindingTab.tvCustomTabLayout.setTextColor(resources.getColor(R.color.white))
                                bindingTab.cvCustomTabLayout.setCardBackgroundColor(resources.getColor(R.color.blue))
                            }
                        }

                        tab.customView = bindingTab.root
                    }.attach()

                    binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {
                            val customTabView = tab?.customView
                            val tabCard: MaterialCardView? = customTabView?.findViewById(R.id.cvCustomTabLayout)
                            val tabText: TextView? = customTabView?.findViewById(R.id.tvCustomTabLayout)
                            tabCard?.setCardBackgroundColor(resources.getColor(R.color.white))
                            tabText?.setTextColor(resources.getColor(R.color.blue))
                            if (tabText?.text == getString(R.string.vigentes)) {
                                initialHeight = binding.btnReNewExpiredPrescriptions.height
                                val animClose = ValueAnimator.ofInt(binding.btnReNewExpiredPrescriptions.width, binding.btnReNewExpiredPrescriptions.width - 320)
                                animClose.addUpdateListener { valueAnimator ->
                                    val value = valueAnimator.animatedValue as Int
                                    binding.btnReNewExpiredPrescriptions.layoutParams.width = value
                                    binding.btnReNewExpiredPrescriptions.layoutParams.height = initialHeight
                                    binding.btnReNewExpiredPrescriptions.requestLayout()
                                }
                                binding.tvReNewPrescriptions.text = null
                                animClose.duration = 900
                                animClose.start()
                                Handler().postDelayed({
                                    binding.btnReNewExpiredPrescriptions.visibility = View.GONE
                                    binding.openCurrentPrescriptionsMenu.visibility = View.VISIBLE
                                }, 605)

                            }
                            if (tabText?.text == getString(R.string.vencidos)) {
                                binding.btnReNewExpiredPrescriptions.visibility = View.INVISIBLE
                                initialHeight = binding.btnReNewExpiredPrescriptions.height
                                binding.openCurrentPrescriptionsMenu.visibility = View.GONE
                                val animOpen = ValueAnimator.ofInt(0, 0 + 320)
                                animOpen.addUpdateListener { valueAnimator ->
                                    val value = valueAnimator.animatedValue as Int
                                    binding.btnReNewExpiredPrescriptions.layoutParams.width = value
                                    binding.btnReNewExpiredPrescriptions.layoutParams.height = initialHeight
                                    binding.btnReNewExpiredPrescriptions.requestLayout()
                                }
                                animOpen.duration = 900
                                animOpen.start()
                                Handler().postDelayed({
                                    binding.tvReNewPrescriptions.text = getString(R.string.renovar_recetas_vencidas)
                                }, 800)
                                Handler().postDelayed({
                                    binding.btnReNewExpiredPrescriptions.visibility = View.VISIBLE
                                }, 100)
                            }
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {
                            val customTabView = tab?.customView
                            val tabCard: MaterialCardView? = customTabView?.findViewById(R.id.cvCustomTabLayout)
                            val tabText: TextView? = customTabView?.findViewById(R.id.tvCustomTabLayout)
                            tabCard?.setCardBackgroundColor(resources.getColor(R.color.blue))
                            tabText?.setTextColor(resources.getColor(R.color.white))
                        }

                        override fun onTabReselected(tab: TabLayout.Tab?) {
                            // No es necesario hacer nada aquí, ya que no hay cambios en una pestaña reseleccionada
                        }
                    })
                }
            }
        }
    }

}