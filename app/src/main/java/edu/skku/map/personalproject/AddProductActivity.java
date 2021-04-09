package edu.skku.map.personalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {


    private EditText product_name,
    product_price,
    product_color,
    product_gender,
    videoId,
    product_size;

    private Button addProduct;
    private ImageButton productImg;
    private Button back;
    boolean ck=false;
    int ck2=0;
    Uri currentUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner category;
    private StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        product_name = findViewById(R.id.Product_name);
        product_price = findViewById(R.id.Product_price);
        product_color = findViewById(R.id.Product_color);
        product_gender = findViewById(R.id.Product_gender);
        videoId = findViewById(R.id.videoId);
        product_size = findViewById(R.id.Product_size);
        addProduct = findViewById(R.id.add_Product);
        productImg = findViewById(R.id.Product_img);
        category = findViewById(R.id.Product_category);
        back = findViewById(R.id.add_product_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String[] kinds = getResources().getStringArray(R.array.kinds);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,kinds);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(AddProductActivity.this, "select "+category.getSelectedItem(), Toast.LENGTH_SHORT).show();
                ck2++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final long ONEM=1024*1024;
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,1000);
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ck) {
                    Toast.makeText(AddProductActivity.this, "select Image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ck2==0) {
                    Toast.makeText(AddProductActivity.this, "select category!", Toast.LENGTH_SHORT).show();
                }
                String name = product_name.getText().toString();
                String price = product_price.getText().toString();
                String color = product_color.getText().toString();
                String gender = product_gender.getText().toString();
                String videoid = videoId.getText().toString();
                String sizeinput = product_size.getText().toString();
                String kind = category.getSelectedItem().toString();
                if(name.isEmpty()||price.isEmpty()||color.isEmpty()||gender.isEmpty()||videoid.isEmpty()||sizeinput.isEmpty())
                {
                    Toast.makeText(AddProductActivity.this, "Fill All Blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> sizes = new HashMap<>();
                String temp_size="", temp_quantity="";
                for(int i=0;i<sizeinput.length();i++)
                {
                    if(sizeinput.charAt(i)=='(') {
                        i++;
                        for(;i<sizeinput.length();i++)
                        {
                            if(sizeinput.charAt(i)==',') break;
                            if(sizeinput.charAt(i)==' ') continue;
                            temp_size+=sizeinput.charAt(i);
                        }
                        i++;
                        for(;i<sizeinput.length();i++)
                        {
                            if(sizeinput.charAt(i)==')') break;
                            if(sizeinput.charAt(i)==' ') continue;
                            temp_quantity+=sizeinput.charAt(i);
                        }
                        sizes.put(temp_size,temp_quantity);
                        temp_size="";
                        temp_quantity="";
                    }
                }
                final Map<String, Object> product = new HashMap<>();
                product.put("productName",name);
                product.put("productPrice",price);
                product.put("productGender",gender);
                product.put("productColor",color);
                product.put("sizes",sizes);
                product.put("videoID",videoid);
                product.put("category",kind);
                Toast.makeText(AddProductActivity.this, "Adding....", Toast.LENGTH_SHORT).show();
                db.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            final int pid = task.getResult().size();
                            product.put("productId",String.valueOf(pid));
                            db.collection("Products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Log.d("onSuccess: ",documentReference.getId());
                                    StorageReference riversRef = mStorageRef.child(String.valueOf(pid));
                                    UploadTask uploadTask = riversRef.putFile(currentUri);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddProductActivity.this, "ImgUpload fail", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(AddProductActivity.this, "Add product!", Toast.LENGTH_SHORT).show();
                                            Intent createPostIntent = new Intent(AddProductActivity.this, MainActivity.class);
                                            startActivity(createPostIntent);
                                        }
                                    });

                                }
                            });
                        }
                    }
                });

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000) {
            ck=true;
            currentUri = data.getData();
            productImg.setImageURI(currentUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
