package com.example.camotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button capture_image,detect;
    Bitmap imageBitmap;
    ImageView img_view;
    TextView text_to_display;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture_image=findViewById(R.id.capture_image);
        detect=findViewById(R.id.detect_text);
        text_to_display=findViewById(R.id.text_to_display);
        img_view=findViewById(R.id.image_view);

        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                text_to_display.setText("");
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });

    }

    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();


                detector.processImage(firebaseVisionImage)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                displayTestFromImage(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
    }
    private  void displayTestFromImage(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.TextBlock> blockList=firebaseVisionText.getTextBlocks();

        if(blockList.size()==0)
        {
            Toast.makeText(this,"No Text Found",Toast.LENGTH_SHORT).show();
        }
        else{
            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
            {
                    String text=block.getText();
                    text_to_display.setText(text);

            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            img_view.setImageBitmap(imageBitmap);
        }
    }
}
