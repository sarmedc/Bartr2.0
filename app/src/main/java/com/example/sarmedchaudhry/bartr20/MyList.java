package com.example.sarmedchaudhry.bartr20;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.content.Context;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class MyList extends Fragment {

    View view;
    private DBHelper dbHandler;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Items> data;
    private EditText itemName, itemDescrip, itemPrice;
    private ImageView image;
    private Button addItemImage, addItemCancel, addItemAccept;
    private Items item;
    private Bitmap bitmap;
    private String selectedImagePath;
    private String filemanagerstring;
    private int width, height, id;
    public static String LIST_SEPARATOR = "__,__";

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_list, container, false);

        dbHandler = new DBHelper(getActivity(),null,null,1);

        recyclerView = (RecyclerView) view.findViewById(R.id.item_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = new ArrayList<>();
        adapter = new ItemAdapter(getActivity(), data, MyList.this);
        recyclerView.setAdapter(adapter);

        Button btn = (Button) view.findViewById(R.id.item_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(getView());
            }
        });

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        Bundle bundle = this.getArguments();
        if(bundle != null)
            dbHelper = (DBHelper) bundle.getSerializable("db");

        return view;

    }

    /*public static List<Items> addItem(View view) {
        List<Items> data = new ArrayList<>();

        return data;

    }*/

    public void addItem(View view) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.additem_dialog);
        dialog.setTitle("Add New Item");

        itemName = (EditText) dialog.findViewById(R.id.item_name);
        itemDescrip = (EditText) dialog.findViewById(R.id.item_description);
        itemPrice = (EditText) dialog.findViewById(R.id.item_price);
        image = (ImageView) dialog.findViewById(R.id.item_Image);
        addItemImage = (Button) dialog.findViewById(R.id.add_image_button);
        addItemCancel = (Button) dialog.findViewById(R.id.dialog_cancel_button);
        addItemAccept = (Button) dialog.findViewById(R.id.dialog_positive_button);

        addItemImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("1");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 0);
            }
        });
        System.out.println("3");

        itemPrice.setRawInputType(Configuration.KEYBOARD_12KEY);
        itemPrice.addTextChangedListener(new TextWatcher() {
            DecimalFormat dec = new DecimalFormat("0.00");
            private String current = "";
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    itemPrice.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setMaximumFractionDigits(0);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    itemPrice.setText(formatted);
                    itemPrice.setSelection(formatted.length());
                    itemPrice.addTextChangedListener(this);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(!s.toString().equals(current)){
                    itemPrice.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    itemPrice.setText(formatted);
                    itemPrice.setSelection(formatted.length());

                    itemPrice.addTextChangedListener(this);
                }*/
            }
        });

        // if button is clicked, close the custom dialog
        addItemCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //final DBHelper finalDbHandler = dbHandler;
        addItemAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean closeDialog;
                while (TextUtils.isEmpty(itemName.getText().toString()) || TextUtils.isEmpty(itemDescrip.getText().toString()) || TextUtils.isEmpty(itemPrice.getText().toString())) {
                    if (TextUtils.isEmpty(itemName.getText().toString())) {
                        itemName.setError("Enter name for item!");
                    }
                    if (TextUtils.isEmpty(itemDescrip.getText().toString())) {
                        itemDescrip.setError("Enter description for item!");
                    }
                    if (TextUtils.isEmpty(itemPrice.getText().toString())) {
                        itemPrice.setError("Enter price for item!");
                    }
                    closeDialog = false;

                }
                    System.out.println("4");
                    item = new Items(bitmap, itemName.getText().toString(), itemPrice.getText().toString(), itemDescrip.getText().toString() );
                    //item.imageBitmap = bitmap;
                    //item.itemDescrip =
                    //item.itemName = ;
                    //item.itemPrice = ;

                    data.add(item);
                    adapter.notifyItemInserted(data.size() - 1);
                    image.setImageDrawable(null);

                    // itemDB.execSQL("INSERT INTO items (id, name, description, image) VALUES ('" +
                    //       id + "', '" + itemName.getText().toString() + "', '" + itemDescrip.getText().toString() + "', '" + bitmap + "');");

                    //System.out.println(item.getName());
                //dbhelper not work
                    dbHandler.addItem(item);

                    closeDialog = true;


                if (closeDialog)
                    dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout((width), (2 * height) / 3);
    }

    public void ItemUpdate(View v){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.additem_dialog);
        dialog.setTitle("Update Item");

        final EditText newItemName = (EditText) dialog.findViewById(R.id.item_name);
        final EditText newItemDescrip = (EditText) dialog.findViewById(R.id.item_description);
        final EditText newItemPrice = (EditText) dialog.findViewById(R.id.item_price);
        //newImage = (ImageView) dialog.findViewById(R.id.view);
        addItemImage = (Button) dialog.findViewById(R.id.add_image_button);
        addItemCancel = (Button) dialog.findViewById(R.id.dialog_cancel_button);
        addItemAccept = (Button) dialog.findViewById(R.id.dialog_positive_button);

        newItemName.setText(itemName.getText().toString());
        newItemDescrip.setText(itemDescrip.getText().toString());
        newItemPrice.setText(itemPrice.getText().toString());
        image.setImageBitmap(bitmap);

        addItemImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("Shouldnt be here 1");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 0);
            }
        });

        newItemPrice.setRawInputType(Configuration.KEYBOARD_12KEY);
        newItemPrice.addTextChangedListener(new TextWatcher() {
            DecimalFormat dec = new DecimalFormat("0.00");
            private String current = "";

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    newItemPrice.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setMaximumFractionDigits(0);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    newItemPrice.setText(formatted);
                    newItemPrice.setSelection(formatted.length());
                    newItemPrice.addTextChangedListener(this);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(!s.toString().equals(current)){
                    itemPrice.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    itemPrice.setText(formatted);
                    itemPrice.setSelection(formatted.length());

                    itemPrice.addTextChangedListener(this);
                }*/
            }
        });

        // if button is clicked, close the custom dialog
        addItemCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addItemAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean closeDialog;
                if (TextUtils.isEmpty(newItemName.getText().toString()) || TextUtils.isEmpty(newItemDescrip.getText().toString()) || TextUtils.isEmpty(newItemPrice.getText().toString())) {
                    if (TextUtils.isEmpty(newItemName.getText().toString())) {
                        newItemName.setError("Enter name for item!");
                    }
                    if (TextUtils.isEmpty(newItemDescrip.getText().toString())) {
                        newItemDescrip.setError("Enter description for item!");
                    }
                    if (TextUtils.isEmpty(newItemPrice.getText().toString())) {
                        newItemPrice.setError("Enter price for item!");
                    }
                    closeDialog = false;

                } else {
                    itemName.setText(newItemName.getText().toString());
                    itemDescrip.setText(newItemDescrip.getText().toString());
                    itemPrice.setText(newItemPrice.getText().toString());
                    item.setDescription(newItemDescrip.getText().toString());
                    item.setName(newItemName.getText().toString());
                    item.setPrice(newItemPrice.getText().toString());
                    item.setImage(bitmap);
                    System.out.println("Shouldnt be here 2");
                    adapter.notifyDataSetChanged();

                    image.setImageDrawable(null);
                    bitmap = null;

                    closeDialog = true;
                }

                if (closeDialog)
                    dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout((width), (2 * height) / 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                Uri selectedImageUri = data.getData();

                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selectedImageUri);
                        System.out.println("2");
                    }
                    // Log.d(TAG, String.valueOf(bitmap));

                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //OI FILE Manager
              //  filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
              //  selectedImagePath = getPath(selectedImageUri);

                //NOW WE HAVE OUR WANTED STRING
               /* if (selectedImagePath != null) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(selectedImagePath,bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap,90,75,true);
                    image.setImageBitmap(bitmap);
                }
                else
                    System.out.println("filemanagerstring is the right one for you!");*/
            }
        }
    }

 /*   public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
*/
    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/
}
