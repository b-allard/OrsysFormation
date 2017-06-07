package com.orsys.ballard.addclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orsys.ballard.addclient.Entity.Client;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class AddClientActivity extends Activity {

    private static final String CONTACT_FILE = "CONTACT_FILE";
    private Bitmap contactBitmap;
    private Realm realm;

    @BindView(R.id.first_name_editText)
    EditText firstNameEditText;
    @BindView(R.id.last_name_editText)
    EditText lastNameEditText;
    @BindView(R.id.address_editText)
    EditText addressEditText;
    @BindView(R.id.zipcode_editText)
    EditText zipcodeEditText;
    @BindView(R.id.city_editText)
    EditText cityEditText;
    @BindView(R.id.contact_imageview)
    ImageView contactImageView;

    @BindView(R.id.toolbar_cancel_textview)
    TextView cancelToolbarTextView;

    @BindView(R.id.toolbar_save_textview)
    TextView saveToolbarTextView;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cancelToolbarTextView.setVisibility(View.VISIBLE);
        saveToolbarTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            byte[] byteArray = savedInstanceState.getByteArray(CONTACT_FILE);

            if (byteArray == null) {
                Drawable drawable = getResources().getDrawable(R.drawable.contact);
                contactImageView.setImageDrawable(drawable);
            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                contactImageView.setImageBitmap(bitmap);
            }
        }

    }

    @OnClick(R.id.toolbar_cancel_textview)
    public void cancelAction() {

    }

    @OnClick(R.id.toolbar_save_textview)
    public void saveAction() {
        realm.beginTransaction();
        Number max = realm.where(Client.class).max("id");
        Integer id = (max != null) ? max.intValue() + 1 : 0;
        Client client = realm.createObject(Client.class, id);
        client.setFirstName(firstNameEditText.getText().toString());
        client.setLastName(lastNameEditText.getText().toString());
        client.setAddress(addressEditText.getText().toString());
        client.setZipcode(zipcodeEditText.getText().toString());
        client.setCity(cityEditText.getText().toString());
        client.setImageBytes(extractBytesOfBitmap());
        realm.commitTransaction();
    }


    @OnClick(R.id.camera_floating_button)
    public void takePictures() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            contactBitmap = (Bitmap) extras.get("data");
            contactImageView.setImageBitmap(contactBitmap);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        byte[] byteArray = extractBytesOfBitmap();
        outState.putByteArray(CONTACT_FILE, byteArray);
    }

    private byte[] extractBytesOfBitmap() {
        byte[] byteArray = null;
        if (contactBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            contactBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
        return byteArray;
    }
}
