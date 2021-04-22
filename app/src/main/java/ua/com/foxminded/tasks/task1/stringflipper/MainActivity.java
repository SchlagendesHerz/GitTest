package ua.com.foxminded.tasks.task1.stringflipper;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnLayoutChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName().toUpperCase();

    private EditText editText;
    private String emptyString;
    private String ignoreDefaultPrompt;
    private EditText ignoreEditText;
    private int ignoreEditTextWidth;
    private float ignorePromptMarginsValue;
    private boolean ignorePromptMarginsFixed;
    private String ignoreEllipsisPrompt;
    private String ignoreSetPrompt;
    private Activity mainActivity;
    private StringFlipper stringFlipper;

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

    private void initOnCreate() {
        editText = findViewById(R.id.editText);
        Button flipButton = findViewById(R.id.flipButton);
        ignoreEditText = findViewById(R.id.ignoreEditText);
        ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        ignoreDefaultPrompt = getResources().getString(R.string.ignore_default_prompt);
        emptyString = getResources().getString(R.string.empty_string);
        ignoreSetPrompt = getResources().getString(R.string.ignore_set_prompt);
        ignoreEllipsisPrompt = getResources().getString(R.string.ignore_ellipsis_prompt);
        ignorePromptMarginsValue = getResources().getDimension(R.dimen.ignore_prompt_margins_value);
        ignorePromptMarginsFixed = getResources().getBoolean(R.bool.ignore_prompt_margins_fixed);
        stringFlipper = new StringFlipper();

        flipButton.setOnClickListener(this);
        backgroundLayout.setOnFocusChangeListener(this);
        ignoreEditText.setOnFocusChangeListener(this);
        ignoreEditText.addOnLayoutChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        ignoreEditText.clearFocus();
        editText.clearFocus();
        hideKeyboard(mainActivity);
        editText.setText(stringFlipper.rotate(editText.getText()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        initOnCreate();
        if (savedInstanceState != null) {
            String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
            stringFlipper.setToIgnore(savedInstanceState.getCharSequence(ignoreStringKey));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.backgroundLayout:
                if (hasFocus) {
                    hideKeyboard(mainActivity);
                }
                break;
            case R.id.ignoreEditText:
                if (hasFocus) {

                    ignoreEditText.setText(stringFlipper.hasIgnoreSet()
                            ? stringFlipper.getIgnore()
                            : emptyString);
                    ignoreEditText.setSelection(ignoreEditText.getText().length());

                } else {
                    stringFlipper.setToIgnore(ignoreEditText.getText());
                    ignoreEditText.setText(getIgnorePrompt());
                }
                break;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int ignoreEditTextWidth = v.getWidth();
        if (ignoreEditTextWidth != 0 && ignoreEditTextWidth != this.ignoreEditTextWidth) {
            this.ignoreEditTextWidth = ignoreEditTextWidth - (int) getMargins(ignoreEditTextWidth);
            if (!ignoreEditText.hasFocus()) {
                ignoreEditText.setText(getIgnorePrompt());
            }
        }
    }

    private float getMargins(int ignoreEditTextWidth) {
        return ignorePromptMarginsFixed
                ? getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                    ? getResources().getDisplayMetrics().xdpi * ignorePromptMarginsValue / 160
                    : getResources().getDisplayMetrics().ydpi * ignorePromptMarginsValue / 160
                : ignoreEditTextWidth * ignorePromptMarginsValue / 100.0f;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (ignoreEditText.hasFocus()) {
            stringFlipper.setToIgnore(ignoreEditText.getText());
        }
        String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
        outState.putCharSequence(ignoreStringKey, stringFlipper.getIgnore());
    }

    private CharSequence getIgnorePrompt() {
        if (stringFlipper.hasIgnoreSet()) {
            CharSequence toSetTextEditPrompt = String.format(ignoreSetPrompt, stringFlipper.getIgnore());
            int subEnd = stringFlipper.getIgnore().length();
            Paint ignoreEditTextPaint = ignoreEditText.getPaint();
            float ignorePromptLength = ignoreEditTextPaint.measureText(toSetTextEditPrompt.toString());

            while (ignorePromptLength >= ignoreEditTextWidth) {
//                Log.i(TAG, "ignorePromptLength = " + ignorePromptLength);
//                Log.i(TAG, "ignoreEditTextWidth = " + ignoreEditTextWidth);
//                Log.i(TAG, "subEnd = " + subEnd);
                subEnd--;
                toSetTextEditPrompt = stringFlipper.getIgnore().subSequence(0, subEnd);
                toSetTextEditPrompt = String.format(ignoreEllipsisPrompt, toSetTextEditPrompt);
                ignorePromptLength = ignoreEditTextPaint.measureText(toSetTextEditPrompt.toString());
            }

            return toSetTextEditPrompt;
        }

        return ignoreDefaultPrompt;
    }
}