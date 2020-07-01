package com.example.memodemo.Utils;

import com.example.memodemo.domain.ImageItem;

import java.util.List;

/**
 * description 方便外部来设置数据（可选照片的最大值）的接口
 * 单例模式统一入口
 * create by xiaocai on 2020/6/29
 */
public class ImagePickerConfig {
    //单例模式
    private ImagePickerConfig() {
    }

    private static ImagePickerConfig sImagePickerConfig;

    public static ImagePickerConfig getInstance() {
        if (sImagePickerConfig == null) {
            sImagePickerConfig = new ImagePickerConfig();
        }
        return sImagePickerConfig;
    }

    //设置可选的最大值
    private int maxSelectedCount = 1;

    public int getMaxSelectedCount() {
        return maxSelectedCount;
    }

    public void setMaxSelectedCount(int maxSelectedCount) {
        this.maxSelectedCount = maxSelectedCount;
    }

    //对外暴露方法，让外部设置获取到图片内容，从而加载图片
    //在选择界面set，在展示界面get
    public interface OnImageSelectedFinishLisenter {
        void onSelectedFinish(List<ImageItem> selectedResult);
    }

    public static ImagePickerConfig getImagePickerConfig() {
        return sImagePickerConfig;
    }

    public static void setImagePickerConfig(ImagePickerConfig imagePickerConfig) {
        sImagePickerConfig = imagePickerConfig;
    }

    //
    private OnImageSelectedFinishLisenter onImageSelectedFinishLisenter = null;

    public OnImageSelectedFinishLisenter getOnImageSelectedFinishLisenter() {
        return onImageSelectedFinishLisenter;
    }

    public void setOnImageSelectedFinishLisenter(OnImageSelectedFinishLisenter onImageSelectedFinishLisenter) {
        this.onImageSelectedFinishLisenter = onImageSelectedFinishLisenter;
    }

    //
    private List<ImageItem> selectResult;

    public List<ImageItem> getSelectResult() {
        return selectResult;
    }

    public void setSelectResult(List<ImageItem> selectResult) {
        this.selectResult = selectResult;
    }

    //
    private int seletcedSize;

    public int getSeletcedSize() {
        return seletcedSize;
    }

    public void setSeletcedSize(int seletcedSize) {
        this.seletcedSize = seletcedSize;
    }
}
