package fi.harism.curl;

/**
 * Created by zhenian on 2016/12/28.
 */

public class ItemData{
    private int resId;
    private String name;
    public ItemData(int resId, String name){
        this.resId = resId;
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }
}