package mx.tecnm.tepic.ladm_u3_practica2_juansoltero

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    lateinit var binding:ActivityMain3Binding
    var idElegido=""
    var listaID = ArrayList<String>()
    val baseRemota = FirebaseFirestore.getInstance()
    val arreglo = ArrayList<String>()
    val arreglo2 = ArrayList<String>()
    var arreglofiltro = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.opciones.setOnClickListener {
            binding.lista.visibility =  View.VISIBLE
        }
        binding.lista.visibility =  View.GONE
        idElegido = intent.extras!!.getString("idelegido")!!
        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("ARRENDAMIENTO")
            .document(idElegido)
            .get()//Obtiene un documento
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("NOMBRE"))
                binding.dom.setText(it.getString("DOMICILIO"))
                binding.lic.setText(it.getString("LICENCIACOND"))
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }
        binding.regresar.setOnClickListener {
            finish()
        }
        binding.actualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("ARRENDAMIENTO")
                .document(idElegido)
                .update("NOMBRE", binding.nombre.text.toString(),
                    "IDAUTO", binding.autom.text.toString(),
                    "DOMICILIO", binding.dom.text.toString(),
                    "LICENCIACOND",binding.lic.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this,"EXITO! SE ACTUALIZO CORRECTAMENTE EN LA NUBE", Toast.LENGTH_LONG).show()
                    binding.nombre.setText("")
                    binding.dom.setText("")
                    binding.lic.setText("")
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }

        //---------mostrar en listview
        FirebaseFirestore.getInstance()
            .collection("AUTOMOVIL")
            .addSnapshotListener { query, error ->
                if(error!=null){
                    //SI HUBO ERROR!
                    AlertDialog.Builder(this)
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }
                arreglo.clear()
                arreglo2.clear()
                listaID.clear()


                for(documento in query!!){

                    var cadena = "${documento.getString("MODELO")}" +
                            " -- ${documento.getString("MARCA")} -- ${documento.getLong("KILOMETRAGE")}"
                    arreglo.add(cadena)

                    listaID.add(documento.id.toString())

                    binding.lista.adapter= ArrayAdapter<String>(this,
                        R.layout.simple_list_item_1,arreglo)
                    binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                        dialogoselecciona(posicion)

                    }
                }

            }

        //-----------------------

    }
    private fun dialogoselecciona(posicion: Int) {
        var idElegido = listaID.get(posicion)

        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage("¿DESEAS SELECCIONAR \n${arreglo.get(posicion)}?")
            .setPositiveButton("SELECCIONAR"){d,i->
                binding.autom.setText("${idElegido}")

            }
            .setNeutralButton("CANCELAR"){d,i->}
            .show()
    }
}