package com.example.firestorecheck;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //to store the animal images
    private final String[] imageIDarray;

    //to store the list of countries
    private final String[] nameArray;

    //to store the list of countries
    private final String[] infoArray;

    public CustomListAdapter(Activity context, ArrayList<String> nameArrayParam, ArrayList<String> infoArrayParam, ArrayList<String> imageIDArrayParam) {

        super(context, R.layout.listview_row, nameArrayParam);
        this.context = context;
        this.imageIDarray = imageIDArrayParam.toArray(new String[0]);
        this.nameArray = nameArrayParam.toArray(new String[0]);
        this.infoArray = infoArrayParam.toArray(new String[0]);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextViewID);
        TextView infoTextField = rowView.findViewById(R.id.infoTextViewID);
        ImageView imageView = rowView.findViewById(R.id.imageView1ID);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);
        infoTextField.setText(infoArray[position]);
        //imageView.setImageResource(imageIDarray[position]);

        //ImageView headerImage = headerView.findViewById(R.id.profile_image);
        Picasso.get().load(imageIDarray[position]).centerInside().fit().into(imageView);

        return rowView;

    }

}
