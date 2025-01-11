package org.meicode.badboy;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ServerActivity extends AppCompatActivity {
    private LinearLayout detailsContainer;
    private ImageView displayImage;
    private TextView displayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_server);
        // Set up the toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Server Screen");
            SpannableString title = new SpannableString("Server Screen");
            title.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        }
        TextView textView1 = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        TextView textView5 = findViewById(R.id.textView5);
        TextView textView6 = findViewById(R.id.textView6);
        TextView textView7 = findViewById(R.id.textView7);

        detailsContainer = findViewById(R.id.details_container);
        displayImage = findViewById(R.id.display_image);
        displayData = findViewById(R.id.display_data);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 1!", R.drawable.batteryp);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 2!", R.drawable.motorunit);
            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 3!", R.drawable.dbw);
            }
        });
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 1!", R.drawable.tilting);
            }
        });

        textView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 2!", R.drawable.light);
            }
        });

        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("This is the data for TextView 3!", R.drawable.chassis);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the main page
            onBackPressed(); // This will take the user back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDetails(String data, int imageResId) {
        // Show the details container (image and text)
        detailsContainer.setVisibility(View.VISIBLE);

        // Set the image and text data
        displayImage.setImageResource(imageResId);
        displayData.setText(data);
    }
}
