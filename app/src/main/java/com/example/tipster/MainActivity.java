package com.example.tipster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    // Widgets in the application
    private EditText txtAmount;
    private EditText txtPeople;
    private EditText txtTipOther;
    private RadioGroup rdoGroupTips;
    private Button btnCalculate;
    private Button btnReset;

    private TextView txtTipAmount;
    private TextView txtTotalToPay;
    private TextView txtTipPerPerson;

    // For the ID of the radio button selected
    private int radioCheckedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access the various widgets by their ID in R.java
        txtAmount = findViewById(R.id.txtAmount);
        // On app load, the cursor should be in the Amount field
        txtAmount.requestFocus();

        txtPeople = findViewById(R.id.txtPeople);
        txtTipOther = findViewById(R.id.txtTipOther);

        rdoGroupTips = findViewById(R.id.RadioGroupTips);

        btnCalculate = findViewById(R.id.btnCalculate);
        // On app load, the Calculate button is disabled
        btnCalculate.setEnabled(false);

        btnReset = findViewById(R.id.btnReset);

        txtTipAmount = findViewById(R.id.txtTipAmount);
        txtTotalToPay = findViewById(R.id.txtTotalToPay);
        txtTipPerPerson = findViewById(R.id.txtTipPerPerson);

        // ON app load, disable the Other Tip Percentage text field
        txtTipOther.setEnabled(false);

        /*
         * Attach on OnCheckedChangeListener to the
         * radio group to monitor radio buttons selected by user
         */
        rdoGroupTips.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Enable/disable Other Tip Percentage field
                if (checkedId == R.id.radioFifteen ||
                        checkedId == R.id.radioTwenty) {
                    txtTipAmount.setEnabled(false);
                    /*
                     * Enable the calculate button if Total Amount and No. of
                     * People fields have valid values.
                     */
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0);
                }
                if (checkedId == R.id.radioOther) {
                    // Enable the Other Tip Percentage field
                    txtTipOther.setEnabled(true);
                    // Set the focus to this field
                    txtTipOther.requestFocus();
                    /*
                     * Enable the Calculate button if Total Amount and No. of
                     * People fields have valid values. Also ensure that user
                     * has entered an Other Tip Percentage value before enablind
                     * the Calculate button.
                     */
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0 &&
                            txtTipOther.getText().length() > 0);
                }
                // To determine the tip percentage choice made by user
                radioCheckedId = checkedId;
            }
        });

        /*
         * Attach on KeyListener to the Tip Amount, No. of people, and Other Tip
         * Percentage text fields
         */
        txtAmount.setOnKeyListener(mKeyListener);
        txtPeople.setOnKeyListener(mKeyListener);
        txtTipOther.setOnKeyListener(mKeyListener);

        btnCalculate.setOnClickListener(mClickListener);
        btnReset.setOnClickListener(mClickListener);
    }

    /**
     * KeyListener for the Total Amount, No. of People, and Other Tip Percentage
     * text fields. We need to apply this key listener to check for the following
     * conditions:
     * <p>
     * 1) If the user selects Other Tip Percentage, then the Other Tip Percentage text
     * field should have a valid tip percentage entered by the user. Enable the
     * Calculate button only when the user enters a valid value.
     * <p>
     * 2) If the user does not enter values in the Total Amount and No. of People fields,
     * we cannot perform the calculations. Hence we enable the Calculate button
     * only when the user enters valid values.
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(@org.jetbrains.annotations.NotNull View v, int keyCode, KeyEvent event) {

            switch (v.getId()) {
                case R.id.txtAmount:
                case R.id.txtPeople:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0);
                    break;
                case R.id.txtTipOther:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.length() > 0 &&
                            txtTipOther.getText().length() > 0);
                    break;
            }

            return false;
        }
    };

    /**
     * ClickListener for the Calculate and Reset buttons.
     * Depending on the button clicked, the corresponding
     * method is called.
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NotNull View v) {
            if (v.getId() == R.id.btnCalculate)
                calculate();
            else
                reset();
        }
    };

    /**
     * Calculate the tip as per data entered by the user
     */
    private void calculate() {
        Double billAmount = Double.parseDouble(
                txtAmount.getText().toString());
        Double totalPeople = Double.parseDouble(
                txtPeople.getText().toString());
        Double percentage = null;
        boolean isError = false;
        if (billAmount < 1.0) {
            showErrorAlert("Enter a valid Total Amount",
                    txtAmount.getId());
            isError = true;
        }

        if (totalPeople < 1.0) {
            showErrorAlert("Enter a valid value for No. of People.",
                    txtPeople.getId());
            isError = true;
        }

        /*
         * If the user never changes the radio selection, then is means
         * the default selection of 15% is in effect. But it's
         * safer to verify
         */
        if (radioCheckedId == -1)
            radioCheckedId = rdoGroupTips.getCheckedRadioButtonId();
        if (radioCheckedId == R.id.radioFifteen)
            percentage = 15.00;
        else if (radioCheckedId == R.id.radioTwenty)
            percentage = 20.00;
        else if (radioCheckedId == R.id.radioOther) {
            txtTipOther.getText().toString();
            if (percentage < 1.0) {
                showErrorAlert("Enter a valid Tip Percentage",
                        txtTipOther.getId());
                isError = true;
            }
        }

        /*
         * If all fields are populated with valid values, then proceed to
         * calculate the tip
         */
        if (!isError) {
            Double tipAmount = ((billAmount * percentage) / 100);
            Double TotalToPay = billAmount + tipAmount;
            Double perPersonPays = TotalToPay / totalPeople;

            txtTipAmount.setText(tipAmount.toString());
            txtTotalToPay.setText(totalPeople.toString());
            txtTipPerPerson.setText(perPersonPays.toString());
        }
    }

    /**
     * Resets the results text views at the bottom of the screen and
     * resets the text fields and radio buttons.
     */
    private void reset() {
        txtTipAmount.setText("");
        txtTotalToPay.setText("");
        txtTipPerPerson.setText("");
        txtAmount.setText("");
        txtPeople.setText("");
        txtTipOther.setText("");
        rdoGroupTips.clearCheck();
        rdoGroupTips.check(R.id.radioFifteen);
        // Set focus on the first field
        txtAmount.requestFocus();
    }

    /**
     * shows the error message in an alert dialog
     *
     * @param errorMessage: String for the error message to show
     *
     * @param fieldId: ID of the field which caused the error.
     *               This is required so that the focus can be
     *               set on that field once the dialog is dismissed.
     */
    private void showErrorAlert(String errorMessage,
                                final int fieldId) {
        new AlertDialog.Builder(this).setTitle("Error")
                .setMessage(errorMessage).setNeutralButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findViewById(fieldId).requestFocus();
                    }
                }).show();
    }
}
