package com.example.patientsportal.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentTripAdviceBinding


class TripAdviceFragment : Fragment(R.layout.fragment_trip_advice) {

    private lateinit var binding: FragmentTripAdviceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTripAdviceBinding.bind(view)

        binding.tvTripDeclared.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTripDeclared.setLinkTextColor(Color.BLUE)
        setSecondPart()
    }

    private fun setSecondPart() {
        val data = """
            <p>No olvide que sin importar el lugar donde se encuentre, desde cualquier dispositivo móvil podrá conectarse a través de su Portal Personal de Salud con su médico de cabecera y con la Guardia Virtual del Hospital Italiano, que funciona todos los días de 8 a 20 hs.</p>
            <br>
            <p>Para más información ingrese a la opción "Mi Portal" en la página web oficial del Hospital o en la aplicación "Mi Hospital" (disponible para iOS y Android).</p>
            <br>
            <p>Se informa que sólo se efectuarán reintegros por prestaciones médicas si éstas fueron comunicadas previamente a la empresa UA mediante una llamada a los teléfonos previamente informados.</p>
            <br>
            <p><b><u>Prestadores de Cartilla del Plan de Salud</u></b></p>
            <br>
            <ul>
                <li><b>Bahía Blanca:</b> Hospital Italiano Regional del Sur. Mariano Necochea 675. (0291)4583100</li>
                <li><b>Bariloche:</b> Sanatorio San Carlos. Av. Ezequiel Bustillo km. 1. (0294)4409800</li>
                <li><b>Ciudad de Córdoba:</b> Hospital Italiano de Córdoba. Roma 550. (0351)4106500 / Hospital Privado Centro Médico De Córdoba. Naciones Unidas 346. (0351)4688200</li>
                <li><b>Ciudad de Mendoza:</b> Hospital Italiano de Mendoza. Av. De Acceso Este 1070. 0800-333-0083</li>
                <li><b>Ciudad de Neuquén:</b> Centro de Medicina Integral del Comahue (Cmic). Santiago Del Estero 280. (0299)4487140</li>
                <li><b>La Plata:</b> Hospital Italiano de La Plata. Calle 51 1725 (entre 29 y 30). (0221)512 9500</li>
                <li><b>Mar de Ajó:</b> Clínica San Martín de Porres. Av. Libertador San Martín 1040. (02257)423076/420911</li>
                <li><b>Mar del Plata:</b> Hospital Privado de Comunidad. Córdoba 4545. (0223)4990000 / Clínica 25 de Mayo. 25 de Mayo 3558. (0223)4994000</li>
                <li><b>Pinamar:</b> Clínica Pinamed. Rivadavia 1083. (02254)482223 / Policlínica Arq. Bunge. Av. Shaw 59. (02254)482400 / Hospital Comunitario de Pinamar. Av. Shaw 255. (02254)491670// Laboratorio Bettiga: Av. Constitución 1472- 4to piso teléfono: 02254 49-1122</li>
                <li><b>Rosario:</b> Sanatorio Parque. Bv. Oroño 860. (0341)4200222 / Sanatorio Centro. Paraguay 975. (0341)5309000 / Sanatorio de Niños. Alvear 863. (0341)4204400</li>
                <li><b>San Bernardo:</b> Clínica San Bernardo. Garay 336. 0800-666-0666</li>
                <li><b>Villa Gesell:</b> Clínica Privada del Sol. Av. 3 3014. (02255)466466</li>
            </ul>
        """.trimIndent()

        val formattedText = Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT)
        binding.secondPartTripFragmentFormattedText.text = formattedText
    }
}