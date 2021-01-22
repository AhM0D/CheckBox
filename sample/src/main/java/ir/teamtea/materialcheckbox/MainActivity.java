package ir.teamtea.materialcheckbox;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import ir.teamtea.circlecheckbox.CircleCheckBox;

public class MainActivity extends AppCompatActivity {

    private CircleCheckBox checkbox_xml;
    private CircleCheckBox checkbox_java;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        findViewById(R.id.button).setOnClickListener(View -> {
            checkbox_xml.setChecked(!checkbox_xml.isChecked());
            checkbox_java.setChecked(!checkbox_java.isChecked());
        });

    }

    private void initView() {
        initCheckBoxWithXml();
        initCircleCheckBoxWithLogic();
    }

    private void initCheckBoxWithXml() {
        checkbox_xml = findViewById(R.id.checkbox);
        checkbox_xml.setDuration(150);
        checkbox_xml.setCheckAnimDuration(100);
    }

    private void initCircleCheckBoxWithLogic() {
        FrameLayout layout = findViewById(R.id.customView);
        checkbox_java = createCircleCheckBox();
        layout.addView(checkbox_java);
    }

    private CircleCheckBox createCircleCheckBox() {
        return new CircleCheckBox.Builder(this)
                .setBorderThickness(2)
                .setCheckThickness(4)
                .setBackGroundColor(getResources().getColor(R.color.blue))
                .setCheckIconColor(getResources().getColor(R.color.white))
                .setBorderColor(getResources().getColor(R.color.white))
                .setCircleStrokeStartAngle(180)
                .setCircleStrokeDegree(180)
                .setDuration(150)
                .setCheckAnimDuration(100)
                .setOnCheckedChangeListener(isChecked -> {

                })
                .build();
    }
}