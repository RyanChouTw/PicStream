package com.happypig.picstream;

import android.graphics.Bitmap;

/**
 * Created by ryanchou on 2013/6/21.
 */
final class CommonDef {
    final static int APP_VERSION = 1;
    final static int VALUE_COUNT = 1;
    final static int NUM_URLS_INIT = 100;
    final static int MAX_NUM_PAGES = 1024;
    
    final static int LOADER_ID_LOCAL_IMAGES = 0;

    final static int BYTES_PER_KB = 1024;
    final static int KB_PER_MB = 1024;

    final static int IO_BUFFER_SIZE = 8 * BYTES_PER_KB;
    final static int DISK_CACHE_SIZE =  20 * KB_PER_MB * BYTES_PER_KB; // 10MB

    final static String DISK_CACHE_SUBDIR = "thumbnails";
    final static Bitmap.CompressFormat BITMAP_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    final static int BITMAP_COMPRESS_QUALITY = 70;

    final static int NUM_COLUMN_IN_GRID = 3;

    final static int IMG_SOURCE_CATEGORY_SD = 0;
    final static int IMG_SOURCE_CATEGORY_FACEBOOK = 1;
    final static int IMG_SOURCE_CATEGORY_GOOGLE_PLUS = 2;
    	    
    final static int ACTION_CODE_PICK_GALLERY_IMAGE = 1000;
    
    final static String EXTRA_TAG_PICSTREAM_GALLERY_MODE = "ExtraTagPicStreamGalleryMode";
	final static int PICSTREAM_GALLERY_MODE_FIRST_ENTRY = 0;
	final static int PICSTREAM_GALLERY_MODE_ADD = 1;

	final static String EXTRA_TAG_PASS_ACTION = "ExtraTagPassAction";
    final static String ACTION_START_PICSTREAM_LIST = "Action_StartPicStream";    
    
    final static String EXTRA_TAG_PASS_FB_USER_ID = "ExtraTagPassFbUserId";
    final static String EXTRA_TAG_PASS_ARRAYLIST = "ExtraTagPassArrayList";   
    final static String EXTRA_TAG_PASS_STREAM_URI = "ExtraTagPassStreamUri";
    final static String EXTRA_TAG_PASS_STREAM_NAME = "ExtraTagPassStreamName";
    final static String EXTRA_TAG_PASS_COVER_IMG_URI = "ExtraTagPassCoverImgUri";
    
    final static String EXTRA_TAG_PICSTREAM_EDITOR_MODE = "ExtraTagPicStreamEditorMode";
    final static int PICSTREAM_EDITOR_MODE_NEW = 0;
    final static int PICSTREAM_EDITOR_MODE_EDITOR = 1;
    final static int PICSTREAM_EDITOR_MODE_VIEWER = 2;
        
    final static int PAGE_SLIDE_ANIMATION_DURATION	= 1000; 
    
    // Template related paramters
    final static String PATH_TO_TEMPLATES = "template";
    final static String PATH_TO_BACKGROUND = "background";
    final static String PATH_TO_FOREGROUND = "frame";
    final static int MAX_NUM_IMAGES_IN_PAGE = 4;
    
    final static String JSON_TEMPLATE_TAG_PAGE_BACKGROUND_IMG = "background_image";
    final static String JSON_TEMPLATE_TAG_PAGE_BACKGROUND_COLOR = "background_color";
    final static String JSON_TEMPLATE_TAG_PAGE_FOREGROUND_IMG = "foreground_image";
    final static String JSON_TEMPLATE_TAG_PAGE_NUM_IMAGES = "num_images";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGES = "images";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION = "position";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION_X = "x_coord";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION_Y = "y_coord";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_ROTATION = "rotate";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_WIDTH = "width";
    final static String JSON_TEMPLATE_TAG_PAGE_IMAGE_HEIGHT = "height";
    
    final static String JSON_PIC_STREAM_TAG_NUM_PAGES = "num_pages";
    final static String JSON_PIC_STREAM_TAG_PAGES = "pages";
    final static String JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_IMG = "background_image";
    final static String JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_COLOR = "background_color";
    final static String JSON_PIC_STREAM_TAG_PAGE_FOREGROUND_IMG = "foreground_image";
    final static String JSON_PIC_STREAM_TAG_PAGE_NUM_IMAGES = "num_images";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGES = "images";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_ID = "id";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_URL = "url";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_CATEGORY = "category";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_X = "x_coord";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_Y = "y_coord";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_ROTATION = "rotate";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_WIDTH = "width";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_HEIGHT = "height";
    final static String JSON_PIC_STREAM_TAG_PAGE_IMAGE_OFFSET = "offset";
    
    final static String PICSTREAM_STORE = "picStreamStore.json";
    final static String JSON_PIC_STREAM_STORE_TAG_NUM_STREAMS = "num_streams";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAMS = "streams";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAM_ID = "id";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAM_NAME = "disp_name";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAM_DESCRIPTION = "desc";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAM_COVER_IMG = "cover_img";
    final static String JSON_PIC_STREAM_STORE_TAG_STREAM_URL = "url";
    
    
    // Slide behavior parameters
    final static int SWIPE_MIN_DISTANCE = 120;
    final static int SWIPE_MAX_OFF_PATH = 250;
    final static int SWIPE_THRESHOLD_VELOCITY = 400;
    
    // ImageEditor Paint Area
    final static int IMAGE_EDITOR_WIDTH = 720;
    final static int IMAGE_EDITOR_HEIGHT = 1080;
    
    final static int IMAGE_EDITOR_MOVE_VERT = 0;
    final static int IMAGE_EDITOR_MOVE_HORZ = 1;
    
    final static float COVER_IMAGE_SCALE_RATIO = (float) 0.3;
    
    
    final static String FILE_EXTENSION_PICTURE_STREAM = ".json";
    final static String FILE_EXTENSION_COVER_IMAGE = ".png";
}
