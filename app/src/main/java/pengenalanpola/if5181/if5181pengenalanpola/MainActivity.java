package pengenalanpola.if5181.if5181pengenalanpola;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadTugas01(View view) {
        Intent intent = new Intent(MainActivity.this, Activity1.class);
        startActivity(intent);
    }
}