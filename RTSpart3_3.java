package com.example.lab3.ui.lab33;

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

import java.util.Random;

public class Lab33Fragment extends Fragment implements View.OnClickListener {
    private Random random;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab33, container, false);
        Button b = view.findViewById(R.id.lab33_button);
        b.setOnClickListener(this);
        return view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        View view = v.getRootView();
        EditText aEditText = view.findViewById(R.id.lab33_a);
        EditText bEditText = view.findViewById(R.id.lab33_b);
        EditText cEditText = view.findViewById(R.id.lab33_c);
        EditText dEditText = view.findViewById(R.id.lab33_d);
        EditText yEditText = view.findViewById(R.id.lab33_y);
        TextView x1 = view.findViewById(R.id.lab33_x1);
        TextView x2 = view.findViewById(R.id.lab33_x2);
        TextView x3 = view.findViewById(R.id.lab33_x3);
        TextView x4 = view.findViewById(R.id.lab33_x4);
        TextView result = view.findViewById(R.id.lab33_result);
        TextView time = view.findViewById(R.id.lab33_time);
        random = new Random();
        try {
            int a = Integer.parseInt(aEditText.getText().toString());
            int b = Integer.parseInt(bEditText.getText().toString());
            int c = Integer.parseInt(cEditText.getText().toString());
            int d = Integer.parseInt(dEditText.getText().toString());
            int y = Integer.parseInt(yEditText.getText().toString());
            long start = System.nanoTime();
            int[] x1234 = solve(a, b, c, d, y, view);
            long execTimeMls = (System.nanoTime() - start);
            x1.setText(String.valueOf(x1234[0]));
            x2.setText(String.valueOf(x1234[1]));
            x3.setText(String.valueOf(x1234[2]));
            x4.setText(String.valueOf(x1234[3]));
            time.setText(String.format("%.2f ms", (double) execTimeMls / 1_000_000));
            result.setText(R.string.lab33_success);
        }
        catch (NumberFormatException e) {
            result.setText(R.string.lab33_failure);
        }
    }

    private int[][] makePairs(double[] survProb, int[][] population) {
        int[][] pairs = new int[population.length][2];
        int parentsNum = survProb.length / 2;
        int[] parents = new int[parentsNum];
        int maxIndex;
        double max;
        for(int i = 0; i < parentsNum; i++) {
            max = survProb[0];
            maxIndex = 0;
            for (int j = 1; j < survProb.length; j++) {
                if (survProb[j] > max) {
                    max = survProb[j];
                    maxIndex = j;
                }
            }
            survProb[maxIndex] = -1;
            parents[i] = maxIndex;
        }

        for (int i = 0; i < pairs.length;) {
            pairs[i][0] = parents[random.nextInt(parents.length)];
            pairs[i][1] = parents[random.nextInt(parents.length)];
            if (pairs[i][0] != pairs[i][1]) {
                i++;
            }
        }
        return pairs;
    }

    private int[] crossover(int[] pair1, int[] pair2) {
        int bound = 1 + random.nextInt(3);
        int[] child = new int[pair1.length];
        for (int i = 0; i < child.length; i++) {
            if (i < bound) {
                child[i] = pair1[i];
            } else {
                child[i] = pair2[i];
            }
        }
        return child;
    }

    private int[][] calculateNewPopulation(int[][] parentPairs, int[][] population) {
        int[][] newPopulation = new int[population.length][4];
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = crossover(population[parentPairs[i][0]], population[parentPairs[i][1]]);
        }
        return newPopulation;
    }


    private int[][] firstPopulation(int y) {
        int[][] firstPopulation = new int[2 + random.nextInt(y - 1)][4];
        for (int i = 0; i < firstPopulation.length; i++) {
            for (int j = 0; j < firstPopulation[0].length; j++) {
                firstPopulation[i][j] = random.nextInt(y / 2);
            }
        }
        return firstPopulation;
    }

    private int[] fitness(int[][] population, int[] array, int y) {
        int[] dts = new int[population.length];
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                dts[i] += population[i][j] * array[j];
            }
            dts[i] = Math.abs(dts[i] - y);
        }
        return dts;
    }

    private int solution(int[] array) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    private double[] survivalProbability(int[] deltas) {
        double cummProb = 0;
        double[] surv = new double[deltas.length];
        for (int delta : deltas) {
            cummProb += (double) 1 / delta;
        }
        for (int i = 0; i < deltas.length; i++) {
            surv[i] = ((double) 1 / deltas[i]) / cummProb;
        }
        return surv;
    }

    private int[][] populate(int[] deltas, int[][] previousPopulation) {
        double[] survProb = survivalProbability(deltas);
        int[][] parentPairs = makePairs(survProb, previousPopulation);
        return calculateNewPopulation(parentPairs, previousPopulation);
    }

    private double mean(double[] array) {
        double mean = 0;
        for (double v : array) {
            mean += v;
        }
        mean /= array.length;
        return mean;
    }

    private void change(int[][] population, int y) {
        double prob = 0.5;
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                double coin = random.nextDouble();
                if (coin <= prob) population[i][j] = random.nextInt(y + 1);
            }
        }
    }

    private int[] solve(int a, int b, int c, int d, int y, View v) {
        int[][] population = firstPopulation(y);
        int[] array = {a, b, c, d};
        int index;
        int[] dts;
        while (true) {
            dts = fitness(population, array, y);
            if ((index = solution(dts)) != -1) {
                break;
            } else {
                double avgSurvivalOld = mean(survivalProbability(dts));
                int[][] newPopulation = populate(dts, population);
                double avgSurvivalNew = mean(survivalProbability(fitness(newPopulation, array, y)));
                while (avgSurvivalOld >= avgSurvivalNew) {
                    avgSurvivalNew += 0.01;
                }
                if (avgSurvivalOld < avgSurvivalNew) {
                    population = newPopulation;
                    createAlert(v, "optimal mutation is " + avgSurvivalNew * 100 + "%");
                } else {
                    change(population, y);
                }
            }
        }
        return population[index];
    }

    private void createAlert(View v, String msg) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
        alertBuilder.setTitle("Mutation");
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