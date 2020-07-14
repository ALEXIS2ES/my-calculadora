package com.lhamfor.mycalculadora;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.contenMain)
    RelativeLayout contenMain;
    private boolean onIsEditinProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configEditText();

    }

    private void configEditText() {
            /*etInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });*/
        etInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == motionEvent.ACTION_UP){
                    if (motionEvent.getRawX() >= (etInput.getRight() -
                    etInput.getCompoundDrawables() [Constantes.DRAWABLE_RIGHT].getBounds().width())){
                        if (etInput.length() > 0){
                            final  int length = etInput.getText().length();
                            etInput.getText().delete(length-1, length);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (onIsEditinProgress &&
                        Metodos.canReplaceOperator(charSequence)){
                    onIsEditinProgress = true;
                    etInput.getText().delete(etInput.getText().length()-2, etInput.getText().length()-1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                onIsEditinProgress= false;
            }
        });
    }

    @OnClick({R.id.btnSeven, R.id.btnFour, R.id.btnOne, R.id.btnPoint, R.id.btnEight, R.id.btnFive,
            R.id.btntwo, R.id.btnZero, R.id.btnNine, R.id.btnSix, R.id.btnThree})
    public void inClickNumbers(View view) {
        final String valStr = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.btnZero:
            case R.id.btnOne:
            case R.id.btntwo:
            case R.id.btnThree:
            case R.id.btnFour:
            case R.id.btnFive:
            case R.id.btnSix:
            case R.id.btnSeven:
            case R.id.btnEight:
            case R.id.btnNine:
                etInput.getText().append(valStr);
                break;
            case R.id.btnPoint:
                final String operacion = etInput.getText().toString();

                final String operador = Metodos.getOperator(operacion);

                final int count = operacion.length() - operacion.replace(".", "").length();

                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && (!operador.equals(Constantes.OPERATOR_NULL)))) {
                    etInput.getText().append(valStr);
                }
                break;
        }
    }

    @OnClick({R.id.btnClear, R.id.btnDiv, R.id.btnMultiplication, R.id.btnSub, R.id.btnSum, R.id.btnResult})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                etInput.setText("");
                break;
            case R.id.btnDiv:
            case R.id.btnMultiplication:
            case R.id.btnSub:
            case R.id.btnSum:
                resolve(false);

                final String operador = ((Button)view).getText().toString();
                final String operacion = etInput.getText().toString();


                final String ultimoCaracter = operacion.isEmpty() ? "":
                        operacion.substring(operacion.length() - 1);
                if (operador.equals(Constantes.OPERATOR_SUB)) {
                    if (operacion.isEmpty() ||
                            (!(ultimoCaracter.equals(Constantes.OPERATOR_SUB))) &&
                            !(ultimoCaracter.equals(Constantes.POINT))) {
                        etInput.getText().append(operador);
                    }
                }else {
                    if (!operacion.isEmpty() &&
                             !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                             !(ultimoCaracter.equals(Constantes.POINT))){
                        etInput.getText().append(operador);
                    }
                }
                break;
            case R.id.btnResult:
                resolve(true);
                break;
        }
    }
    private  void resolve(boolean fromResult) {
        Metodos.tryResolve(fromResult, etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                ShowMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                onIsEditinProgress = true;
            }
        });
    }

    private void ShowMessage(int errorRes) {
        Snackbar.make(contenMain, errorRes, Snackbar.LENGTH_SHORT).show();
    }
}
