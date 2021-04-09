package ua.com.foxminded.tasks.task1.stringflipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Activity mainActivity;
    private Button flipButton;
    private EditText editText;
    private EditText ignorEditText;
    private ConstraintLayout constraintLayout;
    private StringFlipper stringFlipper;
    private String ignoreDefaultPrompt;
    private String ignoreSetPrompt;
    private String emptyString;
    private String ignoreEllipsisPrompt;
    private int ignorePromptLengthHor;
    private int ignorePromptLengthVer;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        init();
        if (savedInstanceState != null) {
            String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
            stringFlipper.setToIgnore(savedInstanceState.getCharSequence(ignoreStringKey));
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!ignorEditText.hasFocus()) {
            setIgnorEditTextPrompt();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (ignorEditText.hasFocus()) {
            stringFlipper.setToIgnore(ignorEditText.getText());
        }

        String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
        outState.putCharSequence(ignoreStringKey, stringFlipper.getIgnore());
    }

    private void setIgnorEditTextPrompt() {
        if (stringFlipper.hasIgnoreSet()) {

            CharSequence toSetTextEditPrompt = String.format(ignoreSetPrompt, stringFlipper.getIgnore());

            int ignorePromptLengthLimit = mainActivity
                    .getResources()
                    .getConfiguration()
                    .orientation == Configuration.ORIENTATION_PORTRAIT
                    ? ignorePromptLengthVer
                    : ignorePromptLengthHor;

            if (toSetTextEditPrompt.length() > ignorePromptLengthLimit) {

                int subEnd = ignorePromptLengthLimit - ignoreEllipsisPrompt.length() + 2;
                toSetTextEditPrompt = stringFlipper.getIgnore().subSequence(0, subEnd);
                toSetTextEditPrompt = String.format(ignoreEllipsisPrompt, toSetTextEditPrompt);

            }
            ignorEditText.setText(toSetTextEditPrompt);
        } else {
            ignorEditText.setText(ignoreDefaultPrompt);
        }
    }

    private void init() {
        editText = findViewById(R.id.editText);
        flipButton = findViewById(R.id.flipButton);
        ignorEditText = findViewById(R.id.ignorEditText);
        constraintLayout = findViewById(R.id.constraintLayout);
        ignoreDefaultPrompt = getResources().getString(R.string.ignore_default_prompt);
        emptyString = getResources().getString(R.string.empty_string);
        ignoreSetPrompt = getResources().getString(R.string.ignore_set_prompt);
        ignorePromptLengthHor = getResources().getInteger(R.integer.ignore_prompt_length_hor);
        ignorePromptLengthVer = getResources().getInteger(R.integer.ignore_prompt_length_ver);
        ignoreEllipsisPrompt = getResources().getString(R.string.ignore_ellipsis_prompt);
        stringFlipper = new StringFlipper();

        View.OnClickListener oclFlipButton = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ignorEditText.clearFocus();
                editText.clearFocus();
                hideKeyboard(mainActivity);
                editText.setText(stringFlipper.rotate(editText.getText()));
            }
        };

        View.OnFocusChangeListener ofclCostraintLayout = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(mainActivity);
                }
            }
        };


        View.OnFocusChangeListener ofclIgnorEditText = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    ignorEditText.setText(stringFlipper.hasIgnoreSet()
                            ? stringFlipper.getIgnore()
                            : emptyString);
                    ignorEditText.setSelection(ignorEditText.getText().length());

                } else {
                    stringFlipper.setToIgnore(ignorEditText.getText());
                    setIgnorEditTextPrompt();
                }
            }
        };

        flipButton.setOnClickListener(oclFlipButton);
        constraintLayout.setOnFocusChangeListener(ofclCostraintLayout);
        ignorEditText.setOnFocusChangeListener(ofclIgnorEditText);
    }
}