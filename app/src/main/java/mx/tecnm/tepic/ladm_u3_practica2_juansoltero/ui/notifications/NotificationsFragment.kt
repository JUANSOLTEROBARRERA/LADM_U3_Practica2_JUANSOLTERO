package mx.tecnm.tepic.ladm_u3_practica2_juansoltero.ui.notifications

import android.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.MainActivity2
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.MainActivity3
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.databinding.FragmentNotificationsBinding
import java.lang.NullPointerException
import java.time.LocalDateTime

class NotificationsFragment : Fragment() {
    var filtro = "NOMBRE"
    val spinner = arrayOf("Nombre", "Licencia","Domicilio","Modelo","Marca")
    val arreglo = ArrayList<String>()
    val arreglo2 = ArrayList<String>()
    val arreglo3 = ArrayList<String>()
    val arreglo5 = ArrayList<String>()
    val arreglo4 = ArrayList<String>()
    var listaID = ArrayList<String>()
    var listaID2 = ArrayList<String>()
    var arreglofiltro = ArrayList<String>()
    val baseRemota = FirebaseFirestore.getInstance()
    var hayfiltro = false



    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        AlertDialog.Builder(requireContext())
            .setMessage("BIENVENIDO" +
                    "   Al inicio de esta ventana se encuentra un formulario inicial que es para " +
                    "insertar un nuevo arrendamiento, al llenar los campos y dar clic en insertar " +
                    "??ste se agregar?? a la base de datos y al listview (para insertar el auto que " +
                    "ser?? rentado es necesario dar clic en mostrar opciones y dar clic sobre uno" +
                    "de los autos disponibles en la lista para seleccionarlo). En la parte inferior podemos " +
                    "filtrar los arrendamientos que han sido agregados, se selecciona en el DropDown el " +
                    "campo por el que se desea filtrar y en el campo Clave lo que queremos buscar, " +
                    "si queremos quitar los filtros damos clic en mostrar todos. " +
                    "Al dar clic sobre un elemento de la lista se despliega un dialogo que nos dar?? la " +
                    "opci??n de eliminar, actualizar y cancelar.")
            .show()
        binding.lista2.visibility = View.GONE
        //codigo spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item,spinner)
        adapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spinner1.adapter = adapter
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(binding.spinner1.selectedItemPosition == 0){
                    filtro = "NOMBRE"
                }
                if(binding.spinner1.selectedItemPosition == 1){
                    filtro = "LICENCIACOND"
                }
                if(binding.spinner1.selectedItemPosition == 2){
                    filtro = "DOMICILIO"
                }
                if(binding.spinner1.selectedItemPosition == 3){
                    filtro = "MODELO"
                }
                if(binding.spinner1.selectedItemPosition == 4){
                    filtro = "MARCA"
                }
            }

        }
        //
        //-----------------------
        //---------mostrar en listview2
        var lista1 = FirebaseFirestore.getInstance()
            .collection("AUTOMOVIL")
            .addSnapshotListener { query, error ->
                if(error!=null){
                    //SI HUBO ERROR!
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }
                arreglo3.clear()
                arreglo5.clear()
                arreglo4.clear()
                listaID2.clear()


                for(documento in query!!){
                    var cadena ="${documento.getString("MODELO")}" +
                            " -- ${documento.getString("MARCA")} -- ${documento.getLong("KILOMETRAGE")}"
                    var cadena2 = "${documento.id.toString()}" +
                            " -- ${documento.getString("MODELO")}" +
                            " -- ${documento.getString("MARCA")} -- ${documento.getLong("KILOMETRAGE")}"
                    arreglo3.add(cadena)
                    arreglo5.add(cadena2)

                    listaID2.add(documento.id.toString())
                }
                try {
                    binding.lista2.adapter = ArrayAdapter<String>(
                        requireContext(),
                        R.layout.simple_list_item_1, arreglo3
                    )

                    binding.lista2.setOnItemClickListener { adapterView, view, posicion, l ->
                        dialogoselecciona(posicion)
                    }
                }catch (err:NullPointerException){

                }
            }

        //-----------------------
        //---------mostrar en listview
        var lista2 = FirebaseFirestore.getInstance()
            .collection("ARRENDAMIENTO")
            .addSnapshotListener { query, error ->
                if(error!=null){
                    //SI HUBO ERROR!
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }
                arreglo.clear()
                arreglo2.clear()
                listaID.clear()
                arreglofiltro.clear()

                        for (documento in query!!) {
                            var cadena = "${documento.getString("NOMBRE")}" +
                                    " -- ${documento.getString("DOMICILIO")} " +
                                    " -- ${documento.getString("FECHA")}" +
                                    " -- ${documento.getString("LICENCIACOND")}"
                                    //" -- ${query2.get.getString("FECHA")}" +
                            (0..arreglo5.size-1).forEach{
                                var asist = arreglo5.get(it).toString().split(" -- ")
                                if (documento.getString("IDAUTO")==asist[0]){
                                    cadena = cadena + " -- ${asist[1]} -- ${asist[2]}"
                                }
                            }
                            arreglo.add(cadena)
                            //arreglo5.add(cadena)

                            listaID.add(documento.id.toString())

                        }
                try{
                    binding.lista.adapter= ArrayAdapter<String>(requireContext(),
                        R.layout.simple_list_item_1,arreglo)
                    binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                        dialogoEliminaActualiza(posicion)
                    }
                }catch (err:NullPointerException){

                }
            }

        binding.todos.setOnClickListener {
            binding.lista.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.simple_list_item_1,arreglo));
            hayfiltro = false
            arreglofiltro.clear()
        }
        binding.filtrar.setOnClickListener {
            if(hayfiltro){
                arreglofiltro.clear()
            }
            hayfiltro = true
            binding.lista.adapter = null

            (0..arreglo.size-1).forEach {
                if (filtro == "NOMBRE") {
                    var cad = arreglo.get(it).split(" -- ")
                    if (binding.clave.text.toString() != cad.get(0)) {

                    } else {
                        arreglofiltro.add(arreglo.get(it))
                    }

                }
                if (filtro == "LICENCIACOND") {
                    var cad = arreglo.get(it).split(" -- ")
                    if (binding.clave.text.toString() != cad.get(3)) {
                    } else {
                        arreglofiltro.add(arreglo.get(it))
                    }
                }
                if (filtro == "DOMICILIO") {
                    var cad = arreglo.get(it).split(" -- ")

                    if (binding.clave.text.toString().replace(" ","") != cad.get(1).replace(" ","")){
                    } else {
                        arreglofiltro.add(arreglo.get(it))
                    }

                }
                if (filtro == "MODELO") {
                    var cad = arreglo.get(it).split(" -- ")

                    if (binding.clave.text.toString() != cad.get(4)) {
                    }else {
                        arreglofiltro.add(arreglo.get(it))
                    }
                }
                if (filtro == "MARCA") {
                    var cad = arreglo.get(it).split(" -- ")

                    if (binding.clave.text.toString() != cad.get(5)) {
                    }else {
                        arreglofiltro.add(arreglo.get(it))
                    }
                }
            }
            binding.lista.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.simple_list_item_1,arreglofiltro));
        }



        binding.opciones.setOnClickListener {
            binding.lista2.visibility = View.VISIBLE
        }
        binding.insert.setOnClickListener{
            insertar()
        }


        return root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertar() {
        val baseRemota = FirebaseFirestore.getInstance()
        val current = LocalDateTime.now()
        val asist2 = current.toString().split("T")
        val datos = hashMapOf(
            "NOMBRE" to binding.nombre.text.toString(),
            "FECHA" to asist2.get(0),
            "LICENCIACOND" to binding.lic.text.toString(),
            "DOMICILIO" to binding.dom.text.toString(),
            "IDAUTO" to binding.autom.text.toString(),
        )

        baseRemota.collection("ARRENDAMIENTO")
            .add(datos)

            .addOnSuccessListener {

                Toast.makeText(requireContext(),"EXITO! SE INSERTO", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener{

                AlertDialog.Builder(requireContext())
                    .setMessage(it.message)
                    .show()
            }
        binding.nombre.setText("")
        binding.lic.setText("")
        binding.dom.setText("")
    }

    private fun dialogoselecciona(posicion: Int) {
        var idElegido = listaID2.get(posicion)

        AlertDialog.Builder(requireContext()).setTitle("ATENCION")
            .setMessage("??DESEAS SELECCIONAR \n${arreglo3.get(posicion)}?")
            .setPositiveButton("SELECCIONAR"){d,i->
                binding.autom.setText("${idElegido}")

            }
            .setNeutralButton("CANCELAR"){d,i->}
            .show()
    }

    private fun dialogoEliminaActualiza(posicion: Int) {
        var idElegido = listaID.get(posicion)
        if(!hayfiltro) {
            AlertDialog.Builder(requireContext()).setTitle("ATENCION")
                .setMessage("??QU?? DESEAS HACER CON\n${arreglo.get(posicion)}?")
                .setNegativeButton("ELIMINAR") { d, i ->
                    eliminar(idElegido)
                }
                .setPositiveButton("ACTUALIZAR") { d, i ->
                    actualizar(idElegido)
                }
                .setNeutralButton("CANCELAR") { d, i -> }
                .show()
        }else{
            binding.lista.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.simple_list_item_1,arreglo));
            hayfiltro = false
            arreglofiltro.clear()
            AlertDialog.Builder(requireContext())
                .setMessage("Favor de seleccionar nuevamente un" +
                        " documento en el ListView para desdeplegar su lista de opciones" +
                        " (La lista de opciones para eliminar y actualizar s??lo saldr??" +
                        " cuando no haya datos filtrados)!")
                .show()
            binding.clave.setText("")
        }
    }

    private fun actualizar(idElegido: String) {
        var otraVentana = Intent(requireContext(), MainActivity3::class.java)
        otraVentana.putExtra("idelegido", idElegido)
        startActivity(otraVentana)
    }

    private fun eliminar(idElegido: String) {
        baseRemota.collection("ARRENDAMIENTO")
            .document(idElegido)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"SE ELIMINO CON EXITO!",Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"ERROR! ${it.message!!}",Toast.LENGTH_LONG)
                    .show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}