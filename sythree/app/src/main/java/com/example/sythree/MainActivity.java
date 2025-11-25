package com.example.sythree;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView animalList;
    private List<Map<String, Object>> animalData;
    private SimpleAdapter adapter;
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "animal_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化通知管理器
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        // 初始化视图
        animalList = findViewById(R.id.animal_list);

        // 准备数据
        prepareAnimalData();

        // 创建适配器
        adapter = new SimpleAdapter(
                this,
                animalData,
                R.layout.list_item,
                new String[]{"name", "image"},
                new int[]{R.id.animal_name, R.id.animal_image}
        );

        // 设置适配器
        animalList.setAdapter(adapter);

        // 设置点击监听器
        animalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String animalName = (String) animalData.get(position).get("name");
                
                // 显示Toast
                Toast.makeText(MainActivity.this, animalName, Toast.LENGTH_SHORT).show();
                
                // 发送通知
                sendNotification(animalName);
                
                // 显示自定义AlertDialog
                showCustomDialog();
            }
        });
    }

    private void prepareAnimalData() {
        animalData = new ArrayList<>();
        
        // 添加动物数据
        addAnimal("Lion", R.drawable.lion);
        addAnimal("Tiger", R.drawable.tiger);
        addAnimal("Monkey", R.drawable.monkey);
        addAnimal("Dog", R.drawable.dog);
        addAnimal("Cat", R.drawable.cat);
        addAnimal("Elephant", R.drawable.elephant);
    }

    private void addAnimal(String name, int imageResId) {
        Map<String, Object> animal = new HashMap<>();
        animal.put("name", name);
        animal.put("image", imageResId);
        animalData.add(animal);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "动物通知";
            String description = "显示选中的动物信息";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String animalName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(animalName)
                .setContentText("您选择了" + animalName + "，这是一个可爱的动物！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showCustomDialog() {
        // 创建AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        // 获取自定义布局
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        
        // 设置自定义布局到AlertDialog
        builder.setView(dialogView);
        
        // 创建AlertDialog
        AlertDialog dialog = builder.create();
        
        // 获取布局中的控件
        EditText etUsername = dialogView.findViewById(R.id.et_username);
        EditText etPassword = dialogView.findViewById(R.id.et_password);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSignIn = dialogView.findViewById(R.id.btn_sign_in);
        
        // 设置取消按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "已取消登录", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 设置登录按钮点击事件
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "登录成功！用户名: " + username, Toast.LENGTH_LONG).show();
                }
            }
        });
        
        // 显示对话框
        dialog.show();
    }
}
