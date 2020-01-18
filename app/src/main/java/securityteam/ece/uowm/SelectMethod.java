package securityteam.ece.uowm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMethod extends AppCompatActivity {
    private Button button_root;
    private  Button button_non_root;



    protected void onCreate(Bundle SaveInstanceState){
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.select_method);

        button_root = findViewById(R.id.root_method);
        button_non_root = findViewById(R.id.non_root);

        button_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActiviteMain();
            }
        });


        button_non_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActiviteMethod2();
            }
        });



    }

    public void openActiviteMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openActiviteMethod2(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
