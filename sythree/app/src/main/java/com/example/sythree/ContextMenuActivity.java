package com.example.sythree;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_menu);

        // 初始化数据
        dataList = new ArrayList<>();
        dataList.add("One");
        dataList.add("Two");
        dataList.add("Three");
        dataList.add("Four");
        dataList.add("Five");

        // 初始化ListView
        listView = findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this, R.layout.list_item_context, R.id.item_text, dataList);
        listView.setAdapter(adapter);

        // 设置多选模式
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // 更新ActionMode标题
                int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // 创建ActionMode菜单
                mode.getMenuInflater().inflate(R.menu.context_menu, menu);
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    // 删除选中的项目
                    deleteSelectedItems();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        });
    }

    private void deleteSelectedItems() {
        // 获取选中的项目位置
        List<Integer> selectedPositions = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                selectedPositions.add(i);
            }
        }

        // 从后往前删除，避免索引变化问题
        for (int i = selectedPositions.size() - 1; i >= 0; i--) {
            int position = selectedPositions.get(i);
            dataList.remove(position);
        }

        // 更新适配器
        adapter.notifyDataSetChanged();
        
        // 显示删除提示
        Toast.makeText(this, "已删除 " + selectedPositions.size() + " 个项目", Toast.LENGTH_SHORT).show();
    }
}
