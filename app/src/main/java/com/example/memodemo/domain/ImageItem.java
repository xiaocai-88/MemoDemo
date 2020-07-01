package com.example.memodemo.domain;

/**
 * description 图片的bean类，获取到的图片信息保存在此处
 * create by xiaocai on 2020/6/29
 */
public class ImageItem {
    private String path;

    @Override
    public String toString() {
        return "ImageItem{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                '}';
    }

    public ImageItem(String path, String title, long date) {
        this.path = path;
        this.title = title;
        this.date = date;
    }

    private String title;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    protected long date;
}
