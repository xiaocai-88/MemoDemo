package com.example.memodemo;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memodemo.Adapter.ImageViewAdapter;
import com.example.memodemo.Utils.ImagePickerConfig;
import com.example.memodemo.domain.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends AppCompatActivity {

    private static final String TAG = "PickerActivity";
    private static final int LOADER_ID = 1;
    private List<ImageItem> mItemlist = new ArrayList<>();
    private ImageView mBackBtn;
    private TextView mFinishTv;
    private RecyclerView mImageList;
    private ImageViewAdapter mImageViewAdapter;
    private ImagePickerConfig mImagePickerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        initLoadManager();
        initView();
        initEvent();
        initConfig();
    }

    private void initConfig() {
        mImagePickerConfig = ImagePickerConfig.getInstance();
        int maxSelectedCount = mImagePickerConfig.getMaxSelectedCount();
        mImageViewAdapter.setMaxSelectedCount(maxSelectedCount);
    }

    private void initEvent() {
        //返回按钮被点击
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //确定选择的图片
        mFinishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到选择的数据，通知显示的界面，结束当前界面
                List<ImageItem> selectedResult = new ArrayList<>();
                selectedResult.addAll(mImageViewAdapter.getSelectedItem());
                mImageViewAdapter.reSetDate();
                ImagePickerConfig.OnImageSelectedFinishLisenter onImageSelectedFinishLisenter = mImagePickerConfig.getOnImageSelectedFinishLisenter();
                if (onImageSelectedFinishLisenter != null) {
                    onImageSelectedFinishLisenter.onSelectedFinish(selectedResult);
                }
                //
                finish();
            }
        });

        //所选的张数发生改变
        mImageViewAdapter.setOnItemSelectedChangeListener(new ImageViewAdapter.OnItemSelectedChangeListener() {
            @Override
            public void onItemSelectedChange(List<ImageItem> SelectedItem) {
                //所选择的数据发生了变化
                mFinishTv.setText("(" + SelectedItem.size() + "/" + mImageViewAdapter.getMaxSelectedCount() + ")已选择");
            }
        });
    }

    private void initView() {
        mBackBtn = findViewById(R.id.back_btn);
        mFinishTv = findViewById(R.id.finishTv);
        mImageList = findViewById(R.id.image_list_view);
        //
        mImageViewAdapter = new ImageViewAdapter();
        mImageList.setAdapter(mImageViewAdapter);
        mImageList.setLayoutManager(new GridLayoutManager(this, 3));
        //设置间距
        mImageList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 6;
                outRect.bottom = 6;
            }
        });
    }

    /**
     * 使用loadmanager来加载图片内容
     */
    private void initLoadManager() {
        //先清空,再将数据放进去
        mItemlist.clear();
        //初始化loaderManager
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                if (id == LOADER_ID) {
                    return new CursorLoader(PickerActivity.this,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{"_data", "_display_name", "date_added"},
                            null, null, "date_added DESC");

                }
                return null;
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    //将每个字段打印出来
                    String[] columnNames = cursor.getColumnNames();
                    while (cursor.moveToNext()) {
                        Log.d(TAG, "=========================");
                        for (String columnName : columnNames) {
                            Log.d(TAG, columnName + " is ======" + cursor.getString(cursor.getColumnIndex(columnName)));
                        }
                        String path = cursor.getString(0);
                        String title = cursor.getString(1);
                        long date = cursor.getLong(2);
                        ImageItem imageItem = new ImageItem(path, title, date);
                        mItemlist.add(imageItem);
                    }
                    cursor.close();
                    for (ImageItem imageItem : mItemlist) {
                        Log.d(TAG, "image ---->" + imageItem.toString());
                    }
                    //将获取到的数据设置给适配器
                    mImageViewAdapter.setDate(mItemlist);
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }


        });
    }
}
