package russianapp.yk.kz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.htmlcleaner.TagNode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

	private ArrayList<ItemDetails> results;
	private ArrayList<CategoriesStructure> categories;

	private ProgressDialog pd;
	int progressChanged = 0;
	int lastPageSelectted; // Last page selected
	int CategorySelected = -1; // Выбраный раздел

	public Boolean everythingisok;

	String[] data = new String[36];

	SeekBar pagesontrol;
	String Path = "";
	String SearchString = "";
	Drawable dwItem;
	int ItemSelected;
	int dwItemS;
	String[] phonesPieces = { "тут пусто" }; // массив номеров телефонов
	String[] emailPieces; // массив email адресов
	String[] httpPieces; // массив email адресов
	public int halfWidth; // ширина изображения в текущем сеансе
	public int halfWidth0; // ширина изображения в текущем сеансе

	private Mail m;

	// Делаем при загрузке приложения
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-9163301852390874~6490908748");

		// Отправка обратной связи
//		try {
//			GMailSender sender = new GMailSender("studio36bagels@gmail.com",
//					"vghrt");
//			sender.sendMail("This is Subject", "This is Body",
//					"studio36bagels@gmail.com", "avoronov@mail.ru");
//			Toast.makeText(this, "Email was sent successfully.",
//					Toast.LENGTH_LONG).show();
//		} catch (Exception e) {
//			Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG)
//					.show();
//		}

        // admob
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Ширина и высота изображений кнопок и изображений
        halfWidth = Methods.GetMetrics(getApplicationContext(),1);
        halfWidth0= Methods.GetMetrics(getApplicationContext(),0);

		ListView listview = findViewById(R.id.listViewData);
		listview.setClickable(true);
		listview.setOnItemClickListener(onItemClick);

		// создали прокрутку
		pagesontrol = findViewById(R.id.pageBox);
		pagesontrol.setOnSeekBarChangeListener(seekBarChange);

		// Кнопки смены страницы
		Button l_change = findViewById(R.id.l_change);
		l_change.setOnClickListener(l_Listener);
		Button r_change = findViewById(R.id.R_change);
		r_change.setOnClickListener(r_Listener);

		// logo
		Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
				R.drawable.logo1);
		Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
				halfWidth0, false);
		ImageView logo = findViewById(R.id.logo1);
		logo.setImageBitmap(bmHalf);

		//!// Нет действия для ЛОГО
		// logo.setOnClickListener(sbListen);

		// Кнопка поиска
		bmOriginal = BitmapFactory
				.decodeResource(getResources(), R.drawable.sl);
		bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0, halfWidth0,
				false);
		ImageButton sb = findViewById(R.id.imageButton1);
		sb.setImageBitmap(bmHalf);
		sb.setOnClickListener(sbListen);

		Button reConnect = findViewById(R.id.reConnect);
		reConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				everythingisok = isNetworkConnected();

				if (((ListView) findViewById(R.id.listViewData)).getCount() == 0) {
					if (everythingisok) {
						// Запуск процесса получения разделов
						pd = ProgressDialog.show(MainActivity.this,
								"Загрузка разделов...", "Обрашение к серверу",
								true, false);
						new ParseCategories().execute("https://yk.kz/announce");
					}
				} else {
					final Spinner spinner = findViewById(R.id.Categories);
					spinner.setSelection(CategorySelected);
					// spinner.setSelected(true);
				}

			}
		});

		Button workOFFline = findViewById(R.id.workOFFline);
		workOFFline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				everythingisok = EveryIsOk();

				View TopOfAds = findViewById(R.id.TopOfAds);
				TopOfAds.setVisibility(View.VISIBLE);
				View ListOfAds = findViewById(R.id.ListOfAds);
				ListOfAds.setVisibility(View.VISIBLE);
				View ButtonOfAds = findViewById(R.id.ButtonOfAds);
				ButtonOfAds.setVisibility(View.VISIBLE);

				View connectTab = findViewById(R.id.connectTab);
				connectTab.setVisibility(View.GONE);
			}
		});

		// Проверка подключения
		everythingisok = EveryIsOk();

		if (everythingisok) {
			// Запуск процесса получения разделов
			pd = ProgressDialog.show(MainActivity.this, "Загрузка разделов...",
					"Обрашение к серверу", true, false);
			new ParseCategories().execute("https://yk.kz/announce");
		}
	}

	// Проверка подключения
	public Boolean EveryIsOk() {
		Boolean resAvalable = executeCmd("ping -c 1 -w 1 yk.kz/announce", false);
		Boolean isConnected = isNetworkConnected();

		if (!resAvalable)
			Toast.makeText(MainActivity.this,
					"Сайт YK.KZ не отвечает. Попробуйте позже.",
					Toast.LENGTH_LONG).show();

		if (!isConnected)
			Toast.makeText(
					MainActivity.this,
					"Нет соединения с интернетом. Проверьте в настройках устройства.",
					Toast.LENGTH_LONG).show();

		// from:
        //		if (resAvalable && isConnected)
        //			everythingisok = true;
        //		else
        //			everythingisok = false;
		// simplified to :
        everythingisok =(resAvalable && isConnected);

		// Таб переподкдлючения
		if (!everythingisok) {
			View TopOfAds = findViewById(R.id.TopOfAds);
			TopOfAds.setVisibility(View.GONE);
			View ListOfAds = findViewById(R.id.ListOfAds);
			ListOfAds.setVisibility(View.GONE);
			View ButtonOfAds = findViewById(R.id.ButtonOfAds);
			ButtonOfAds.setVisibility(View.GONE);

			View connectTab = findViewById(R.id.connectTab);
			connectTab.setVisibility(View.VISIBLE);
		} else {
			View TopOfAds = findViewById(R.id.TopOfAds);
			TopOfAds.setVisibility(View.VISIBLE);
			View ListOfAds = findViewById(R.id.ListOfAds);
			ListOfAds.setVisibility(View.VISIBLE);
			View ButtonOfAds = findViewById(R.id.ButtonOfAds);
			ButtonOfAds.setVisibility(View.VISIBLE);

			View connectTab = findViewById(R.id.connectTab);
			connectTab.setVisibility(View.GONE);
		}

        // from:
        //		if (resAvalable && isConnected)
        //			everythingisok = true;
        //		else
        //			everythingisok = false;
        // simplified to :
        everythingisok =(resAvalable && isConnected);

		return everythingisok;
	}

	// Пропингуем сайт
	public static Boolean executeCmd(String cmd, boolean sudo) {
		try {

			Process p;
			if (!sudo)
				p = Runtime.getRuntime().exec(cmd);
			else {
				p = Runtime.getRuntime().exec(new String[] { "su", "-c", cmd });
			}
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			p.destroy();
			// return res;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	// Проверка соединения с интернетом
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	// Прокрутили полосу страниц или выбрали значение
	public OnSeekBarChangeListener seekBarChange = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			TextView pages = (TextView) findViewById(R.id.pageCount);

			progressChanged = progress;
			pages.setText(Integer.toString(seekBar.getProgress() + 1));
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			lastPageSelectted = progressChanged;
			TextView pages = (TextView) findViewById(R.id.pageCount);
			pages.setText(Integer.toString(seekBar.getProgress() + 1));
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			if (!isNetworkConnected()) {
				TextView pages = (TextView) findViewById(R.id.pageCount);
				pages.setText(Integer.toString(lastPageSelectted + 1));
				final Spinner spinner = (Spinner) findViewById(R.id.Categories);
				spinner.setSelection(lastPageSelectted);
				return;
			}
//			Toast.makeText(MainActivity.this,
//					"Выбрана страница: " + (progressChanged + 1),
//					Toast.LENGTH_SHORT).show();

//			 Запускаем диалог загрузки
			pd = ProgressDialog.show(MainActivity.this,
					categories.get(CategorySelected).name, "Загрузка страницы: " + (progressChanged + 1), true, false);

			if (SearchString.length() > 0) {
				Path = "https://yk.kz" + categories.get(
						// spinner.getSelectedItemPosition()
						CategorySelected).path + "/"
						+ Integer.toString(seekBar.getProgress() + 1) + "/"
						+ SearchString;
			} else {
				Path = "https://yk.kz" + categories.get(
						// spinner.getSelectedItemPosition()
						CategorySelected).path + "/"
						+ Integer.toString(seekBar.getProgress() + 1);
			}
			new ParseSite().execute(Path);
		}
	};

	// "Слушаем" кнупку http
	public android.content.DialogInterface.OnClickListener phoneClickListenerHTTP = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int indx) {
			if (!isNetworkConnected())
				return;

			Toast.makeText(MainActivity.this,
					"Открывается: " + httpPieces[indx], Toast.LENGTH_SHORT)
					.show();

			String url = httpPieces[indx];

			Intent myIntent = new Intent(MainActivity.this, ImageActivity.class);

			myIntent.putExtra("params", new String[] { url, "0", "url" });

			int requestCode = 0;
			MainActivity.this.startActivityForResult(myIntent, requestCode);
		}
	};

	// "Слушаем" кнопку звонка
	public android.content.DialogInterface.OnClickListener phoneClickListener = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int indx) {
			Toast.makeText(MainActivity.this, "Вызов: " + phonesPieces[indx],
					Toast.LENGTH_SHORT).show();
			Intent dial = new Intent();
			dial.setAction("android.intent.action.DIAL");
			dial.setData(Uri.parse("tel:" + phonesPieces[indx]));
			startActivity(dial);
		}
	};

	// "Слушаем" кнопку SMS
	public android.content.DialogInterface.OnClickListener phoneClickListenerSMS = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int indx) {
			Toast.makeText(MainActivity.this,
					"Отправка смс на: " + phonesPieces[indx],
					Toast.LENGTH_SHORT).show();
			Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
					+ phonesPieces[indx]));
			sms.putExtra("sms_body", "");
			startActivity(sms);
		}
	};

	// "Слушаем" кнопку email
	public android.content.DialogInterface.OnClickListener phoneClickListenerEmail = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int indx) {
			Toast.makeText(MainActivity.this,
					"Отправка письма на: " + emailPieces[indx],
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, emailPieces);
			i.putExtra(Intent.EXTRA_SUBJECT, "Отклик на объявление");
			i.putExtra(
					Intent.EXTRA_TEXT,
					"Отклик на объявление с сайта YK.KZ: "
							+ results.get(ItemSelected).getItemDescription());
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(
						MainActivity.this,
						"На вашем устройстве нет установленных почтовых клиентов.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// "Слушаем" элемент списка
	public OnItemClickListener onItemClick = new OnItemClickListener() {

		// Нажали на элемент списка
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this).setTitle(results.get(position).getDate()
			);
			final FrameLayout frameView = new FrameLayout(MainActivity.this);
			builder.setView(frameView);

			final AlertDialog alertD = builder.create();
			LayoutInflater inflater = alertD.getLayoutInflater();

			View dialogDescription = inflater.inflate(
					R.layout.item_description, frameView);

			// Вывод изображения в ImageView
			ImageView image = (ImageView) dialogDescription
					.findViewById(R.id.image);
			DrawItem di = new DrawItem();
			di.execute(results.get(position).getImage());

			try {
				image.setImageDrawable(di.get());
			} catch (InterruptedException e) {

			} catch (ExecutionException e) {
				Bitmap bmOriginal = BitmapFactory.decodeResource(
						getResources(), R.drawable.noimg);
				Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal,
						halfWidth, halfWidth, false);
				image.setImageDrawable(new BitmapDrawable(bmHalf));
				dwItemS = 0;
			}

			ItemSelected = position;

			// Вывод изображения в map
			ImageButton map = (ImageButton) dialogDescription
					.findViewById(R.id.map);
			Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.map);
			Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			map.setImageBitmap(bmHalf);

			// Вывод изображения в call
			ImageButton call = (ImageButton) dialogDescription
					.findViewById(R.id.call);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.call);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			call.setImageBitmap(bmHalf);

			TextView phone = (TextView) dialogDescription
					.findViewById(R.id.phone);
			phone.setText(results.get(position).getPhone());

			// Обработчик кнопки call
			call.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					List<String> phones = new ArrayList<String>();

					String getPhone = results.get(ItemSelected).getPhone();
					getPhone = getPhone.replace(";", ",").replace(".", ",")
							.replace("+7", "8");
					phonesPieces = getPhone.split(",");
					for (String s : phonesPieces) {
						Pattern p = Pattern.compile("\\d+");
						Matcher m = p.matcher(s);
						s = "";
						while (m.find())
							s = s + m.group();
						if (s.length() > 5) {
							if (s.length() == 6)
								s = "8 (7232) " + s.substring(0, 3) + "-"
										+ s.substring(3, 6);
							else if (s.length() == 11)
								s = "8 (" + s.substring(1, 4) + ") "
										+ s.substring(4, 7) + "-"
										+ s.substring(7, 9) + "-"
										+ s.substring(9, 11);
							else if (s.length() == 10)
								s = "8 (" + s.substring(0, 3) + ") "
										+ s.substring(3, 6) + "-"
										+ s.substring(6, 8) + "-"
										+ s.substring(8, 10);
							if (Integer.parseInt(s.substring(0, 1)) == 8)
								s = "+7" + s.substring(1);
							phones.add(s);
						} else {
							// phones.add("тут пусто");
						}
					}

					AlertDialog.Builder adb = new AlertDialog.Builder(
							MainActivity.this);
					adb.setTitle("Выберите номер для звонка:");

					HashSet<String> hs = new HashSet<String>();
					hs.addAll(phones);
					phones.clear();
					phones.addAll(hs);

					phonesPieces = phones.toArray(new String[phones.size()]);
					adb.setItems(phonesPieces, phoneClickListener);

					if (phones.size() > 0)
						adb.show();
					else
						Toast.makeText(MainActivity.this,
								"Номера телефона не обнаружено",
								Toast.LENGTH_SHORT).show();
				}

			});

			// Вывод изображения в sms
			ImageButton sms = (ImageButton) dialogDescription
					.findViewById(R.id.text);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.sms);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			sms.setImageBitmap(bmHalf);

			// Обработчик кнопки sms
			sms.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					List<String> phones = new ArrayList<String>();
					// List<String> realPhones = new ArrayList<String>();

					String getPhone = results.get(ItemSelected).getPhone();
					getPhone = getPhone.replace(";", ",").replace(".", ",")
							.replace("+7", "8");
					phonesPieces = getPhone.split(",");
					for (String s : phonesPieces) {
						Pattern p = Pattern.compile("\\d+");
						Matcher m = p.matcher(s);
						s = "";
						while (m.find())
							s = s + m.group();
						if (s.length() > 5) {
							if (s.length() == 6)
								s = "8 (7232) " + s.substring(0, 3) + "-"
										+ s.substring(3, 6);
							else if (s.length() == 11)
								s = "8 (" + s.substring(1, 4) + ") "
										+ s.substring(4, 7) + "-"
										+ s.substring(7, 9) + "-"
										+ s.substring(9, 11);
							else if (s.length() == 10)
								s = "8 (" + s.substring(0, 3) + ") "
										+ s.substring(3, 6) + "-"
										+ s.substring(6, 8) + "-"
										+ s.substring(8, 10);
							if (Integer.parseInt(s.substring(0, 1)) == 8)
								s = "+7" + s.substring(1);
							phones.add(s);
						} else {
							// phones.add("тут пусто");
						}
					}

					AlertDialog.Builder adb = new AlertDialog.Builder(
							MainActivity.this);
					adb.setTitle("Выберите номер для сообщения:");

					HashSet<String> hs = new HashSet<String>();
					hs.addAll(phones);
					phones.clear();
					phones.addAll(hs);

					phonesPieces = phones.toArray(new String[phones.size()]);
					adb.setItems(phonesPieces, phoneClickListenerSMS);

					if (phones.size() > 0)
						adb.show();
					else
						Toast.makeText(MainActivity.this,
								"Номера телефона не обнаружено",
								Toast.LENGTH_SHORT).show();
				}

			});

			// Вывод изображения в email
			ImageButton email = (ImageButton) dialogDescription
					.findViewById(R.id.email);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.email);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			email.setImageBitmap(bmHalf);

			// Обработчик кнопки email
			email.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					List<String> phones = new ArrayList<String>();

					String getPhone = results.get(ItemSelected).getPhone()
							+ " "
							+ results.get(ItemSelected).getItemDescription();
					getPhone = getPhone.replace(" .", ".").replace(". ", ".");

					String EMAIL_PATTERN = "(([a-zA-Z][-\\w.]*)@[\\w[.]]*\\.+([a-z]+))";
					// Пока что это лучшее рег. выражение для email
					// String EMAIL_PATTERN =
					// "^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$";

					emailPieces = getPhone.split(" ");
					for (String s : emailPieces) {
						Pattern p = Pattern.compile(EMAIL_PATTERN);
						Matcher m = p.matcher(s);
						while (m.find()) {
							phones.add(m.group());
						}
					}
					AlertDialog.Builder adb = new AlertDialog.Builder(
							MainActivity.this);
					adb.setTitle("Выберите адрес почты:");

					HashSet<String> hs = new HashSet<String>();
					hs.addAll(phones);
					phones.clear();
					phones.addAll(hs);

					emailPieces = phones.toArray(new String[phones.size()]);
					adb.setItems(emailPieces, phoneClickListenerEmail);

					if (phones.size() > 0)
						adb.show();
					else
						Toast.makeText(MainActivity.this,
								"Электронной почты не обнаружено",
								Toast.LENGTH_SHORT).show();
				}
			});

			// Вывод изображения в http web link
			ImageButton http = (ImageButton) dialogDescription
					.findViewById(R.id.web);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.http);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			http.setImageBitmap(bmHalf);

			// Обработчик кнопки http
			http.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					List<String> http = new ArrayList<String>();

					String getPhone = results.get(ItemSelected)
							.getItemDescription();
					getPhone = getPhone.replace(". ", ".");
					// String HTTP_PATTERN =
					// "(https=//)?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w[a-zA-Z_0-9\\-]$@]+)?";
					String HTTP_PATTERN = "(((f|ht){1}tp://)[-a-zA-Z0-9@:%_\\+.~#?&//=]+)";
					String HTTPS_PATTERN = "(((f|ht){1}tps://)[-a-zA-Z0-9@:%_\\+.~#?&//=]+)";
					// Удивительная регулярка, лучшая из всех для интернет ссылок

					Pattern p = Pattern.compile(HTTP_PATTERN);
					Matcher m = p.matcher(getPhone);
					while (m.find()) {
						String tmpS = m.group().trim();
						// tmpS = tmpS.replaceAll(".$", "");
						if (tmpS.substring(tmpS.length() - 1).codePointAt(0) == "."
								.codePointAt(0))
							tmpS = tmpS.substring(0, tmpS.length() - 1);
						http.add(tmpS);
					}

                    p = Pattern.compile(HTTPS_PATTERN);
                    m = p.matcher(getPhone);
                    while (m.find()) {
                        String tmpS = m.group().trim();
                        // tmpS = tmpS.replaceAll(".$", "");
                        if (tmpS.substring(tmpS.length() - 1).codePointAt(0) == "."
                                .codePointAt(0))
                            tmpS = tmpS.substring(0, tmpS.length() - 1);
                        http.add(tmpS);
                    }

					AlertDialog.Builder adb = new AlertDialog.Builder(
							MainActivity.this);
					adb.setTitle("Выберите веб ссылку:");

					HashSet<String> hs = new HashSet<String>();
					hs.addAll(http);
					http.clear();
					http.addAll(hs);

					httpPieces = http.toArray(new String[http.size()]);
					adb.setItems(httpPieces, phoneClickListenerHTTP);

					if (http.size() > 0)
						adb.show();
					else
						Toast.makeText(MainActivity.this,
								"Интернет ссылок не обнаружено",
								Toast.LENGTH_SHORT).show();
				}
			});

			// Вывод изображения в share
			ImageButton share = (ImageButton) dialogDescription
					.findViewById(R.id.share);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.share_this_icon);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			share.setImageBitmap(bmHalf);

			share.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Intent shareIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(
							android.content.Intent.EXTRA_SUBJECT,
							"Объявление с YK.KZ mobile от: "
									+ results.get(ItemSelected).getDate());
					String shareMessage = ""
							+ results.get(ItemSelected).getItemDescription()
							+ " " + results.get(ItemSelected).getPhone()
							+ " Цена: " + results.get(ItemSelected).getPrice();
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
							shareMessage);
					startActivity(Intent.createChooser(shareIntent,
							"Публикация объявления"));
				}
			});

			// Вывод изображения в appeal
			ImageButton appeal = (ImageButton) dialogDescription
					.findViewById(R.id.appeal);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.no);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			appeal.setImageBitmap(bmHalf);

			// Вывод изображения в favorite
			ImageButton favorite = (ImageButton) dialogDescription
					.findViewById(R.id.favorite);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.star_off);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			favorite.setImageBitmap(bmHalf);

			// Вывод изображения в back
			ImageButton back = (ImageButton) dialogDescription
					.findViewById(R.id.back);
			bmOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.back);
			bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth0,
					halfWidth0, false);
			back.setImageBitmap(bmHalf);

			TextView desc = (TextView) dialogDescription
					.findViewById(R.id.description);
			desc.setText(results.get(position).getItemDescription());

			TextView price = (TextView) dialogDescription
					.findViewById(R.id.price);
			price.setText(results.get(position).getPrice());

			if (dwItemS != 0)
				image.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (!isNetworkConnected())
							return;

						Display display = getWindowManager()
								.getDefaultDisplay();
						int wd = display.getWidth();
						String url = results.get(ItemSelected).getFullImage();

						Intent myIntent = new Intent(MainActivity.this,
								ImageActivity.class);

						myIntent.putExtra("params",
								new String[] { url, String.valueOf(wd), "" });

						int requestCode = 0;
						MainActivity.this.startActivityForResult(myIntent,
								requestCode);
					}
				});

			alertD.show();
		}
	};

	// Рисуем эскиз
	private class DrawItem extends AsyncTask<Drawable, Void, Drawable> {

		@SuppressWarnings("deprecation")
		@Override
		protected Drawable doInBackground(Drawable... arg0) {
			if (arg0[0] == null) {
				Bitmap bmOriginal = BitmapFactory.decodeResource(
						getResources(), R.drawable.noimg);
				int halfHeight = halfWidth;
				Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal,
						halfHeight, halfHeight, false);
				dwItem = new BitmapDrawable(bmHalf);
				dwItemS = 0;
			} else {
				dwItem = arg0[0];
				dwItemS = 1;
			}
			return dwItem;
		}
	}

	// Кнопка "Поиска" нажатие
	private OnClickListener sbListen = new OnClickListener() {
		public void onClick(View v) {
			if (!isNetworkConnected())
				return;
			AlertDialog.Builder alert = new AlertDialog.Builder(
					MainActivity.this);

			final Spinner spinner = (Spinner) findViewById(R.id.Categories);
			alert.setTitle(spinner.getSelectedItem().toString());
			alert.setMessage("Введите текст для поиска...");

			// Set an EditText view to get user input
			final EditText input = new EditText(MainActivity.this);
			input.setText(SearchString);
			alert.setView(input);

			alert.setPositiveButton("Поиск",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
											int whichButton) {
							Editable value = input.getText();
							if (value.toString() != "") {
								pd = ProgressDialog.show(MainActivity.this,
										spinner.getSelectedItem().toString(),
										"Поиск: " + value, true, false);
								progressChanged = 0;
								pagesontrol.setProgress(progressChanged);

								SearchString = value.toString();

								// Меняем кодировку для русского поиска
								String valueUTF8 = "";
								try {
//									valueUTF8 = new String(SearchString
//											.getBytes(), "ISO-8859-1");
                                    valueUTF8 = URLEncoder.encode(SearchString, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									valueUTF8 = SearchString;
								}

								Path = "https://yk.kz"
										+ categories.get(spinner
										.getSelectedItemPosition()).path
										+ "/1/" + valueUTF8;
								new ParseSite().execute(Path);
							}
						}
					});

			alert.setNegativeButton("Очистить",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
											int whichButton) {
							SearchString = "";
						}
					});

			alert.show();
		}
	};

	// Кликаем влево (страницы)
	private OnClickListener l_Listener = new OnClickListener() {
		public void onClick(View v) {
			if (!isNetworkConnected())
				return;
			progressChanged = pagesontrol.getProgress();
			if (progressChanged > 0) {
				progressChanged--;
				pagesontrol.setProgress(progressChanged);

//				Toast.makeText(MainActivity.this,
//						"Выбрана страница: " + (progressChanged + 1),
//						Toast.LENGTH_SHORT).show();

//			 Запускаем диалог загрузки
                pd = ProgressDialog.show(MainActivity.this,
                        categories.get(CategorySelected).name, "Загрузка страницы: " + (progressChanged + 1), true, false);

				if (SearchString.length() > 0) {
					Path = "https://yk.kz" + categories.get(
							// spinner.getSelectedItemPosition()
							CategorySelected).path + "/"
							+ Integer.toString(progressChanged + 1) + "/"
							+ SearchString;
					new ParseSite().execute(Path);
				} else {
					Path = "https://yk.kz" + categories.get(
							// spinner.getSelectedItemPosition()
							CategorySelected).path + "/"
							+ Integer.toString(progressChanged + 1);
					new ParseSite().execute(Path);
				}
			}
		}
	};

	// Кликаем вправо (страницы)
	private OnClickListener r_Listener = new OnClickListener() {
		public void onClick(View v) {
			if (!isNetworkConnected())
				return;
			progressChanged = pagesontrol.getProgress();
			if (progressChanged < pagesontrol.getMax()) {
				progressChanged++;
				pagesontrol.setProgress(progressChanged);

//				Toast.makeText(MainActivity.this,
//						"Выбрана страница: " + (progressChanged + 1),
//						Toast.LENGTH_SHORT).show();

//			 Запускаем диалог загрузки
                pd = ProgressDialog.show(MainActivity.this,
                        categories.get(CategorySelected).name, "Загрузка страницы: " + (progressChanged + 1), true, false);

				if (SearchString.length() > 0) {
					Path = "https://yk.kz" + categories.get(
							// spinner.getSelectedItemPosition()
							CategorySelected).path + "/"
							+ Integer.toString(progressChanged + 1) + "/"
							+ SearchString;
					new ParseSite().execute(Path);
				} else {
					Path = "https://yk.kz" + categories.get(
							// spinner.getSelectedItemPosition()
							CategorySelected).path + "/"
							+ Integer.toString(progressChanged + 1);
					new ParseSite().execute(Path);
				}
			}
		}
	};

	// Читаем разделы из XML
	void ParseCategoriesFromXMLTheSecond() {
		categories = new ArrayList<CategoriesStructure>();
		CategoriesStructure Categories_Structure = new CategoriesStructure();

		XmlPullParser parser = getResources().getXml(R.xml.categories);
		try {
			int ii = 0;

			while (parser.next() != XmlPullParser.END_TAG) {
				Categories_Structure = new CategoriesStructure();
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();

				if (name.equals("category")) {

					while (parser.next() != XmlPullParser.END_TAG) {
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						} else {
							String name1 = parser.getName();
							if (name1.equals("id")) {
								parser.require(XmlPullParser.START_TAG, null,
										"id");
								if (parser.next() == XmlPullParser.TEXT) {
									Categories_Structure.Id = Integer
											.parseInt(parser.getText());
									parser.nextTag();
								}
							} else if (name1.equals("name")) {
								parser.require(XmlPullParser.START_TAG, null,
										"name");
								if (parser.next() == XmlPullParser.TEXT) {
									Categories_Structure.name = parser
											.getText();
									parser.nextTag();
								}
							} else if (name1.equals("path")) {
								parser.require(XmlPullParser.START_TAG, null,
										"path");
								if (parser.next() == XmlPullParser.TEXT) {
									Categories_Structure.path = parser
											.getText();
									parser.nextTag();
								}
							}
						}
					}

					categories.add(Categories_Structure);
					data[ii] = Categories_Structure.name;
					ii++;
				}
			}
		} catch (XmlPullParserException e) {
		} catch (IOException e) {
		}
	}

	// Парсим Категории
	private class ParseCategories extends AsyncTask<String, Void, List<String>> {
		protected List<String> doInBackground(String... arg) {
			List<String> output = new ArrayList<String>();

			ParseCategoriesFromXMLTheSecond();
			return output;
		}

		// Конец Парсинга, выполняем по факту:
		protected void onPostExecute(List<String> output) {
			pd.dismiss();

			// Сортировка разделов
			List<String> dataCollection = new ArrayList<String>();
			for (String s : data) {
				dataCollection.add(s);
			}
			Collections.sort(dataCollection);
			Collections.sort(categories, new MyCatComp());
			data = new String[36];
			int i = 0;
			data[i] = " Все разделы";
			for (String s : dataCollection) {
				data[i] = s;
				i++;
			}

			// Список разделов
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					MainActivity.this, android.R.layout.simple_spinner_item,
					data);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			Spinner spinner = (Spinner) findViewById(R.id.Categories);
			spinner.setAdapter(adapter);
			spinner.setPrompt("Раздел");

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
										   int position, long id) {

					if (CategorySelected == -1) {

						Spinner spinner = (Spinner) findViewById(R.id.Categories);
						spinner.performClick();
						CategorySelected = spinner.getSelectedItemPosition();

					} else if (CategorySelected != -1) {
						if (!isNetworkConnected())
							return;

						progressChanged = 0;
						pagesontrol.setProgress(progressChanged);
						Path = "https://yk.kz" + categories.get(position).path;
						// if (cout > 0) {
						pd = ProgressDialog.show(MainActivity.this,
								categories.get(position).name, "Загрузка...",
								true, false);

						CategorySelected = position;

						new ParseSite().execute(Path);

						SearchString = "";
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}

		// Великая сортировка Array List
		public class MyCatComp implements Comparator<CategoriesStructure> {
			@Override
			public int compare(CategoriesStructure arg0,
							   CategoriesStructure arg1) {
				return arg0.name.compareTo(arg1.name);

			}

		}
	}

	// Первый запуск каталога объявлений
	public android.content.DialogInterface.OnClickListener firstStartSelected = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int indx) {
			if (!isNetworkConnected())
				return;

			progressChanged = 0;
			pagesontrol.setProgress(progressChanged);
			Path = "https://yk.kz" + categories.get(indx).path;

			// Запускаем диалог загрузки
			pd = ProgressDialog.show(MainActivity.this,
					categories.get(indx).name, "Загрузка...", true, false);

			CategorySelected = indx;

			new ParseSite().execute(Path);
			SearchString = "";
		}
	};

	// Парсим Страницу
	private class ParseSite extends AsyncTask<String, Void, List<String>> {
		@SuppressWarnings("deprecation")
		protected List<String> doInBackground(String... arg) {
			List<String> output = new ArrayList<String>();
			results = new ArrayList<ItemDetails>();
			try {
				HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
				List<TagNode> links = hh.getLinksByClass("news_date_sm");

				int i = 0;
				for (Iterator<TagNode> iterator = links.iterator(); iterator
						.hasNext();) {
					i++;
					TagNode divElement = (TagNode) iterator.next();

					if (i == 1) {

						if (divElement == null) {
							pagesontrol.setMax(0);
						} else {
							pagesontrol.setMax(Integer.parseInt(divElement
									.getText().toString()) - 1);
						}
					} else {

						ItemDetails item_details = new ItemDetails();

						item_details.setDate(divElement.getText().toString()
								.trim());
						item_details.setId("123");
						int cOfprice = divElement.getParent().getParent()
								.getElementsByName("div", true).length;
						if (cOfprice == 0) {
							item_details.setPrice("Не указана");
						} else {
							item_details
									.setPrice(divElement.getParent()
											.getParent()
											.getElementsByName("div", true)[0]
											.getText().toString().trim());
						}
						item_details.setPhone(divElement.getParent()
								.getElementsByName("strong", true)[0].getText()
								.toString().trim());
						item_details.setCategory("category1");

						TagNode desc = divElement.getParent();
						String Body = desc.getText().toString().substring(50)
								.trim();
						int delPart = item_details.getPhone().length();

						item_details.setItemDescription(Body
								.substring(0, Body.length() - delPart)
								.trim()
								.replace(
										item_details.getPhone()
												.replace(".", ". ").trim(), "")
								.replace("&quot;", "\""));
						item_details.setPhone(item_details.getPhone()
								.replaceAll("  ", " ").replaceAll("  ", ""));

						String source = divElement.getParent().getParent()
								.getElementsByName("td", true)[0]
								.getAttributeByName("background");

						if (source != null) {
							String Fullsource = divElement.getParent()
									.getParent().getElementsByName("td", true)[0]
									.getElementsByName("a", true)[0]
									.getAttributeByName("href");

							item_details.setImage(getImageByUrl("https://yk.kz"
									+ source, halfWidth));

							item_details.setFullImage("https://yk.kz"
									+ Fullsource);

						} else {
							Bitmap bmOriginal = BitmapFactory.decodeResource(
									getResources(), R.drawable.noimg);
							Bitmap bmHalf = Bitmap.createScaledBitmap(
									bmOriginal, halfWidth, halfWidth, false);
							item_details.setImage(new BitmapDrawable(bmHalf));
						}
						results.add(item_details);
					}
				}
			} catch (Exception e) {
				e.toString();
			}
			return output;
		}

		protected void onPostExecute(List<String> output) {
			// Убираем диалог загрузки
			pd.dismiss();

			ListView listview = (ListView) findViewById(R.id.listViewData);
			listview.setAdapter(new ItemListBaseAdapter(MainActivity.this,
					results));
		}
	}

	// Получаем картинку по URL
	@SuppressWarnings("deprecation")
	private Drawable getImageByUrl(String source, int halfWidth)
			throws IOException, MalformedURLException {
		// Вот так можно получить изображение по url
		URL url = new URL(source);
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();

		Bitmap bmOriginal = BitmapFactory.decodeStream(is);

		if (halfWidth == 0 && halfWidth < bmOriginal.getWidth()) {
			return new BitmapDrawable(bmOriginal);
		} else if (halfWidth == 0 && halfWidth > bmOriginal.getWidth()) {
			// halfWidth = halfWidth;
		}

		double x = bmOriginal.getWidth();
		double y = bmOriginal.getHeight();
		double k = x / y;
		int getHeight = (int) (halfWidth / k);

		Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth,
				getHeight, false);
		is.close();

		return new BitmapDrawable(bmHalf);
	}

	// Меню
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}