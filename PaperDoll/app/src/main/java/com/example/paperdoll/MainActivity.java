package com.example.paperdoll;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Functional UIs
    public Button resetBtn;
    public Button aboutBtn;
    public Button switchBtn;
    public CanvasView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init UI comps
        InitView();
    }

    private void InitView() {
        resetBtn = (Button) findViewById(R.id.resetBtn);
        aboutBtn = (Button) findViewById(R.id.aboutBtn);
        switchBtn = (Button) findViewById(R.id.swithBtn);
        canvasView = (CanvasView) findViewById(R.id.canvasView);
    }

    public void resetBtnOnClick(View v) {
        canvasView.resetBody();
        canvasView.invalidate();
    }

    public void aboutBtnOnClick(View v) {
        AlertDialog about = new AlertDialog.Builder(this)
                .setTitle("CS349 A5 Paper Doll")
                .setMessage("Kaiwen Chen\n20713621\nk2999che")
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void switchBtnOnClick(View v) {
        canvasView.switchBody();
        canvasView.invalidate();
    }
}
