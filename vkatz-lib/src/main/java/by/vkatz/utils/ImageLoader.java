package by.vkatz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Katz on 15.06.2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageLoader {
    private File cacheInfoFile;
    private File cacheFolder;
    //worker
    private SelfKillerExecutor worker = new SelfKillerExecutor();
    private Handler handler = new Handler();
    private HashMap<String, String> data = new HashMap<>();//map url->file
    private HashMap<ImageView, Options> loadings = new HashMap<>();

    public ImageLoader(Context context) {
        this(context, null);
    }

    public ImageLoader(Context context, File cacheInfoFile) {
        this(context, cacheInfoFile, null);
    }

    public ImageLoader(Context context, File cacheInfoFile, File cacheFolder) {
        this.cacheFolder = cacheFolder != null ? cacheFolder : context.getExternalCacheDir();
        this.cacheInfoFile = cacheInfoFile != null ? cacheInfoFile : new File(cacheFolder, "imagesCacheData.st");
    }

    public void load(ImageView image, String url) {
        load(image, new Options(url));
    }

    public void load(final ImageView imageView, final Options options) {
        synchronized (this) {
            loadings.put(imageView, options);
        }
        loadData();
        worker.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap image = null;
                //try to load from file, in case it in cache table
                synchronized (ImageLoader.this) {
                    if (data != null && data.containsKey(options.url))
                        image = BitmapFactory.decodeFile(data.get(options.url));
                }
                boolean fromFile = image != null;
                //load image from web in case not in cache
                if (image == null) {
                    InputStream inputStream = null;
                    if (options.streamProvider != null) inputStream = options.streamProvider.execute(options.url);
                    else try {
                        inputStream = new URL(Uri.encode(options.url, "@#&=*+-_.,:!?()/~'%")).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (inputStream != null) image = BitmapFactory.decodeStream(inputStream);
                    //process image once (on first time loaded
                    if (image != null && options.processBitmapOnce != null) {
                        Bitmap tmp = options.processBitmapOnce.execute(imageView, image, options);
                        if (tmp != image) { // in case result not the same image - recycle old
                            image.recycle();
                            image = tmp;
                        }
                    }
                }
                //process image each time it loading
                if (image != null && options.processBitmap != null) {
                    Bitmap tmp = options.processBitmap.execute(imageView, image, options);
                    if (tmp != image) { // in case result not the same image - recycle old
                        image.recycle();
                        image = tmp;
                    }
                }
                //validate image
                if (image == null) {
                    if (options.onFailed != null) options.onFailed.execute(imageView, options);
                    return;
                }

                if (!fromFile) {
                    try {
                        File file = File.createTempFile("lazy-", ".png", cacheFolder);
                        image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                        synchronized (ImageLoader.this) {
                            data.put(options.url, file.getAbsolutePath());
                        }
                        saveData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (options.onFailed != null)
                            options.onFailed.execute(imageView, options);
                        return;
                    }
                }
                //apply in case it actual
                final Bitmap finalImage = image;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (ImageLoader.this) {
                            if (loadings.get(imageView) == options) {
                                imageView.setImageBitmap(finalImage);
                                loadings.remove(imageView);
                            }
                        }
                    }
                });
                if (options.onSuccess != null)
                    options.onSuccess.execute(imageView, image, options);

            }
        });
    }

    public void clearCache() {
        loadData();
        worker.execute(new Runnable() {
            @Override
            @SuppressWarnings("ResultOfMethodCallIgnored")
            public void run() {
                synchronized (ImageLoader.this) {
                    for (String item : data.values()) {
                        try {
                            new File(item).delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    saveData();
                }
            }
        });
    }

    private void saveData() {
        synchronized (this) {
            if (data != null)
                SerializableUtils.commit(cacheInfoFile, data);
        }
    }

    private void loadData() {
        synchronized (this) {
            if (data == null)
                data = SerializableUtils.restore(cacheInfoFile, new Functions.Func0<HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> execute() {
                        return new HashMap<>();
                    }
                });
        }
    }

    public static class Options {
        private String url;
        private Functions.Func1<InputStream, String> streamProvider;
        private Functions.Func2<Void, ImageView, Options> onFailed;
        private Functions.Func3<Void, ImageView, Bitmap, Options> onSuccess;
        private Functions.Func3<Bitmap, ImageView, Bitmap, Options> processBitmapOnce, processBitmap;

        public Options(String url) {
            this.url = url;
        }

        public Options setStreamProvider(Functions.Func1<InputStream, String> streamProvider) {
            this.streamProvider = streamProvider;
            return this;
        }

        public Options setOnFailed(Functions.Func2<Void, ImageView, Options> onFailed) {
            this.onFailed = onFailed;
            return this;
        }

        public Options setOnSuccess(Functions.Func3<Void, ImageView, Bitmap, Options> onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        public Options setProcessBitmapOnce(Functions.Func3<Bitmap, ImageView, Bitmap, Options> processBitmapOnce) {
            this.processBitmapOnce = processBitmapOnce;
            return this;
        }

        public Options setProcessBitmap(Functions.Func3<Bitmap, ImageView, Bitmap, Options> processBitmap) {
            this.processBitmap = processBitmap;
            return this;
        }
    }
}
