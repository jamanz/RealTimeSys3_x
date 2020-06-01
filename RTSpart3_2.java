package com.example.lab3.ui.lab32;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.lab3.R;

public class Lab32Fragment extends Fragment implements View.OnClickListener {
    private final double P = 4.0;
    private final int[][] points = {
            {0, 6},
            {1, 5},
            {3, 3},
            {2, 4}
    };
    private double w1, w2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab32, container, false);
        Button b = view.findViewById(R.id.lab32_button);
        b.setOnClickListener(this);
        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        View view = v.getRootView();
        Spinner iterationsNumSpinner = view.findViewById(R.id.lab32_deadline_iteration);
        Spinner deadlineSpinner = view.findViewById(R.id.lab32_deadline_time);
        Spinner learningSpeedSpinner = view.findViewById(R.id.lab32_learning_speed);
        TextView w1Result = view.findViewById(R.id.lab32_w1);
        TextView resultMessage = view.findViewById(R.id.lab32_result);
        TextView w2Result = view.findViewById(R.id.lab32_w2);
        TextView timeResult = view.findViewById(R.id.lab32_result_time);
        TextView iterationsResult = view.findViewById(R.id.lab32_iterations);

        long iterationsNum = Integer.parseInt(iterationsNumSpinner.getSelectedItem().toString());
        double deadline = Double.parseDouble(deadlineSpinner.getSelectedItem().toString());
        double learningSpeed = Double.parseDouble(learningSpeedSpinner.getSelectedItem().toString());
        double y;
        double dt;
        int iterations = 0;
        boolean done = false;
        w1 = 0;
        w2 = 0;
        long startTime = System.nanoTime();

        int index = 0;
        while (iterations++ < iterationsNum && (System.nanoTime() - startTime) < deadline * 1_000_000_000) {

            index %= 4;

            y = points[index][0] * w1 + points[index][1] * w2;

            if (isDone()) {
                done = true;
                break;
            }

            dt = P - y;
            w1 += dt * points[index][0] * learningSpeed;
            w2 += dt * points[index][1] * learningSpeed;
            index++;
        }

        if (done) {
            long execTimeMcs = (System.nanoTime() - startTime);
            resultMessage.setText(R.string.lab32_success);
            w1Result.setText(String.format("W1: %.3f", w1));
            w2Result.setText(String.format("W2: %.3f", w2));
            timeResult.setText(String.format("%.2f ms", (double) execTimeMcs / 1_000_000));
            createAlert(view, "Iterations: " + iterations);
        } else {
            String reason = "Training failed!";
            if (iterations >= iterationsNum) {
                reason += "\nMore iterations needed!";
            } else {
                reason += "\nMore time is required!";
            }
            resultMessage.setText(reason);
        }

    }

    private boolean isDone() {
        return P < points[0][0] * w1 + points[0][1] * w2
                && P < points[1][0] * w1 + points[1][1] * w2
                && P > points[2][0] * w1 + points[2][1] * w2
                && P > points[3][0] * w1 + points[3][1] * w2;
    }

    private void createAlert(View v, String msg) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
        alertBuilder.setTitle("Iterations count");
        alertBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}