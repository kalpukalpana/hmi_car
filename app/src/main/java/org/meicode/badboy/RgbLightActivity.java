package org.meicode.badboy;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RgbLightActivity extends AppCompatActivity {

    ImageView imageView,imageView1;
    TextView mColorValues;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rgb_light);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView=findViewById(R.id.colorPicker);
        imageView1=findViewById(R.id.imageView);
        mColorValues=findViewById(R.id.displayValues);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("RGB Light Control");
            SpannableString title = new SpannableString("RGB Light Control");
            title.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        }

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN|| event.getAction()==MotionEvent.ACTION_MOVE)
                {
                    bitmap=imageView.getDrawingCache();
                    int pixels=bitmap.getPixel((int)event.getX(),(int) event.getY());

                    int r=Color.red(pixels);
                    int g=Color.green(pixels);
                    int b=Color.blue(pixels);

                    String hex="#" + Integer.toHexString(pixels);
                    imageView1.setColorFilter(Color.rgb(r,g,b));
                    mColorValues.setText("RGB: "+r+","+g+","+b+"");


                }
                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the main page
            onBackPressed(); // This will take the user back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}