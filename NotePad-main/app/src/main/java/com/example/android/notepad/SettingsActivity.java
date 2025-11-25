/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * NotePad偏好设置的设置活动
 */
public class SettingsActivity extends Activity {
    
    public static final String PREFS_NAME = "NotePadPrefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_FONT_FAMILY = "font_family";
    
    private SharedPreferences prefs;
    private Spinner themeSpinner;
    private Spinner backgroundColorSpinner;
    private SeekBar textSizeSeekBar;
    private TextView textSizePreview;
    private Spinner fontFamilySpinner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // 在操作栏中启用返回按钮
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // 初始化视图
        themeSpinner = (Spinner) findViewById(R.id.theme_spinner);
        backgroundColorSpinner = (Spinner) findViewById(R.id.background_color_spinner);
        textSizeSeekBar = (SeekBar) findViewById(R.id.text_size_seekbar);
        textSizePreview = (TextView) findViewById(R.id.text_size_preview);
        fontFamilySpinner = (Spinner) findViewById(R.id.font_family_spinner);
        
        // 加载保存的偏好设置
        loadPreferences();
        
        // 设置监听器
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int textSize = 14 + progress; // 范围：14-24
                textSizePreview.setTextSize(textSize);
                textSizePreview.setText("Text Size: " + textSize + "sp");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void loadPreferences() {
        // 加载主题
        String theme = prefs.getString(KEY_THEME, "Light");
        String[] themes = getResources().getStringArray(R.array.theme_options);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equals(theme)) {
                themeSpinner.setSelection(i);
                break;
            }
        }
        
        // 加载背景颜色
        String bgColor = prefs.getString(KEY_BACKGROUND_COLOR, "White");
        String[] colors = getResources().getStringArray(R.array.background_color_options);
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(bgColor)) {
                backgroundColorSpinner.setSelection(i);
                break;
            }
        }
        
        // 加载文本大小
        int textSize = prefs.getInt(KEY_TEXT_SIZE, 18);
        textSizeSeekBar.setProgress(textSize - 14);
        textSizePreview.setTextSize(textSize);
        textSizePreview.setText("Text Size: " + textSize + "sp");
        
        // 加载字体类型
        String fontFamily = prefs.getString(KEY_FONT_FAMILY, "Default");
        String[] fonts = getResources().getStringArray(R.array.font_family_options);
        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].equals(fontFamily)) {
                fontFamilySpinner.setSelection(i);
                break;
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
    
    private void savePreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        
        // 保存主题
        String theme = themeSpinner.getSelectedItem().toString();
        editor.putString(KEY_THEME, theme);
        
        // 保存背景颜色
        String bgColor = backgroundColorSpinner.getSelectedItem().toString();
        editor.putString(KEY_BACKGROUND_COLOR, bgColor);
        
        // 保存文本大小
        int textSize = 14 + textSizeSeekBar.getProgress();
        editor.putInt(KEY_TEXT_SIZE, textSize);
        
        // 保存字体类型
        String fontFamily = fontFamilySpinner.getSelectedItem().toString();
        editor.putString(KEY_FONT_FAMILY, fontFamily);
        
        editor.apply();
        
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }
    
    public static SharedPreferences getPreferences(Activity activity) {
        return activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

