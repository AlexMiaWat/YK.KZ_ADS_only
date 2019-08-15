package russianapp.yk.kz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

public class ImageActivity extends Activity {

	String purl;
	String filename;

	Boolean SaveLikePage;

	int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);

		// admob
		AdView mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		// Вывод изображения на WebView
		WebView wv = this.findViewById(R.id.webView1);

		String[] params = getIntent().getExtras().getStringArray("params");

		purl = params[0];
		int wd = Integer.parseInt(params[1]);

		if (params[2].length() > 0) {
			wv.loadUrl(purl);
			SaveLikePage = true;
		} else {
			SaveLikePage = false;
			String data = "<html><head><style type='text/css' vertical-align='middle'>body{margin:auto;text-align:center;background:white} img{width:100%25;} </style></head>"
					+ "<body><center><img width=\""
					+ wd
					+ "\" src=\""
					+ purl
					+ "\" /><center><body><html>";
			wv.loadData(data, "text/html", null);
		}
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setUseWideViewPort(true);

		// Ширина и высота изображений кнопок
		int iconSize = Methods.GetMetrics(getApplicationContext(),2);

        // Кнопка back
        Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.back);
        Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal, iconSize,
                iconSize, false);
        ImageButton bb = findViewById(R.id.imageButton2);
        bb.setImageBitmap(bmHalf);

        bb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            ImageButton sb = findViewById(R.id.imageButton1);
            sb.setVisibility(View.GONE);
        }
		// Кнопка Сохранить
		bmOriginal = BitmapFactory.decodeResource(getResources(),
				R.drawable.save1);
		bmHalf = Bitmap.createScaledBitmap(bmOriginal, iconSize,
				iconSize, false);
		ImageButton sb = findViewById(R.id.imageButton1);
		sb.setImageBitmap(bmHalf);

		sb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(ImageActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ImageActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(ImageActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    // filename = SaveImgOntoSD();
                    SaveToSD stsd = new SaveToSD();
                    int downloadedSize = 0;
                    try {
                        downloadedSize = stsd.execute(purl).get();
                    } catch (InterruptedException e) {
                        Toast.makeText(ImageActivity.this, "Провал!",
                                Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        Toast.makeText(ImageActivity.this, "Провал!",
                                Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(
                            ImageActivity.this,
                            "Файл сохранен размер: " + downloadedSize / 1024
                                    + " Kb", Toast.LENGTH_SHORT).show();
                }

			}
		});

	}

	// Создаем изображение на SD CARD
	private class SaveToSD extends AsyncTask<String, Void, Integer> {

		protected Integer doInBackground(String... arg0) {
			URL url;
			int downloadedSize = 0;
			try {
				url = new URL(purl);

				File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                        File.separator + "YK.KZ Folder");
                if (!dir.exists()) {
                    dir.mkdir();
                }

				String uuid = UUID.randomUUID().toString();
				String filename = "YK.KZ " + uuid + (!SaveLikePage ? ".png" : ".jpg");

				File file = new File(dir.getAbsolutePath(), filename);

                //If you're writing the file to a specific location on the SD card, try using Environment variables.
                //They should always point to a valid location. Here's an example to write to the downloads folder
//                java.io.File file = new java.io.File(Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                        + "/Filename.xml");

//                //If you're writing the file to the application's internal storage. Try this example:
//                java.io.File file = new java.io.File((ImageActivity.this
//                        .getApplicationContext().getFileStreamPath(filename)
//                        .getPath()));
//
//                //Personally I rely on external libraries to handle the streaming to file. This one hasn't failed me yet.
//                org.apache.commons.io.FileUtils.copyInputStreamToFile(is, file);

                //Функция внешняя
                //File file = createImageFile();

//                File storageDir = new File(Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/YK.KZ Folder/");
//                if (!storageDir.exists())
//                    storageDir.mkdirs();
//                File file = new File(dir.getAbsolutePath(), filename);

                FileOutputStream fileOutput = new FileOutputStream(file);

				//////////////////////////////////////

				if (!SaveLikePage) {

					InputStream inputStream = url.openStream();

					byte[] buffer = new byte[1024];
					int bufferLength = 0;
					while ((bufferLength = inputStream.read(buffer, 0,
							buffer.length)) >= 0) {
						fileOutput.write(buffer, 0, bufferLength);
						downloadedSize += bufferLength;
					}
					fileOutput.close();
					inputStream.close();
				} else {
					try {
						WebView wv = (WebView) findViewById(R.id.webView1);

						wv.setDrawingCacheEnabled(true);
						Bitmap bmap = Bitmap.createBitmap(wv.getDrawingCache());
						wv.setDrawingCacheEnabled(false);

						bmap.compress(Bitmap.CompressFormat.JPEG, 90,
								fileOutput);

						//!// Чему равен этот размер?
						downloadedSize = (int) file.length();
						fileOutput.flush();
						fileOutput.close();
					} catch (Exception e) {
						Toast.makeText(ImageActivity.this, e.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}

				// Обновить в галерее
                refreshGallery(dir.getPath(), ImageActivity.this);

			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}

			return downloadedSize;
		}
	}

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(System.currentTimeMillis());
        File storageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/YK.KZ Folder/");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                timeStamp,                   /* prefix */
                ".jpg",                /* suffix */
                storageDir                   /* directory */
        );
        return image;
    }

    private static void refreshGallery(String mCurrentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

	// меню
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_image, menu);
		return true;
	}
}