package com.anybeen.mark.imageeditor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;


import com.anybeen.mark.imageeditor.entity.ProgressItem;
import com.anybeen.mark.imageeditor.view.MovableTextView2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by maidou on 2016/3/16.
 */
public class CommonUtils {

    public static void closeKeyboard(MovableTextView2 mtv, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(mtv.getWindowToken(), 0);
    }

    public static void closeKeyboard(EditText et, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static void closeKeyboard(View view, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * @details 当键盘打开的时候，点击则关闭 && 当键盘关闭的时候，点击则打开
     */
    public static void hitKeyboardOpenOrNot(Context context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


//    public static int listenKeyboard(final View view) {
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                view.getWindowVisibleDisplayFrame(r);
//                int visibleHeight = r.height();
//                if (mVisibleHeight == 0) {
//                    mVisibleHeight = visibleHeight;
//                    return;
//                }
//                if (mVisibleHeight == visibleHeight) {
//                    return;
//                }
//                return visibleHeight;
//                System.out.println("mVisibleHeight-----:" + mVisibleHeight);
//            }
//        });
//        return 0;
//    }

    public static int matchProgress(int color, Context context) {
        int ret = 0;
        for (int i = 0; i < Const.COLOR_VALUES.length ; i++) {
            if (color == context.getResources().getColor(Const.COLOR_VALUES[i])) {
                ret = i;
            }
        }
        return ret * 10 + 1;    // + 5
    }

    public static int matchedColor(int seekBarProgress) {
        int ret = seekBarProgress / 10;
        return ret;
    }


    /**
     * 获取配置文件键值对<p>用法：getFontConfigProperties(fileName).getProperty("key");</p>
     * @param propertyFileName 配置文件名
     * @param context context
     * @return Properties
     */
    public static Properties getFontConfigProperties(File propertyFileName,Context context) {
        Properties props = new Properties();
        try {
            FileInputStream s = new FileInputStream(propertyFileName);
            props.load(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFiles2Assets(Context context,String oldPath,String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFiles2Assets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断网络连接类型，只有在3G或wifi里进行数据发送
     */
    public static boolean isNetAvailable(Context context){
        return checkNet(context);
    }
    public static boolean checkNet(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {

                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("==>" + e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * @details 通过字体名称获取字体
     * @param fontName
     * @return
     */
    public static Typeface getTypeface(String fontName) {
        Typeface typeface = null;
        if (fontName.equals("default")) {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        } else {
            File file = new File(Const.FONT_PATH + File.separator + fontName);
            if (file.exists()) {
                try {
                    typeface = Typeface.createFromFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return typeface;
    }

    /**
     * 获取颜色进度
     * @param colorValues
     * @return
     */
    public static ArrayList<ProgressItem> getProgressItemList(int[] colorValues) {
        ArrayList<ProgressItem> progressItemList;
        ProgressItem mProgressItem;
        float totalSpan = 9;        // 颜色个数
        float[] colorSpan = Const.COLOR_SPAN;
        progressItemList = new ArrayList<>();
        for (int i = 0; i < colorSpan.length; i++) {
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((colorSpan[i] / totalSpan) * 100);
            mProgressItem.color = colorValues[i];
            progressItemList.add(mProgressItem);
        }
        return progressItemList;
    }


    public static float[] calcScaleAndLeaveSize(Bitmap copyBitmap, ImageView iv_main_image) {
        float[] scaleAndLeaveSize = new float[3];
        float svWidth = iv_main_image.getWidth() * 1.0f;
        float svHeight = iv_main_image.getHeight() * 1.0f;
        float copyBitWidth = copyBitmap.getWidth() * 1.0f;
        float copyBitHeight = copyBitmap.getHeight() * 1.0f;

        float scaleX = copyBitWidth / svWidth;
        float scaleY = copyBitHeight / svHeight;
        float scale = scaleX > scaleY ? scaleX:scaleY;
        float leaveW = 0.0f, leaveH = 0.0f;     // 留白区域
        if (scaleX > scaleY) {
            leaveH = (svHeight -  copyBitHeight / scale) / 2;
        } else {
            leaveW = (svWidth - copyBitWidth / scale) / 2;
        }
        scaleAndLeaveSize[0] = scale;       // 表示图片与屏幕的缩放比
        scaleAndLeaveSize[1] = leaveW;      // 表示图片的X轴留白区域
        scaleAndLeaveSize[2] = leaveH;      // 表示图片的Y轴留白区域
        return scaleAndLeaveSize;
    }

    /**
     * @details 四色五入的方式转换 float 值为 int 值
     * @param f
     * @return int
     */
    public static int floatToInt(float f){
        int i;
        if(f>0) //正数
            i = (int) ((f*10 + 5)/10);
        else if(f<0) //负数
            i = (int) ((f*10 - 5)/10);
        else i = 0;
        return i;
    }

    /**
     * @details 转 float 为 double
     * @param f
     * @return double
     */
    public static double float2Double(float f) {
        return Double.parseDouble(String.valueOf(f));
    }

    /**
     * @details 转 double 为 float
     * @param d
     * @return float
     */
    public static float double2Float(double d) {
        return (float)d;
    }
}
