package ua.com.foxminded.tasks.task1.stringflipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Activity app;
    private Button flipButton;
    private EditText editText;
    private EditText exceptEditText;
    private ConstraintLayout constraintLayout1;
    private StringFlipper stringFlipper;
    private String exceptDefaultText;
    private String exceptSetText;
    private String emptyString;

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
        String ignoreStringKey = getResources().getString(R.string.ignore_string_key);
        outState.putCharSequence(ignoreStringKey, stringFlipper.getIgnore());
    }

    private void init() {
        editText = findViewById(R.id.editText);
        flipButton = findViewById(R.id.flipButton);
        exceptEditText = findViewById(R.id.exceptEditText);
        exceptDefaultText = getResources().getString(R.string.except_default_text);
        emptyString = getResources().getString(R.string.empty_string);
        exceptSetText = getResources().getString(R.string.except_set_text);
        constraintLayout1 = findViewById(R.id.constraintLayout1);
        stringFlipper = new StringFlipper();

        View.OnClickListener oclFlipButton = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exceptEditText.clearFocus();
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

        View.OnFocusChangeListener ofclExceptEditText = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    exceptEditText.setText(stringFlipper.hasIgnoreSet()
                            ? stringFlipper.getIgnore()
                            : emptyString);
                } else {
                    stringFlipper.setToIgnore(exceptEditText.getText());
                    exceptEditText.setText(stringFlipper.hasIgnoreSet()
                            ? String.format(exceptSetText, stringFlipper.getIgnore())
                            : exceptDefaultText);
                }
            }
        };

        flipButton.setOnClickListener(oclFlipButton);
        constraintLayout1.setOnFocusChangeListener(ofclCostraintLayout1);
        exceptEditText.setOnFocusChangeListener(ofclExceptEditText);
    }

}