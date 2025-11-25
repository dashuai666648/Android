package com.example.sythree;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MenuTestActivity extends AppCompatActivity {

    private TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_test);

        testTextView = findViewById(R.id.test_text_view);
        // 设置初始文本和大小（16sp对应"中"）
        testTextView.setTextSize(16);
        testTextView.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.font_size_small) {
            testTextView.setTextSize(10);
            Toast.makeText(this, "字体大小设置为小 (10sp)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.font_size_medium) {
            testTextView.setTextSize(16);
            Toast.makeText(this, "字体大小设置为中 (16sp)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.font_size_large) {
            testTextView.setTextSize(20);
            Toast.makeText(this, "字体大小设置为大 (20sp)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_normal_item) {
            Toast.makeText(this, "普通菜单项被点击了！", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.font_color_red) {
            testTextView.setTextColor(Color.RED);
            Toast.makeText(this, "字体颜色设置为红色", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.font_color_black) {
            testTextView.setTextColor(Color.BLACK);
            Toast.makeText(this, "字体颜色设置为黑色", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
