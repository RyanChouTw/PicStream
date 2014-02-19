package com.happypig.picstream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.format.Time;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ryanchou on 2013/6/26.
 */
public class Utils {
    private final Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruImageCache  mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;

    private static Utils instance = null;
    protected Utils (Context context) {
        mContext = context;        
        new InitDiskCacheTask().execute();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);        
        final int cacheSize = maxMemory / 8;
        
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getRowBytes() / 1024;
            }
        };        
    }

    public static Utils getInstance(Context context) {
        if (instance == null)
            instance = new Utils(context);
        return instance;
    }    
    
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch (Exception e) {
            // nothing
        }finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }    
    
    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... files) {
            synchronized (mDiskCacheLock) {
                mDiskLruCache = new DiskLruImageCache(mContext,
                                        CommonDef.DISK_CACHE_SUBDIR,
                                        CommonDef.DISK_CACHE_SIZE,
                                        CommonDef.BITMAP_COMPRESS_FORMAT,
                                        CommonDef.BITMAP_COMPRESS_QUALITY);
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }    	
    }
    
    public Bitmap getBitmapFromMemCache(String key) {
    	return mMemoryCache.get(key);
    }
    
    public void addBitmapToCache(String key, Bitmap bitmap) {
    	// Add to memory cache as before
    	if (getBitmapFromMemCache(key) == null) {
    		mMemoryCache.put(key, bitmap);
    	}
    	
        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && mDiskLruCache.getBitmap(key) == null) {
                mDiskLruCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromCache(String key) {
    	Bitmap retBmp = getBitmapFromMemCache(key);
    	if(retBmp != null)
    		return retBmp;
    	
    	return getBitmapFromDiskCache(key);
    }
    
    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
                return mDiskLruCache.getBitmap(key);
            }
        }
        return null;
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    
    public static Bitmap getBitmapFromAsset(Context context, String strName) throws IOException
    {
        AssetManager assetManager = context.getAssets();

        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }
    
    public static Bitmap getBitmap(Context context, Uri imgUri, int reqWidth, int reqHeight) {
        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imgUri), null, bitmapOption);
            bitmapOption.inSampleSize = calculateInSampleSize(bitmapOption, reqWidth, reqHeight);
            bitmapOption.inJustDecodeBounds = false;        	
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imgUri), null, bitmapOption);
        } catch (IOException e) {
        	// nothing
        	return null;
        }        
    }
    public static Bitmap cropImage(Bitmap baseImage,int width,int height){
    	Matrix matrix = new Matrix();
    	matrix.reset();
    	Rect cropArea = new Rect();
    	
        if(baseImage==null){
        	throw new RuntimeException("baseImage is null");
        }

        int dw=width;
        int dh=height;
        int wscaled, hscaled;
        
        
        if(dw!=0 && dh!=0 ){
			float waspect=(float)dw/baseImage.getWidth();
			float haspect=(float)dh/baseImage.getHeight();
			if(waspect>haspect){
				matrix.postScale(waspect, waspect);
				
				wscaled = (int) (baseImage.getWidth()*waspect);
				hscaled = (int) (baseImage.getHeight()*waspect);
				cropArea.left = 0;
				cropArea.top = (int)(((hscaled - dh) / 2) / waspect);
				cropArea.right = (int) (cropArea.left + dw/waspect);
				cropArea.bottom = (int) (cropArea.top + dh/waspect);				
			}else{
				matrix.postScale(haspect, haspect);
				
				wscaled = (int) (baseImage.getWidth() *haspect);
				hscaled = (int) (baseImage.getHeight()*haspect);
				cropArea.left = (int) (((wscaled - dw) / 2)/haspect);
				cropArea.top = 0;
				cropArea.right = (int) (cropArea.left + (dw/haspect));
				cropArea.bottom = (int) (cropArea.top + (dh/haspect)); 							
			}
        }        
        
        Bitmap croppedBitmap = Bitmap.createBitmap(baseImage, cropArea.left, cropArea.top, cropArea.width(), cropArea.height(), matrix, true);
        
        return croppedBitmap;
    }     
    
    public static Bitmap cropImage(ImageLayoutDescription info) {
       	Matrix matrix = new Matrix();
    	matrix.reset();
    	Rect cropArea = new Rect();
    	Bitmap baseImage = info.getBitmap();
        if(baseImage==null){
        	throw new RuntimeException("baseImage is null");
        }

        int dw=info.mWidth;
        int dh=info.mHeight;
        int wscaled, hscaled;
        
        
        if(dw!=0 && dh!=0 ){
			float waspect=(float)dw/baseImage.getWidth();
			float haspect=(float)dh/baseImage.getHeight();
			if(waspect>haspect){
				matrix.postScale(waspect, waspect);
				
				wscaled = (int) (baseImage.getWidth()*waspect);
				hscaled = (int) (baseImage.getHeight()*waspect);
				cropArea.left = 0;
				cropArea.top = (int)(((hscaled - dh) / 2) / waspect) + info.mOffset;
				cropArea.right = (int) (cropArea.left + dw/waspect);
				cropArea.bottom = (int) (cropArea.top + dh/waspect);
				info.mAllowMoveDirect = CommonDef.IMAGE_EDITOR_MOVE_VERT;
				info.mOffsetMax = (int)(((hscaled - dh) / 2) / waspect);
			}else{
				matrix.postScale(haspect, haspect);
				
				wscaled = (int) (baseImage.getWidth() *haspect);
				hscaled = (int) (baseImage.getHeight()*haspect);
				cropArea.left = (int) (((wscaled - dw) / 2)/haspect) + info.mOffset;
				cropArea.top = 0;
				cropArea.right = (int) (cropArea.left + (dw/haspect));
				cropArea.bottom = (int) (cropArea.top + (dh/haspect)); 
				info.mAllowMoveDirect = CommonDef.IMAGE_EDITOR_MOVE_HORZ;
				info.mOffsetMax = (int) (((wscaled - dw) / 2)/haspect);
			}
        }        
        
        Bitmap croppedBitmap = Bitmap.createBitmap(baseImage, cropArea.left, cropArea.top, cropArea.width(), cropArea.height(), matrix, true);
        
        return croppedBitmap;
    }
    
    public static Bitmap cropImageWithBorder(ImageLayoutDescription info, Paint borderPaint) {
    	Bitmap cropBmp = cropImage(info);
    	Bitmap retBmp = Bitmap.createBitmap((int)(cropBmp.getWidth()+2*borderPaint.getStrokeWidth()), (int)(cropBmp.getHeight()+2*borderPaint.getStrokeWidth()), cropBmp.getConfig());
    	Canvas canvas = new Canvas(cropBmp);    	
    	canvas.drawColor(borderPaint.getColor());
    	canvas.drawBitmap(cropBmp, null,  new RectF(borderPaint.getStrokeWidth(), borderPaint.getStrokeWidth(), cropBmp.getWidth()+borderPaint.getStrokeWidth(), cropBmp.getHeight()+borderPaint.getStrokeWidth()), null);    	
    	
    	return retBmp;
    }
    
    public static Bitmap cropSquareImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
          source.recycle();
        }
        return squaredBitmap;    	
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
     
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;
     
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
     
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
     
        return output;
    }
    
    
    public static File getAppRootDir() {
        File root = Environment.getExternalStorageDirectory();
        return new File(root.getAbsolutePath() + File.separator + "PicStream");
    	
    }
        
    public static Uri saveViewContentToImage(View view, 
    										String filename, 
    										int x, int y, int width, int height) {
    	File outDir = getAppRootDir();
    	
        Uri imageFileUri = null;

        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        
    	// create bitmap screen capture
    	Bitmap bitmap;    	
    	Matrix scaleMatrix = new Matrix();
    	view.setDrawingCacheEnabled(true);
    	scaleMatrix.postScale(CommonDef.COVER_IMAGE_SCALE_RATIO, CommonDef.COVER_IMAGE_SCALE_RATIO);
    	bitmap = Bitmap.createBitmap(view.getDrawingCache(), x, y, width, height, scaleMatrix, true);
    	view.setDrawingCacheEnabled(false);

    	OutputStream fout = null;
    	File imageFile = new File(outDir, filename);

    	try {
    	    fout = new FileOutputStream(imageFile);
    	    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
    	    fout.flush();
    	    fout.close();
    	    imageFileUri = Uri.fromFile(imageFile);    	    

    	} catch (FileNotFoundException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	}
    	
    	return imageFileUri;
    }
    
    public static Uri writeStringToFile(String fileName, String data) {        
        File outDir = getAppRootDir();
        Uri fileUri = null;

        if (!outDir.isDirectory()) {
          outDir.mkdir();
        }
        
        try {
			if (!outDir.isDirectory()) {
				throw new IOException("Unable to create directory PicStream. Maybe the SD card is mounted?");
			}

			File outputFile = new File(outDir, fileName);
  
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(data);

			writer.close();
			fileUri = Uri.fromFile(outputFile);
        } catch (IOException e) {
        	// Nothing
        }
        return fileUri;
    }
    
    public static String readStringFromFile(Uri fileUri) {    	
        InputStream iStream = null;        
        String data = "";

        try {
        	String filePath = fileUri.getPath();
            iStream = (InputStream) new FileInputStream(new File(filePath));
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            data = sb.toString();
            iStream.close();                                 
        } catch (Exception e) {
            // nothing
        }   
        
        return data;
    }
    
    public static String getUniqueFileName() {
    	Time now = new Time();
    	now.setToNow();    	
    	
    	return now.format2445();
    }   
    
}