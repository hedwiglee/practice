package com.practice;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    private static final String KWS_SEARCH = "火车";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "博物馆";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private Button startButton;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.psa_caption_key);
        setContentView(R.layout.pocketsphinx);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");
        startButton=(Button)findViewById(R.id.voice_start);
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
				                    Assets assets = new Assets(PocketSphinxActivity.this);
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
				                    ((TextView) findViewById(R.id.caption_text))
				                            .setText("Failed to init recognizer " + result);
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
            ((TextView) findViewById(R.id.result_text)).setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
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
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
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