package com.idx.technicalanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ScrollView;

public class MainActivity extends Activity {

    private EditText stockInput;
    private TextView statusText;
    private TextView reportText;
    private Handler handler;

    private static final int REFRESH_MS = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        buildInterface();

        startAutoRefresh();
    }

    private void buildInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("IDX TECHNICAL ANALYSIS");
        title.setTextSize(22);
        title.setTextColor(Color.rgb(13, 71, 161));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);

        root.addView(title);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);

        stockInput = new EditText(this);
        stockInput.setHint("Kode saham, contoh AADI");
        stockInput.setSingleLine(true);

        searchRow.addView(
                stockInput,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        Button searchButton = new Button(this);
        searchButton.setText("ANALISIS");

        searchButton.setOnClickListener(v -> {

            String kode = stockInput
                    .getText()
                    .toString()
                    .trim()
                    .toUpperCase();

            if (kode.length() == 0) {
                kode = "AADI";
                stockInput.setText(kode);
            }

            loadAnalysis(kode);
        });

        searchRow.addView(searchButton);

        root.addView(searchRow);

        statusText = new TextView(this);
        statusText.setText("Status: siap");
        statusText.setTextSize(14);
        statusText.setPadding(0, 15, 0, 15);

        root.addView(statusText);

        ScrollView scrollView = new ScrollView(this);

        reportText = new TextView(this);
        reportText.setTextSize(16);
        reportText.setTextColor(Color.DKGRAY);
        reportText.setPadding(5, 5, 5, 30);

        scrollView.addView(reportText);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);

        stockInput.setText("AADI");

        loadAnalysis("AADI");
    }

    private void startAutoRefresh() {

        handler.postDelayed(
                new Runnable() {

                    @Override
                    public void run() {

                        String kode = stockInput
                                .getText()
                                .toString()
                                .trim()
                                .toUpperCase();

                        if (kode.length() > 0) {
                            loadAnalysis(kode);
                        }

                        handler.postDelayed(
                                this,
                                REFRESH_MS
                        );
                    }
                },
                REFRESH_MS
        );
    }

    private void loadAnalysis(String kode) {

        statusText.setText(
                "Status: menghubungkan ke RTI..."
        );

        reportText.setText(
                "IDX TECHNICAL ANALYSIS\n\n"
                + "Kode       : " + kode + "\n"
                + "RTI        : menghubungkan...\n"
                + "Refresh    : 15 detik\n\n"
                + "Technical Analysis\n"
                + "-------------------------\n"
                + "Trend       : menunggu data\n"
                + "Price Action: menunggu data\n"
                + "FVG         : menunggu data\n"
                + "Support     : menunggu data\n"
                + "Resistance  : menunggu data\n"
                + "Breakout    : menunggu data\n\n"
                + "Trading Scenarios\n"
                + "-------------------------\n"
                + "1. Rebound / Sell\n"
                + "2. Breakout\n"
                + "3. Entry Scenario\n"
                + "4. Risk Management\n\n"
                + "Mengambil data RTI..."
        );

        RtiClient.getRealtime(
                kode,
                new RtiClient.Callback() {

                    @Override
                    public void onSuccess(
                            final String result
                    ) {

                        runOnUiThread(
                                new Runnable() {

                                    @Override
                                    public void run() {

                                        statusText.setText(
                                                "Status: RTI terhubung"
                                        );

                                        reportText.setText(
                                                "IDX TECHNICAL ANALYSIS\n\n"
                                                + "Kode: "
                                                + kode
                                                + "\n\n"
                                                + "RTI RESPONSE\n"
                                                + "-------------------------\n"
                                                + result
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(
                            final String error
                    ) {

                        runOnUiThread(
                                new Runnable() {

                                    @Override
                                    public void run() {

                                        statusText.setText(
                                                "Status: RTI gagal"
                                        );

                                        reportText.setText(
                                                "IDX TECHNICAL ANALYSIS\n\n"
                                                + "Kode: "
                                                + kode
                                                + "\n\n"
                                                + "RTI STATUS\n"
                                                + "-------------------------\n"
                                                + error
                                                + "\n\n"
                                                + "Tidak ada data palsu."
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }
}
