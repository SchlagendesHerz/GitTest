package ua.com.foxminded.tasks.task1.stringflipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Activity app;
    private Button flipButton;
    private EditText editText;
    private EditText ignorEditText;
    private ConstraintLayout constraintLayout1;
    private StringFlipper stringFlipper;
    private String ignoreDefaultPrompt;
    private String ignoreSetPrompt;
    private String emptyString;
    private String ignoreEllipsisPrompt;
    private int ignorePromptLength;

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
        app = this;
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState != null) {
            String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
            stringFlipper.setToIgnore(savedInstanceState.getCharSequence(ignoreStringKey));
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

    private void init() {
        editText = findViewById(R.id.editText);
        flipButton = findViewById(R.id.flipButton);
        ignorEditText = findViewById(R.id.ignorEditText);
        constraintLayout1 = findViewById(R.id.constraintLayout);
        ignoreDefaultPrompt = getResources().getString(R.string.ignore_default_prompt);
        emptyString = getResources().getString(R.string.empty_string);
        ignoreSetPrompt = getResources().getString(R.string.ignore_set_prompt);
        ignorePromptLength = Integer.parseInt(getResources().getString(R.string.ignore_prompt_length));
        ignoreEllipsisPrompt = getResources().getString(R.string.ignore_ellipsis_prompt);
        stringFlipper = new StringFlipper();

        View.OnClickListener oclFlipButton = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ignorEditText.clearFocus();
                editText.clearFocus();
                hideKeyboard(app);
                editText.setText(stringFlipper.rotate(editText.getText()));
            }
        };

        View.OnFocusChangeListener ofclCostraintLayout1 = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(app);
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
                } else {
                    stringFlipper.setToIgnore(ignorEditText.getText());
                    if (stringFlipper.hasIgnoreSet()) {
                        CharSequence toSetTextEditPrompt = String.format(ignoreSetPrompt, stringFlipper.getIgnore());
                        if (toSetTextEditPrompt.length() > ignorePromptLength) {
                            int maxLen = ignorePromptLength - ignoreEllipsisPrompt.length() + 1;
                            toSetTextEditPrompt = stringFlipper.getIgnore().subSequence(0, maxLen);
                            toSetTextEditPrompt = String.format(ignoreEllipsisPrompt, toSetTextEditPrompt);
                        }
                        ignorEditText.setText(toSetTextEditPrompt);
                    } else {
                        ignorEditText.setText(ignoreDefaultPrompt);
                    }
                }
            }
        };

        flipButton.setOnClickListener(oclFlipButton);
        constraintLayout1.setOnFocusChangeListener(ofclCostraintLayout1);
        ignorEditText.setOnFocusChangeListener(ofclIgnorEditText);
    }
}