package com.lu.tool.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.tool.util.DimensionTools;
import com.lu.tool.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lu.basetool.R;


/**
 * 拍照的popWindow
 * Created by lqh on 2016/9/13.
 */
public class ChoosePicDialog extends Dialog {
    private static final int INTENT_CODE_CAMERA = 0x220;
    private static final int INTENT_CODE_ALBUM = 0x221;
    private static final int INTENT_CODE_CROP = 0x222;

    private Activity mActivity;
    private Fragment mFragment;
    private TextView mTakePhotoView, mCheckPhotoView;

    private boolean mCanTakePhoto;
    /**
     * --裁剪图片的目标大小--
     */
    private int mPicWidth, mPicHeight;

    private OnBitmapReturn mBitmapReturn;
    /**
     * 拍摄图片保存的路径
     */
    private Uri mCameraUri;
    private Uri mCropUri;

    public void setOnBitmapReturn(OnBitmapReturn bitmapReturn) {
        this.mBitmapReturn = bitmapReturn;
    }

    public ChoosePicDialog(Context context, Fragment fragment, int picWith, int picHeight) {
        super(context);
        this.mActivity = (Activity) context;
        this.mFragment = fragment;
        this.mPicWidth = picWith;
        this.mPicHeight = picHeight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (DimensionTools.getScreenWidth() * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(createContentView(), params);
        initListener();
    }

    private View createContentView() {
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.VERTICAL);
        mTakePhotoView = createButton("拍照");
        mCheckPhotoView = createButton("相册");

        contentView.addView(mTakePhotoView);

        View line = new View(getContext());
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(ResourceUtil.getColor(R.color.line_color));
        contentView.addView(line);

        contentView.addView(mCheckPhotoView);

        return contentView;
    }


    private TextView createButton(String text) {
        TextView view = new TextView(getContext());
        view.setGravity(Gravity.CENTER);
        view.setPadding(10, 20, 10, 20);
        view.setText(text);
        view.setTextSize(18);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return view;
    }

    private void initListener() {
        mCanTakePhoto = mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        /*--------拍照-----*/
        mTakePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromCamera();
            }
        });
        /*----从相册选取----*/
        mCheckPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromAlbum();
            }
        });
    }

    /**
     * 拍照
     */
    private void fromCamera() {
        if (!mCanTakePhoto) {
            Toast.makeText(mActivity, "当前手机不支持拍照", Toast.LENGTH_SHORT).show();
            return;
        }
        mCameraUri = createImageUri();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //小于7.0的版本
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
        } else {
            //大于7.0的版本
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mCameraUri.getPath());
            Uri uri = getImageContentUri(mCameraUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        if (intent.resolveActivity(mActivity.getPackageManager()) != null)
            if (mFragment == null)
                mActivity.startActivityForResult(intent, INTENT_CODE_CAMERA);
            else
                mFragment.startActivityForResult(intent, INTENT_CODE_CAMERA);
    }

    /**
     * 相册选取
     */
    private void fromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        if (intent.resolveActivity(mActivity.getPackageManager()) != null)
            if (mFragment == null)
                mActivity.startActivityForResult(intent, INTENT_CODE_ALBUM);
            else
                mFragment.startActivityForResult(intent, INTENT_CODE_ALBUM);
    }

    /**
     * 在对应的activity中的onActivityResult调用该方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBitmapReturn == null) return;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_CODE_CAMERA:
                    cropPic(mCameraUri, mPicWidth, mPicHeight);
                    break;
                case INTENT_CODE_ALBUM:
                    Uri uri = data.getData();
                    if (uri == null) {
                        mBitmapReturn.captureFailed("选取照片失败");
                    }
                    cropPic(uri, mPicWidth, mPicHeight);
                    break;
                case INTENT_CODE_CROP:
                    Log.d("ChoosePicDialog", mCropUri.getPath());
                    Bitmap bitmap = BitmapFactory.decodeFile(mCropUri.getPath());
                    if (bitmap == null) {
                        mBitmapReturn.captureFailed("裁剪照片失败");
                    } else {
                        mBitmapReturn.returnBitmap(bitmap, new File(mCropUri.getPath()));
                    }
                    dismiss();
                    break;
                default:
            }
        }
    }

    private void cropPic(Uri uri, int outputX, int outputY) {
        //创建用于保存的uri
        mCropUri = createImageUri();

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        /*进行裁剪*/
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        /*图片格式*/
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
        /*不通过intent返回   直接根据mImageFile取*/
        intent.putExtra("return-data", false);

        /*指定文件路径以及所访问的文件类型*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //小于7.0的版本
            intent.setDataAndType(uri, "image/*");
        } else {
            //大于7.0的版本
            String scheme = uri.getScheme();
            if (scheme.equals("content")) {
                intent.setDataAndType(uri, "image/*");
            } else {
                Uri contentUri = getImageContentUri(uri);
                intent.setDataAndType(contentUri, "image/*");
            }
        }

        if (mFragment == null) {
            mActivity.startActivityForResult(intent, INTENT_CODE_CROP);
        } else {
            mFragment.startActivityForResult(intent, INTENT_CODE_CROP);
        }
    }


    /**
     * 生成一个图片文件，默认保存在根目录下的应用包名文件夹下
     *
     * @return
     */
    public Uri createImageUri() {
        try {
            File file = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file = new File(Environment.getExternalStorageDirectory(), mActivity.getPackageName());
                if (!file.exists()) {
                    file.mkdirs();
                }
            } else {
                file = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }

            File imageFile = new File(file, createFileName());
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String createFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'IMG'_MM_dd_HHmmss", Locale.CHINA);
        String str = sdf.format(date) + ".jpg";
        return str;
    }


    /**
     * 调用系统的扫描器将图片添加到媒体扫描器的数据库中，
     * 使得这些照片可以被系统的相册应用或者其他app访问
     */
    private void galleryAddPic(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        mediaScanIntent.setData(uri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    /**
     * 压缩图片的方法
     */
    private void zoomPic(ImageView view, String filePath) {
        float targetW = view.getWidth();
        float targetH = view.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int photoW = options.outWidth;
        int photoH = options.outHeight;


        float scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) scaleFactor;
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        view.setImageBitmap(bm);

    }

    /**
     * 转换 content:// uri
     */
    public Uri getImageContentUri(Uri uri) {
        String filePath = uri.getPath();
        Cursor cursor = mActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return mActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /**
     * 返回裁剪后的图片
     */
    public interface OnBitmapReturn {
        void returnBitmap(Bitmap bm, File file);

        void captureFailed(String msg);
    }

}
