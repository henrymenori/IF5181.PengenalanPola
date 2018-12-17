package pengenalanpola.if5181.if5181pengenalanpola;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SectionPageAdapter sectionPageAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        sectionPageAdapter.addFragment("Attribute", new AttributeFragment());
        sectionPageAdapter.addFragment("Enhancement", new EnhancementFragment());
        sectionPageAdapter.addFragment("Pre-Process", new PreprocessFragment());
        sectionPageAdapter.addFragment("Alphanumeric Recognition", new AlphanumericRecognitionFragment());
        sectionPageAdapter.addFragment("Face Recognition", new FaceRecognitionFragment());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPageAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

}
