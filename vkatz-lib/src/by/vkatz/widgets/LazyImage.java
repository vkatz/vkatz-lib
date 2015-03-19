package by.vkatz.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import by.vkatz.utils.SelfKillerExecutor;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Katz.<br>
 * <br>
 * Usage<br>
 * Cache will be stored in {@link android.content.Context#getExternalCacheDir()}
 * <br>
 * Cache data(info about cached images) will be stored as CacheData.sr in {@link android.content.Context#getExternalFilesDir(String) Context.getExternalFilesDir("")}
 * <br>
 * <br>
 * {@link by.vkatz.widgets.LazyImage} contain a bunch of static methods witch provide access to cache.<br>
 * <br>
 * <font color='red'>Be careful - system may delete cached files without notify app, always check file for exist before action.<font/>
 */
@SuppressWarnings("unused")
public class LazyImage extends ImageView {
    private static final Object synk = new Object();
    private static HashMap<String, String> cache;
    private static Executor executor = new SelfKillerExecutor();
    private OnImageLoadListener onImageLoadListener;
    private Options options;

    public LazyImage(Context context) {
        super(context);
    }

    public LazyImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LazyImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnImageLoadListener(OnImageLoadListener onImageLoadListener) {
        this.onImageLoadListener = onImageLoadListener;
    }

    private String optionsToKey(Options options) {
        return "" + options.cacheWidth + "x" + options.cacheHeight + "|" + options.url;
    }

    public void setImage(String url) {
        setImage(Options.create(url));
    }

    public void setImage(final Options options) {
        setImageBitmap(null);
        if (onImageLoadListener != null) onImageLoadListener.onLoadingStart(this, options.url);
        loadCacheData(getContext());
        this.options = options;
        executor.execute(new Runnable() {
            @Override
            @SuppressWarnings("ResultOfMethodCallIgnored")
            public void run() {
                String file = null;
                boolean success = false;
                synchronized (synk) {
                    if (cache.containsKey(optionsToKey(options))) file = cache.get(optionsToKey(options));
                }
                if (file != null && new File(file).exists()) {
                    final Bitmap bitmap = scaleBitmap(BitmapFactory.decodeFile(file), options.width, options.height, true);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (LazyImage.this.options == options) {
                                if (onImageLoadListener != null) onImageLoadListener.onLoadingComplete(LazyImage.this, options.url, bitmap);
                                setImageBitmap(bitmap);
                            }
                        }
                    });
                    return;
                } else synchronized (synk) {
                    cache.remove(options.url);
                }
                try {
                    Bitmap cacheBitmap = scaleBitmap(BitmapFactory.decodeStream(new URL(options.url).openStream()), options.cacheWidth, options.cacheHeight, true);
                    File folder = options.getCacheDir() == null ? getDefaultCacheFolder(getContext()) : options.getCacheDir();
                    folder.mkdirs();
                    File cacheFile = File.createTempFile("li-", ".png", folder);
                    cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(cacheFile));
                    final Bitmap showBitmap = scaleBitmap(cacheBitmap, options.width, options.height, true);
                    String filename = cacheFile.getAbsolutePath();
                    synchronized (synk) {
                        cache.put(optionsToKey(options), filename);
                        saveCacheData(getContext());
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (LazyImage.this.options == options) {
                                setImageBitmap(showBitmap);
                                if (onImageLoadListener != null)
                                    onImageLoadListener.onLoadingComplete(LazyImage.this, options.url, showBitmap);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (LazyImage.this.options == options && onImageLoadListener != null)
                                onImageLoadListener.onLoadingComplete(LazyImage.this, options.url, null);
                        }
                    });
                }
            }
        });

    }

    public Options getCurrentOptions() {
        return options;
    }

    private static Bitmap scaleBitmap(Bitmap in, int outWidth, int outHeight, boolean rececleOld) {
        if (outHeight <= 0 && outWidth <= 0) return in;
        float scale;
        if (outWidth <= 0 && outHeight > 0) scale = 1f * outHeight / in.getHeight();
        else if (outWidth > 0 && outHeight <= 0) scale = 1f * outWidth / in.getWidth();
        else scale = Math.min(1f * outWidth / in.getWidth(), 1f * outHeight / in.getHeight());
        Bitmap result = Bitmap.createScaledBitmap(in, (int) (in.getWidth() * scale), (int) (in.getHeight() * scale), true);
        if (rececleOld && result != in) in.recycle();
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void loadCacheData(Context context) {
        synchronized (synk) {
            if (cache == null) try {
                cache = (HashMap<String, String>) new ObjectInputStream(new FileInputStream(new File(context.getExternalFilesDir(""), "CacheData.sr"))).readObject();
            } catch (Exception e) {
                e.printStackTrace();
                cache = new HashMap<>();
            }
        }
    }

    public static void saveCacheData(Context context) {
        synchronized (synk) {
            try {
                new ObjectOutputStream(new FileOutputStream(new File(context.getExternalFilesDir(""), "CacheData.sr"))).writeObject(cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File getDefaultCacheFolder(Context context) {
        return context.getExternalCacheDir();
    }

    public static ArrayList<Pair<String, File>> getCachedFiles(Context context) {
        ArrayList<Pair<String, File>> files = new ArrayList<>();
        loadCacheData(context);
        synchronized (synk) {
            for (Map.Entry<String, String> i : cache.entrySet())
                files.add(new Pair<>(i.getKey(), new File(i.getValue())));
        }
        return files;
    }

    public static interface OnImageLoadListener {
        public void onLoadingStart(LazyImage imageView, String url);

        public void onLoadingComplete(LazyImage imageView, String url, Bitmap bitmap);
    }

    public static class Options {
        private String url;
        private int width, height;
        private int cacheWidth, cacheHeight;
        private File cacheDir;

        private Options(String url) {
            width = -1;
            height = -1;
            cacheWidth = -1;
            cacheHeight = -1;
            cacheDir = null;
            this.url = url;
        }

        public Options setUrl(String url) {
            this.url = url;
            return this;
        }

        public Options setWidth(int width) {
            this.width = width;
            return this;
        }

        public Options setHeight(int height) {
            this.height = height;
            return this;
        }

        public Options setCacheWidth(int cacheWidth) {
            this.cacheWidth = cacheWidth;
            return this;
        }

        public Options setCacheHeight(int cacheHeight) {
            this.cacheHeight = cacheHeight;
            return this;
        }

        public Options setCacheDir(File dir) {
            cacheDir = dir;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getCacheWidth() {
            return cacheWidth;
        }

        public int getCacheHeight() {
            return cacheHeight;
        }

        public File getCacheDir() {
            return cacheDir;
        }

        public static Options create(String url) {
            return new Options(url);
        }
    }
}
