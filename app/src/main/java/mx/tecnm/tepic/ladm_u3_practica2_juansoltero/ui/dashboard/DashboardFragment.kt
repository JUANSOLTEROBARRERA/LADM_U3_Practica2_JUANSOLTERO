package mx.tecnm.tepic.ladm_u3_practica2_juansoltero.ui.dashboard

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.MainActivity2
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.databinding.FragmentDashboardBinding
import java.lang.NullPointerException

class DashboardFragment : Fragment() {
    var listaID = ArrayList<String>()
    var listaID2 = ArrayList<String>()
    val baseRemota = FirebaseFirestore.getInstance()
    val arreglo = ArrayList<String>()
    val arreglo2 = ArrayList<String>()
    var arreglofiltro = ArrayList<String>()
    var filtro = "MODELO"
    var hayfiltro = false
    val spinner = arrayOf("Modelo", "Marca","Kilometraje")

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root



        AlertDialog.Builder(requireContext())
            .setMessage("BIENVENIDO" +
                    "   Al inicio de esta ventana se encuentra un formulario inicial que es para " +
                    "insertar un nuevo automovil, al llenar los campos y dar clic en insertar " +
                    "éste se agregará a la base de datos y al listview. En la parte inferior podemos " +
                    "filtrar los autos que han sido agregados, se selecciona en el DropDown el " +
                    "campo por el que se desea filtrar y en el campo Clave lo que queremos buscar, " +
                    "si queremos quitar los filtros damos clic en mostrar todos. " +
                    "Al dar clic sobre un elemento de la lista se despliega un dialogo que nos dará la " +
                    "opción de eliminar, actualizar y cancelar.")
            .show()
        //CODIGO--------
        //CODIGO DE SPINNER
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item,spinner)
        adapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spinner1.adapter = adapter
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(binding.spinner1.selectedItemPosition == 0){
                    filtro = "MODELO"
                    binding.clave2.setText("")
                    binding.clave2.visibility = View.GONE
                }
                if(binding.spinner1.selectedItemPosition == 1){
                    filtro = "MARCA"
                    binding.clave2.setText("")
                    binding.clave2.visibility = View.GONE
                }
                if(binding.spinner1.selectedItemPosition == 2){
                    filtro = "KILOMETRAGE"
                    binding.clave2.setText("")
                    binding.clave2.visibility = View.VISIBLE
                }
            }

        }
        //----------------

        //---------mostrar en listview
        FirebaseFirestore.getInstance()
            .collection("AUTOMOVIL")
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
                arreglofiltro.clear()
                listaID.clear()


                for(documento in query!!){

                        var cadena = "${documento.getString("MODELO")}" +
                                " -- ${documento.getString("MARCA")} -- ${documento.getLong("KILOMETRAGE")}"
                        arreglo.add(cadena)

                        listaID.add(documento.id.toString())
                        try {
                            binding.lista.adapter = ArrayAdapter<String>(
                                requireContext(),
                                R.layout.simple_list_item_1, arreglo
                            )
                            binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                                dialogoEliminaActualiza(posicion)
                            }
                        }catch (err:NullPointerException){

                        }
                }

            }

        //-----------------------
        binding.insert.setOnClickListener{
            insertar()
        }
        binding.filtrar.setOnClickListener {
            if(hayfiltro){
                arreglofiltro.clear()
            }
            hayfiltro = true
            binding.lista.adapter = null

            (0..arreglo.size-1).forEach{
                if(filtro=="MODELO"){
                    var cad = arreglo.get(it).split(" -- ")
                    if(binding.clave.text.toString()!=cad.get(0)){
                        //arreglo.set(it,"")
                    }else{
                        arreglofiltro.add(arreglo.get(it))
                    }
                }
                if(filtro=="MARCA"){
                    var cad = arreglo.get(it).split(" -- ")
                    if(binding.clave.text.toString()!=cad.get(1)){
                        //arreglo.set(it,"")
                    }else{
                        arreglofiltro.add(arreglo.get(it))
                    }

                }
                if(filtro=="KILOMETRAGE"){
                    var cad = arreglo.get(it).split(" -- ")
                    if(binding.clave.text.toString().toInt()<=cad.get(2).toInt() && binding.clave2.text.toString().toInt()>=cad.get(2).toInt()){
                        arreglofiltro.add(arreglo.get(it))
                    }else{

                    }

                }
            }
            binding.lista.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.simple_list_item_1,arreglofiltro));

        }

        binding.todos.setOnClickListener {
            binding.lista.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.simple_list_item_1,arreglo));
            hayfiltro = false
            arreglofiltro.clear()
        }
        //--------------
        return root
    }

    private fun dialogoEliminaActualiza(posicion: Int) {
        var idElegido = listaID.get(posicion)
        if(!hayfiltro) {
            AlertDialog.Builder(requireContext()).setTitle("ATENCION")
                .setMessage("¿QUÉ DESEAS HACER CON\n${arreglo.get(posicion)}?")
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
                        " (La lista de opciones para eliminar y actualizar sólo saldrá" +
                        " cuando no haya datos filtrados)!")
                .show()
                binding.clave.setText("")
                binding.clave2.setText("")
        }
    }

    private fun eliminar(idElegido: String) {
        baseRemota.collection("AUTOMOVIL")
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
    private fun actualizar(idElegido: String) {
        var otraVentana = Intent(requireContext(), MainActivity2::class.java)
        otraVentana.putExtra("idelegido", idElegido)
        startActivity(otraVentana)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun insertar(){
        val baseRemota = FirebaseFirestore.getInstance()

        val datos = hashMapOf(
            "MODELO" to binding.modelo.text.toString(),
            "MARCA" to binding.marca.text.toString(),
            "KILOMETRAGE" to binding.kilo.text.toString().toInt()
        )

        baseRemota.collection("AUTOMOVIL")
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
        binding.modelo.setText("")
        binding.marca.setText("")
        binding.kilo.setText("")
    }
}