package com.idx.technicalanalysis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RtiClient {

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }

    private static final String RTI_URL =
            "https://investor4.rti.co.id/new_rti2/tview/index_tview.jsp";

    public static void getRealtime(
            final String kode,
            final Callback callback
    ) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection connection = null;

                try {

                    URL url = new URL(
                            RTI_URL + "?code=" + kode
                    );

                    connection =
                            (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0"
                    );

                    int responseCode =
                            connection.getResponseCode();

                    if (responseCode != 200) {

                        callback.onError(
                                "RTI HTTP " + responseCode
                        );

                        return;
                    }

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getInputStream()
                                    )
                            );

                    StringBuilder result =
                            new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }

                    reader.close();

                    callback.onSuccess(
                            result.toString()
                    );

                } catch (Exception e) {

                    callback.onError(
                            "RTI error: "
                            + e.getMessage()
                    );

                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

        }).start();
    }
}
