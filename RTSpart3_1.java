package com.example.lab3.ui.lab31;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.lab3.R;

public class Lab31Fragment extends Fragment implements View.OnClickListener {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab31, container, false);
        Button b = view.findViewById(R.id.lab31_button);
        b.setOnClickListener(this);
        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        View view = v.getRootView();
        EditText inputNum = view.findViewById(R.id.lab31_inputNumber);
        EditText timeLimit = view.findViewById(R.id.lab31_timeLimit);
        if (isEmpty(inputNum)) {
            inputNum.setError("Please, enter valid number");
            return;
        }
        TextView r1 = view.findViewById(R.id.lab31_r1);
        TextView r2 = view.findViewById(R.id.lab31_r2);
        TextView time = view.findViewById(R.id.lab31_time);
        long[] values;
        long inputNumLong = Long.parseLong(inputNum.getText().toString());
        long timeLimitLong = Long.parseLong(timeLimit.getText().toString());
        values = factor(inputNumLong);
        if (timeLimitLong < values[2] / 1_000_000_000) {
            createErrorAlert(view, "Lasted longer than time limit", "Time limit error");
            r1.setText("R1: error");
            r2.setText("R2: error");
            time.setText(String.format("%.2f ms", (double) values[2] / 1_000_000));
        } else {
            r1.setText("R1: " + values[0]);
            r2.setText("R2: " + values[1]);
            time.setText(String.format("%.2f ms", (double) values[2] / 1_000_000));
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private static long[] factor(long N) {
        long startTime = System.nanoTime();

        long a = (long) Math.ceil(Math.sqrt(N));
        long b = (long) (Math.pow(a, 2) - N);
        while (!isSquare(b)) {
            a++;
            b = (long) (Math.pow(a, 2) - N);
        }
        long r1 = a - (long) Math.sqrt(b);
        long r2 = N / r1;

        long endTime = System.nanoTime();
        return new long[]{r1, r2, endTime - startTime};
    }

    private static boolean isSquare(long N) {
        long sqr = (long) Math.sqrt(N);
        return Math.pow(sqr + 1, 2) == N || sqr * sqr == N;
    }


    private void createErrorAlert(View v, String errorMsg, String title) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
        alertBuilder.setTitle(title);
        alertBuilder
                .setMessage(errorMsg)
                .setCancelable(false)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

}