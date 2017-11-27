package com.example.sharadsingh.setalarmtostarteverymorning.receiver;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by sharadsingh on 21/11/17.
 */

public class SocketPage extends Activity {
    private Socket mSocket;
    public static final String SEND_MESSAGE = "send Data";
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (this == null || args[1] == null) return;
            String mesg = (String) args[1];
            if (mesg.trim().equalsIgnoreCase("")) return;
             runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String uName = "";
                    String msgDate = "";
                    String json = (String) args[0];
                    try {
                        JSONObject user = new JSONObject(json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String mesg = (String) args[1];


                }
            });
        }
    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSocket != null) {
            mSocket.disconnect();
        }
        try {

           String URL_OF_SOCKET = "http://mobilehost.truxapp.com:5055";
            Uri serverUrl = Uri.parse(URL_OF_SOCKET);
            Uri.Builder builder = serverUrl.buildUpon()
                    .appendQueryParameter("id", "7836914331")
                    .appendQueryParameter("timestamp", "dd")
                    .appendQueryParameter("lat", "ewfef")
                    .appendQueryParameter("lon", "fdgdg")
                    .appendQueryParameter("speed", String.valueOf(123))
                    .appendQueryParameter("bearing", String.valueOf(123))
                    .appendQueryParameter("altitude", "fgsf")
                    .appendQueryParameter("batt", String.valueOf(123));
            builder.appendQueryParameter("alarm", String.valueOf(123));
            builder.build().toString();
            mSocket = IO.socket(builder.build().toString());
        } catch (URISyntaxException e) {
        }

        mSocket.connect();
        mSocket.on("data", onNewMessage);


        mSocket.emit(SEND_MESSAGE, "");
    }

    @Override
    public void onStop() {
        mSocket.off("data", onNewMessage);
        mSocket.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mSocket.off("data", onNewMessage);
        mSocket.disconnect();
        super.onDestroy();
    }



}
