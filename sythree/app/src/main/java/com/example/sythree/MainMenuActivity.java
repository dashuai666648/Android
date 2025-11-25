package com.example.sythree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // 设置点击监听器
        setupClickListeners();
    }

    private void setupClickListeners() {
        // 动物列表功能
        CardView animalCard = findViewById(R.id.animal_card);
        if (animalCard != null) {
            animalCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainMenuActivity.this, "进入动物列表功能", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 自定义AlertDialog功能
        CardView dialogCard = findViewById(R.id.dialog_card);
        if (dialogCard != null) {
            dialogCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainMenuActivity.this, "进入自定义AlertDialog功能", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // XML菜单功能
        CardView menuCard = findViewById(R.id.menu_card);
        if (menuCard != null) {
            menuCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, MenuTestActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainMenuActivity.this, "进入XML菜单功能", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 上下文菜单功能
        CardView contextCard = findViewById(R.id.context_card);
        if (contextCard != null) {
            contextCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, ContextMenuActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainMenuActivity.this, "进入上下文菜单功能", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
