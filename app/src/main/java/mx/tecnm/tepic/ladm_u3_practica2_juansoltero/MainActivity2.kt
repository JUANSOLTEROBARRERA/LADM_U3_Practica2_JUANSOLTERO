package mx.tecnm.tepic.ladm_u3_practica2_juansoltero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_juansoltero.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    var idElegido=""
    lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        AlertDialog.Builder(this)
            .setMessage("BIENVENIDO" +
                    "   Al inicio de esta ventana se encuentra un formulario inicial que es para " +
                    " actualizar un auto, deberás cambiar los datos que quieras actualizar . Damos " +
                    " clic sobre el botón actualizar y sería todo.")
            .show()
        idElegido = intent.extras!!.getString("idelegido")!!
        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("AUTOMOVIL")
            .document(idElegido)
            .get()//Obtiene un documento
            .addOnSuccessListener {
                binding.modelo.setText(it.getString("MODELO"))
                binding.marca.setText(it.getString("MARCA"))
                binding.kilo.setText(it.getLong("KILOMETRAGE").toString())
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
            baseRemota.collection("AUTOMOVIL")
                .document(idElegido)
                .update("MODELO", binding.modelo.text.toString(),
                    "MARCA", binding.marca.text.toString(),
                    "KILOMETRAGE",binding.kilo.text.toString().toInt())
                .addOnSuccessListener {
                    Toast.makeText(this,"EXITO! SE ACTUALIZO CORRECTAMENTE EN LA NUBE", Toast.LENGTH_LONG).show()
                    binding.modelo.setText("")
                    binding.marca.setText("")
                    binding.kilo.text.clear()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }
    }
}