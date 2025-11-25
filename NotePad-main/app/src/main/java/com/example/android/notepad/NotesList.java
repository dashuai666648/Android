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

import com.example.android.notepad.NotePad;

import android.app.ListActivity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.FilterQueryProvider;
import android.view.View;
import android.view.ViewGroup;



public class NotesList extends ListActivity {

    // 用于日志记录和调试
    private static final String TAG = "NotesList";
    
    // 搜索查询字符串
    private String mSearchQuery = null;

    /**
     * Cursor适配器所需的列
     */
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 2
            NotePad.Notes.COLUMN_NAME_CATEGORY, // 3
            NotePad.Notes.COLUMN_NAME_IS_TODO, // 4
            NotePad.Notes.COLUMN_NAME_TODO_COMPLETED, // 5
    };

    /** 标题列的索引 */
    private static final int COLUMN_INDEX_TITLE = 1;
    
    /** 修改日期列的索引 */
    private static final int COLUMN_INDEX_MODIFICATION_DATE = 2;
    
    /** 分类列的索引 */
    private static final int COLUMN_INDEX_CATEGORY = 3;
    
    /** is_todo列的索引 */
    private static final int COLUMN_INDEX_IS_TODO = 4;
    
    /** todo_completed列的索引 */
    private static final int COLUMN_INDEX_TODO_COMPLETED = 5;

    /**
     * 当Android首次启动此Activity时调用onCreate方法。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 用户无需按住键即可使用菜单快捷键。
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        /* 如果启动此Activity的Intent中没有数据，则说明此Activity
         * 是在intent filter匹配MAIN操作时启动的。我们应该使用默认的
         * provider URI。
         */
        // 获取启动此Activity的Intent。
        Intent intent = getIntent();

        // 如果Intent中没有关联数据，则将数据设置为默认URI，
        // 该URI用于访问笔记列表。
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }

        /*
         * 为ListView设置上下文菜单激活的回调。监听器设置为
         * 此Activity。效果是ListView中的项目启用上下文菜单，
         * 上下文菜单由NotesList中的方法处理。
         */
        getListView().setOnCreateContextMenuListener(this);

        /* 执行托管查询。Activity会在需要时处理关闭和重新查询cursor。
         *
         * 请参阅关于在UI线程上执行provider操作的介绍性说明。
         */
        Cursor cursor = managedQuery(
            getIntent().getData(),            // 使用provider的默认content URI。
            PROJECTION,                       // 返回每个笔记的笔记ID和标题。
            null,                             // 无where子句，返回所有记录。
            null,                             // 无where子句，因此无where列值。
            NotePad.Notes.DEFAULT_SORT_ORDER  // 使用默认排序顺序。
        );

        /*
         * 以下两个数组在cursor中的列和ListView中项目的view ID之间创建"映射"。
         * dataColumns数组中的每个元素代表一个列名；
         * viewID数组中的每个元素代表一个View的ID。
         * SimpleCursorAdapter按升序映射它们，以确定每个列值
         * 将出现在ListView中的位置。
         */

        // 要在视图中显示的cursor列名，初始化为标题列
        String[] dataColumns = { 
            NotePad.Notes.COLUMN_NAME_TITLE,
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
        } ;

        // 将显示cursor列的view ID，初始化为noteslist_item.xml中的TextView
        int[] viewIDs = { android.R.id.text1, android.R.id.text2 };

        // 为ListView创建支持适配器，增强分类和待办显示
        SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // ListView的Context
                      R.layout.noteslist_item,          // 指向列表项XML
                      cursor,                           // 从中获取项目的cursor
                      dataColumns,
                      viewIDs
              ) {
            @Override
            public void setViewText(TextView v, String text) {
                if (v.getId() == android.R.id.text2) {
                    // 格式化时间戳
                    Cursor c = getCursor();
                    if (c != null) {
                        try {
                            long timestamp = Long.parseLong(text);
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            text = sdf.format(new java.util.Date(timestamp));
                        } catch (NumberFormatException e) {
                            // 如果解析失败，按原样显示文本
                        }
                    }
                }
                super.setViewText(v, text);
            }
            
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Cursor cursor = getCursor();
                
                if (cursor != null && cursor.moveToPosition(position)) {
                    // 获取分类标签
                    TextView categoryBadge = (TextView) view.findViewById(R.id.category_badge);
                    int categoryIndex = cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_CATEGORY);
                    
                    if (categoryIndex >= 0 && categoryBadge != null) {
                        String category = cursor.getString(categoryIndex);
                        if (category != null && !category.isEmpty() && !category.equals("None")) {
                            categoryBadge.setVisibility(View.VISIBLE);
                            categoryBadge.setText(category);
                            // 为不同分类设置不同颜色
                            setCategoryColor(categoryBadge, category);
                        } else {
                            categoryBadge.setVisibility(View.GONE);
                        }
                    }
                    
                    // 获取待办状态
                    int isTodoIndex = cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_IS_TODO);
                    int todoCompletedIndex = cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TODO_COMPLETED);
                    TextView todoIndicator = (TextView) view.findViewById(R.id.todo_indicator);
                    TextView todoStatusBadge = (TextView) view.findViewById(R.id.todo_status_badge);
                    
                    if (isTodoIndex >= 0 && todoIndicator != null && todoStatusBadge != null) {
                        int isTodo = cursor.getInt(isTodoIndex);
                        if (isTodo == 1) {
                            todoIndicator.setVisibility(View.VISIBLE);
                            todoStatusBadge.setVisibility(View.VISIBLE);
                            
                            int completed = todoCompletedIndex >= 0 ? cursor.getInt(todoCompletedIndex) : 0;
                            if (completed == 1) {
                                // 已完成的待办 - 绿色
                                todoIndicator.setBackgroundColor(0xFF4CAF50); // 绿色
                                android.graphics.drawable.GradientDrawable completedDrawable = new android.graphics.drawable.GradientDrawable();
                                completedDrawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                                completedDrawable.setColor(0xFF4CAF50);
                                completedDrawable.setCornerRadius(12);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    todoStatusBadge.setBackground(completedDrawable);
                                } else {
                                    todoStatusBadge.setBackgroundDrawable(completedDrawable);
                                }
                                todoStatusBadge.setText("✓ 已完成");
                            } else {
                                // 待处理中的待办 - 橙色
                                todoIndicator.setBackgroundColor(0xFFFF9800); // 橙色
                                android.graphics.drawable.GradientDrawable pendingDrawable = new android.graphics.drawable.GradientDrawable();
                                pendingDrawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                                pendingDrawable.setColor(0xFFFF9800);
                                pendingDrawable.setCornerRadius(12);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    todoStatusBadge.setBackground(pendingDrawable);
                                } else {
                                    todoStatusBadge.setBackgroundDrawable(pendingDrawable);
                                }
                                todoStatusBadge.setText("○ 待办");
                            }
                        } else {
                            todoIndicator.setVisibility(View.GONE);
                            todoStatusBadge.setVisibility(View.GONE);
                        }
                    }
                }
                
                TextView titleView = (TextView) view.findViewById(android.R.id.text1);
                TextView infoView = (TextView) view.findViewById(android.R.id.text2);
                if (titleView != null) {
                    titleView.setTextColor(ThemeHelper.getPrimaryTextColor(NotesList.this));
                }
                if (infoView != null) {
                    infoView.setTextColor(ThemeHelper.getSecondaryTextColor(NotesList.this));
                }
                view.setBackgroundColor(ThemeHelper.getCardBackgroundColor(NotesList.this));
                return view;
            }
            
            private void setCategoryColor(TextView badge, String category) {
                int color;
                if (category.equals("Work")) {
                    color = 0xFF2196F3; // 蓝色
                } else if (category.equals("Personal")) {
                    color = 0xFF9C27B0; // 紫色
                } else if (category.equals("Ideas")) {
                    color = 0xFFFFC107; // 琥珀色
                } else if (category.equals("Shopping")) {
                    color = 0xFF00BCD4; // 青色
                } else if (category.equals("Important")) {
                    color = 0xFFF44336; // 红色
                } else {
                    color = 0xFFFF5722; // 深橙色（默认）
                }
                
                // 创建指定颜色的圆角drawable
                android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
                drawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                drawable.setColor(color);
                drawable.setCornerRadius(12);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    badge.setBackground(drawable);
                } else {
                    badge.setBackgroundDrawable(drawable);
                }
            }
        };

        // 将ListView的适配器设置为刚刚创建的cursor适配器。
        setListAdapter(adapter);
        getListView().setBackgroundColor(ThemeHelper.getListBackgroundColor(this));
        
        // 设置搜索功能的过滤器
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String searchString = constraint != null ? constraint.toString() : "";
                mSearchQuery = searchString;
                Cursor newCursor = performSearch(searchString);
                // 更改cursor以更新列表
                adapter.changeCursor(newCursor);
                return newCursor;
            }
        });
    }
    
    /**
     * 根据搜索字符串执行搜索查询
     */
    private Cursor performSearch(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) {
            // 如果搜索为空，返回所有笔记
            return managedQuery(
                getIntent().getData(),
                PROJECTION,
                null,
                null,
                NotePad.Notes.DEFAULT_SORT_ORDER
            );
        }
        
        // 构建搜索查询 - 在标题和笔记内容中搜索
        String selection = "(" + NotePad.Notes.COLUMN_NAME_TITLE + " LIKE ? OR " +
                          NotePad.Notes.COLUMN_NAME_NOTE + " LIKE ?)";
        String[] selectionArgs = new String[] {
            "%" + searchString + "%",
            "%" + searchString + "%"
        };
        
        return managedQuery(
            getIntent().getData(),
            PROJECTION,
            selection,
            selectionArgs,
            NotePad.Notes.DEFAULT_SORT_ORDER
        );
    }

    /**
     * 当用户首次为此Activity点击设备的Menu按钮时调用。
     * Android传入一个已填充项目的Menu对象。
     *
     * 设置一个菜单，提供Insert选项以及此Activity的替代操作列表。
     * 其他想要处理笔记的应用程序可以通过提供包含ALTERNATIVE类别和
     * mimeType NotePad.Notes.CONTENT_TYPE的intent filter在Android中"注册"自己。
     * 如果这样做，onCreateOptionsMenu()中的代码会将包含intent filter的Activity
     * 添加到其选项列表中。实际上，菜单将向用户提供可以处理笔记的其他应用程序。
     * @param menu 一个Menu对象，应向其添加菜单项。
     * @return 始终返回True。应显示菜单。
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从XML资源加载菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

        // 设置搜索视图 - 以编程方式创建以获得更好的兼容性
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            // 以编程方式创建SearchView
            SearchView searchView = new SearchView(this);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // 执行搜索并直接更新cursor
                    Cursor newCursor = performSearch(newText);
                    SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
                    if (adapter != null) {
                        adapter.changeCursor(newCursor);
                    }
                    return true;
                }
            });
            searchItem.setActionView(searchView);
        }

        // 生成可以在整个列表上执行的任何其他操作。
        // 在正常安装中，这里没有找到其他操作，
        // 但这允许其他应用程序使用自己的操作扩展我们的菜单。
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // 如果剪贴板上有数据，则启用粘贴菜单项。
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);


        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

        // 如果剪贴板包含项目，则启用菜单上的Paste选项。
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            // 如果剪贴板为空，则禁用菜单的Paste选项。
            mPasteItem.setEnabled(false);
        }

        // 获取当前显示的笔记数量。
        final boolean haveItems = getListAdapter().getCount() > 0;

        // 如果列表中有任何笔记（这意味着其中一个被选中），
        // 那么我们需要生成可以在当前选择上执行的操作。
        // 这将是我们自己的特定操作以及可以找到的任何扩展的组合。
        if (haveItems) {

            // 这是选中的项目。
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            // 创建一个包含一个元素的Intent数组。这将用于根据选中的菜单项发送Intent。
            Intent[] specifics = new Intent[1];

            // 将数组中的Intent设置为对选中笔记URI的EDIT操作。
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);

            // 创建一个包含一个元素的菜单项数组。这将包含EDIT选项。
            MenuItem[] items = new MenuItem[1];

            // 创建一个没有特定操作的Intent，使用选中笔记的URI。
            Intent intent = new Intent(null, uri);

            /* 将ALTERNATIVE类别添加到Intent，将笔记ID URI作为其数据。
             * 这准备Intent作为在菜单中分组替代选项的位置。
             */
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

            /*
             * 将替代项添加到菜单
             */
            menu.addIntentOptions(
                Menu.CATEGORY_ALTERNATIVE,  // 将Intents作为替代组中的选项添加。
                Menu.NONE,                  // 不需要唯一的项目ID。
                Menu.NONE,                  // 替代项不需要排序。
                null,                       // 调用者名称不排除在组外。
                specifics,                  // 这些特定选项必须首先出现。
                intent,                     // 这些Intent对象映射到specifics中的选项。
                Menu.NONE,                  // 不需要标志。
                items                       // 从specifics到Intent映射生成的菜单项
            );
                // 如果Edit菜单项存在，为其添加快捷键。
                if (items[0] != null) {

                    // 将Edit菜单项快捷键设置为数字"1"，字母"e"
                    items[0].setShortcut('1', 'e');
                }
            } else {
                // 如果列表为空，从菜单中删除任何现有的替代操作
                menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
            }

        // 显示菜单
        return true;
    }

    /**
     * 当用户从菜单中选择选项但列表中没有选中项目时调用此方法。
     * 如果选项是INSERT，则发送一个带有ACTION_INSERT操作的新Intent。
     * 传入Intent的数据被放入新Intent中。实际上，这会触发NotePad应用程序中的NoteEditor活动。
     *
     * 如果项目不是INSERT，则很可能是来自另一个应用程序的替代选项。
     * 调用父方法处理该项目。
     * @param item 用户选中的菜单项
     * @return 如果选中了INSERT菜单项，返回True；否则返回调用父方法的结果。
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            /*
             * 使用Intent启动新Activity。Activity的intent filter
             * 必须具有ACTION_INSERT操作。未设置类别，因此假定为DEFAULT。
             * 实际上，这会启动NotePad中的NoteEditor Activity。
             */
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        } else if (id == R.id.menu_paste) {
            /*
             * 使用Intent启动新Activity。Activity的intent filter
             * 必须具有ACTION_PASTE操作。未设置类别，因此假定为DEFAULT。
             * 实际上，这会启动NotePad中的NoteEditor Activity。
             */
            startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
            return true;
        } else if (id == R.id.menu_settings) {
            // 启动设置活动
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 当用户在列表中上下文点击笔记时调用此方法。NotesList将自己注册为
     * 其ListView中上下文菜单的处理程序（这在onCreate()中完成）。
     *
     * 唯一可用的选项是COPY和DELETE。
     *
     * 上下文点击等同于长按。
     *
     * @param menu 应向其添加项目的ContextMenu对象。
     * @param view 正在为其构建上下文菜单的View。
     * @param menuInfo 与view关联的数据。
     * @throws ClassCastException
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // 来自菜单项的数据。
        AdapterView.AdapterContextMenuInfo info;

        // 尝试获取ListView中被长按的项目位置。
        try {
            // 将传入的数据对象转换为AdapterView对象的类型。
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // 如果菜单对象无法转换，记录错误。
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        /*
         * 获取与选中位置项目关联的数据。getItem()返回
         * ListView的支持适配器与该项目关联的任何数据。在NotesList中，
         * 适配器将笔记的所有数据与其列表项关联。因此，
         * getItem()将该数据作为Cursor返回。
         */
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);

        // 如果cursor为空，则由于某种原因适配器无法从provider获取数据，
        // 因此向调用者返回null。
        if (cursor == null) {
            // 由于某种原因，请求的项目不可用，不执行任何操作
            return;
        }

        // 从XML资源加载菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);

        // 将菜单标题设置为选中笔记的标题。
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // 附加到
        // 可以对其执行操作的其他活动的菜单项。
        // 这会在系统上查询实现我们数据的ALTERNATIVE_ACTION的任何活动，
        // 为找到的每个活动添加菜单项。
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), 
                                        Integer.toString((int) info.id) ));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
    }

    /**
     * 当用户从上下文菜单中选择项目时调用此方法
     * （参见onCreateContextMenu()）。实际处理的唯一菜单项是DELETE和
     * COPY。其他任何内容都是替代选项，应执行默认处理。
     *
     * @param item 选中的菜单项
     * @return 如果菜单项是DELETE且不需要默认处理，返回True；否则返回false，
     * 这会触发项目的默认处理。
     * @throws ClassCastException
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 来自菜单项的数据。
        AdapterView.AdapterContextMenuInfo info;

        /*
         * 从菜单项获取额外信息。当Notes列表中的笔记被长按时，
         * 会出现上下文菜单。菜单的菜单项自动获取与长按的笔记关联的数据。
         * 数据来自支持列表的provider。
         *
         * 笔记的数据在ContextMenuInfo对象中传递给上下文菜单创建例程。
         *
         * 当点击上下文菜单项之一时，相同的数据与笔记ID一起通过item参数
         * 传递给onContextItemSelected()。
         */
        try {
            // 将项目中的数据对象转换为AdapterView对象的类型。
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // 如果对象无法转换，记录错误
            Log.e(TAG, "bad menuInfo", e);

            // 触发菜单项的默认处理。
            return false;
        }
        // 将选中笔记的ID附加到与传入Intent一起发送的URI。
        Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);

        /*
         * 获取菜单项的ID并将其与已知操作进行比较。
         */
        int id = item.getItemId();
        if (id == R.id.context_open) {
            // 启动活动以查看/编辑当前选中的项目
            startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
            return true;
        } else if (id == R.id.context_copy) { //BEGIN_INCLUDE(copy)
            // 获取剪贴板服务的句柄。
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);

            // 将笔记URI复制到剪贴板。实际上，这会复制笔记本身
            clipboard.setPrimaryClip(ClipData.newUri(   // 保存URI的新剪贴板项目
                    getContentResolver(),               // 用于检索URI信息的resolver
                    "Note",                             // 剪贴项的标签
                    noteUri));                          // URI

            // 返回到调用者并跳过进一步处理。
            return true;
            //END_INCLUDE(copy)
        } else if (id == R.id.context_delete) {
            // 通过传入笔记ID格式的URI从provider删除笔记。
            // 请参阅关于在UI线程上执行provider操作的介绍性说明。
            getContentResolver().delete(
                    noteUri,  // provider的URI
                    null,     // 不需要where子句，因为只传入单个笔记ID。
                    null      // 不使用where子句，因此不需要where参数。
            );

            // 返回到调用者并跳过进一步处理。
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 当用户点击显示列表中的笔记时调用此方法。
     *
     * 此方法处理PICK（从provider获取数据）或
     * GET_CONTENT（获取或创建数据）的传入操作。如果传入操作是EDIT，此方法发送
     * 新Intent以启动NoteEditor。
     * @param l 包含被点击项目的ListView
     * @param v 单个项目的View
     * @param position v在显示列表中的位置
     * @param id 被点击项目的行ID
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // 从传入的URI和行ID构造新URI
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // 从传入的Intent获取操作
        String action = getIntent().getAction();

        // 处理笔记数据请求
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // 设置要返回到调用此Activity的组件的结果。
            // 结果包含新URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // 发送Intent以启动可以处理ACTION_EDIT的Activity。
            // Intent的数据是笔记ID URI。效果是调用NoteEdit。
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
