package com.example.mina.boxoffice.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.mina.boxoffice.R;


/**
 * Created by mina on 31/03/17.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
    public SpinnerAdapter(Context context, String[] options) {
        super(context, R.layout.spinner_item, R.id.sort_option, options);
    }
}
