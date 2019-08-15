package russianapp.yk.kz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public class mainmenu extends Activity {

	Context MainActivity;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mainmenu);

		MainActivity = this;

		View Add = (View) findViewById(R.id.ads);
		Add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 Intent myIntent = new Intent(mainmenu.this,
				 MainActivity.class);
				 myIntent.putExtra("key", "000"); //Optional parameters
				 mainmenu.this.startActivity(myIntent);
			}
		});

		View News = (View) findViewById(R.id.news);
		News.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(View arg0) {
				Intent intent = getPackageManager().getLaunchIntentForPackage("russianapp.news.yk.kz");
				
				if (intent != null)
				{
					Intent intent1 = new Intent();
					intent1.setAction(Intent.ACTION_VIEW);
					intent1.setClassName("russianapp.news.yk.kz", "russianapp.news.yk.kz.MainActivity");
					startActivity(intent1);
				}
				else
				{
//				    // bring user to the market
//				    // or let them choose an app?
//				    intent = new Intent(Intent.ACTION_VIEW);
//				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				    intent.setData(Uri.parse("market://details?id="+"russianapp.news.yk.kz"));
//				    startActivity(intent);

					Toast.makeText(MainActivity,
							"Приложение новости Yk.kz доступно для скачивания в магазине приложений Google Play",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
