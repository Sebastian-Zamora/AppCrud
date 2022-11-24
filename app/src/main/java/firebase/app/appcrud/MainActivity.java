package firebase.app.appcrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import firebase.app.appcrud.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private List<Usuario> listUsuario = new ArrayList<Usuario>();
    ArrayAdapter<Usuario> arrayAdapterUsuario;

    EditText nomP, apP, correoP, passwordP;
    ListView listV_usuarios;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Usuario usuarioSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nombreUsuario);
        apP = findViewById(R.id.txt_apellidoUsuario);
        correoP = findViewById(R.id.txt_correoUsuario);
        passwordP = findViewById(R.id.txt_passwordUsuario);

        listV_usuarios = findViewById(R.id.lv_datosUsuarios);
        initFirebase();
        listarDatos();

        listV_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioSelected = (Usuario) parent.getItemAtPosition(position);
                nomP.setText(usuarioSelected.getNombre());
                apP.setText(usuarioSelected.getApellidos());
                correoP.setText(usuarioSelected.getCorreo());
                passwordP.setText(usuarioSelected.getPassword());
            }
        });


    }

    private void listarDatos() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsuario.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()){
                    Usuario p = objSnapshot.getValue(Usuario.class);
                    listUsuario.add(p);

                    arrayAdapterUsuario = new ArrayAdapter<Usuario>(MainActivity.this, android.R.layout.simple_list_item_1, listUsuario);
                    listV_usuarios.setAdapter(arrayAdapterUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String apellidos = apP.getText().toString();
        String password = passwordP.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add:{
                if(nombre.equals("")||correo.equals("")||apellidos.equals("")||password.equals("")){
                    validacion();
                }else {
                    Usuario p = new Usuario();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellidos(apellidos);
                    p.setCorreo(correo);
                    p.setPassword(password);
                    databaseReference.child("Usuario").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }
                break;
            }
            case R.id.icon_save:{
                Usuario p = new Usuario();
                p.setUid(usuarioSelected.getUid());
                p.setNombre(nomP.getText().toString().trim());
                p.setApellidos(apP.getText().toString().trim());
                p.setCorreo(correoP.getText().toString().trim());
                p.setPassword(passwordP.getText().toString().trim());
                databaseReference.child("Usuario").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Guardar", Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_delete:{
                Usuario p =new Usuario();
                p.setUid(usuarioSelected.getUid());
                databaseReference.child("Usuario").child(p.getUid()).removeValue();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        nomP.setText("");
        correoP.setText("");
        passwordP.setText("");
        apP.setText("");
    }

    private void validacion() {
        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String apellidos = apP.getText().toString();
        String password = passwordP.getText().toString();

        if (nombre.equals("")) {
            nomP.setError("Requerido");
        }else if (correo.equals("")) {
            correoP.setError("Requerido");
        }else if (apellidos.equals("")) {
            apP.setError("Requerido");
        }else if (password.equals("")) {
            passwordP.setError("Requerido");
        }
    }
}