package com.example.memodemo.domain;

/**
 * description 备忘录id, title , body 的bean类
 * create by xiaocai on 2020/6/11
 */
public class Record {

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", titleName='" + titleName + '\'' +
                ", textBody='" + textBody + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", getTipsChecked=" + isTipsChecked +
                '}';
    }

    private Integer id;
    private String titleName;
    private String textBody;
    private String createTime;
    private String modifyTime;

    public boolean getTipsChecked() {
        return isTipsChecked;
    }

    public void setTipsChecked(boolean tipsChecked) {
        isTipsChecked = tipsChecked;
    }


    private boolean isTipsChecked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
