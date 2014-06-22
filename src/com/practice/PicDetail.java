package com.practice;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 为所拍摄的照片添加详情
 * */
public class PicDetail extends Activity implements RecognitionListener{    
	
	private ImageView photoview;
	SQLiteDatabase db;
	Button saveBtn;
	String photoname;
	
	private static final String KWS_SEARCH = "火车";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String MENU_SEARCH = "menu";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_detail);
		System.out.println("=====on create");
		//显示图片
		photoview = (ImageView) findViewById(R.id.thumbnail);
		Intent camIntent=this.getIntent();
		photoname=camIntent.getStringExtra("picPath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bm = BitmapFactory.decodeFile(photoname, options);
        System.out.println("========pic detail photopath:"+photoname);
        photoview.setImageBitmap(bm);
        //数据库操作
        File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		saveBtn=(Button)findViewById(R.id.save_detail);
		saveBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View source) {
				// 获取用户输入
				String description=((EditText)findViewById(R.id.photo_description)).getText().toString();
				try {
					insertData(db,description,photoname);
					//启动新activity
	    			Intent intent = new Intent();
	    			intent.setClass(PicDetail.this, PhotoList.class);
	    			startActivity(intent);
				}
				catch(SQLException e) {
					db.execSQL("create table pic_info(_id integer primary key autoincrement,integer tour_id," +
								"photo_time date," +
								"pic_description varchar(255),photo_keyword varchar(255),photo_place varchar(100)," +
								"photo_path varchar(150))");
					insertData(db, description, photoname);
				}
			}
		});        
		
		//语音识别部分
		System.out.println("======recognition part");
		captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.psa_caption_key);
        //((TextView) findViewById(R.id.caption_text)).setText("Preparing the recognizer");
        startButton=(Button)findViewById(R.id.speak_start);
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        startButton.setOnTouchListener(new OnTouchListener() {	
			public boolean onTouch(View v, MotionEvent event) {		
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:	
			        	System.out.println("======action down");			        	 
						new AsyncTask<Void, Void, Exception>() {
				            @Override
				            protected Exception doInBackground(Void... params) {
				                try {
				                    Assets assets = new Assets(PicDetail.this);
				                    File assetDir = assets.syncAssets();                 
				                    setupRecognizer(assetDir);
				                    System.out.println("=======setup recognizer");
				                } catch (IOException e) {
				                    return e;
				                }
				                return null;
				            }
		
				            @Override
				            protected void onPostExecute(Exception result) {
				                if (result != null) {
				                    //((TextView) findViewById(R.id.caption_text)).setText("Failed to init recognizer " + result);
				                } else {
				                    switchSearch(KWS_SEARCH);
				                }
				                System.out.println("=======on post execute");
				            }
				        }.execute();
				        break;
					case MotionEvent.ACTION_UP:
			        	System.out.println("======action up");
						recognizer.stop();
						break;
					default:
						;
				}
				return false;
			}
		});        
	}	

	private void insertData(SQLiteDatabase db,String description,String path){
		db.execSQL("insert into pic_info values (null,1,null,?,null,null,?)",new String[] {description,path});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}	
	
	//语音识别模块
	@Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        /*if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else if (text.equals(FORECAST_SEARCH))
            switchSearch(FORECAST_SEARCH);
        else*/
            ((TextView) findViewById(R.id.photo_description)).setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.photo_description)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                || FORECAST_SEARCH.equals(recognizer.getSearchName()))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
    	System.out.println("======start switch search");
        recognizer.stop();
        recognizer.startListening(searchName);
        String caption = getResources().getString(captions.get(searchName));
        //((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/tdt_sc_8k"))
                .setDictionary(new File(modelsDir, "lm/all_regular_words.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-3f)
                .getRecognizer();
        recognizer.addListener(this);

        // Create keyword-activation search.
        //recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        File kwsFile=new File(modelsDir,"kws/all_regular_kws.txt");
        recognizer.addKeywordSearch(KWS_SEARCH, kwsFile);
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        // Create language model search.
        //File languageModel = new File(modelsDir, "lm/three.lm.DMP");
        //recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
    }
}