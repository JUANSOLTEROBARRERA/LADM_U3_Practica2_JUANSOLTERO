package mx.tecnm.tepic.ladm_u3_practica2_juansoltero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    lateinit var binding:ActivityMain3Binding
    var idElegido=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)




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
                    "DOMICILIO", binding.dom.text.toString(),
                    "LICENCIACOND",binding.lic.text.toString().toString())
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
    }
}