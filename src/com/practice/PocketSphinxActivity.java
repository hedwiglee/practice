package com.practice;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.*;


public class PocketSphinxActivity extends Activity implements
        RecognitionListener, AssetsTaskCallback {

    private static final String KWS_SEARCH_NAME = "wakeup";
    private static final String KWS_LIST_NAME = "wordlist";
    private static final String FORECAST_SEARCH = "咖啡";
    private static final String DIGITS_SEARCH = "餐厅";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "故宫";

    private SpeechRecognizer recognizer;
    private final Map<String, Integer> captions;
    private ProgressDialog dialog;

    public PocketSphinxActivity() {
        captions = new HashMap<String, Integer>();
        /*captions.put(KWS_SEARCH_NAME, R.string.kws_caption);
        captions.put(KWS_LIST_NAME, R.string.kws_list_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);*/
        System.out.println("create function");
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.pocketsphinx);
        new AssetsTask(this, this).execute();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else if (text.equals(FORECAST_SEARCH))
            switchSearch(FORECAST_SEARCH);
        else
            ((TextView) findViewById(R.id.result_text)).setText(text);
        System.out.println("========onpartialresult");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        String text = hypothesis.getHypstr();
        makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        System.out.println("========onresult");
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName);
        //String caption = getResources().getString(captions.get(searchName));
        //((TextView) findViewById(R.id.caption_text)).setText(caption);
        System.out.println("========switchsearch");
    }

    @Override
    public void onBeginningOfSpeech() {
        System.out.println("========onbeginning");
    }

    @Override
    public void onEndOfSpeech() {
        if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                || FORECAST_SEARCH.equals(recognizer.getSearchName()))
            switchSearch(KWS_SEARCH_NAME);
        System.out.println("========onendofspeech");
    }

    @Override
    public void onTaskCancelled() {
        System.out.println("========ontaskcanceled");
    }

    @Override
    public void onTaskComplete(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/tdt_sc_8k"))
                .setDictionary(new File(modelsDir, "lm/gugong.dic"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(1e-5f)
                .getRecognizer();

        File keywordFile=new File(modelsDir,"kws/gugongkey.txt");
        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH_NAME, KEYPHRASE);
        //增加多个关键词识别功能
        recognizer.addKeywordSearch(KWS_LIST_NAME, keywordFile);
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        // Create language model search.
        File languageModel = new File(modelsDir, "lm/gugong.lm.DMP");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        switchSearch(KWS_LIST_NAME);
        dialog.dismiss();
        System.out.println("========ontaskcomplete");
    }

    @Override
    public void onTaskError(Throwable e) {
        if (dialog.isShowing())
            dialog.dismiss();
        //((TextView) findViewById(R.id.caption_text)).setText(e.getMessage());
        System.out.println("========ontaskerror");
    }

    @Override
    public void onTaskProgress(File file) {
        dialog.incrementProgressBy(1);
        System.out.println("========ontaskprogress");
    }

    @Override
    public void onTaskStart(int size) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Copying model files...");
        dialog.setMax(size);
        dialog.show();
        System.out.println("========ontaskstart");
    }
}
