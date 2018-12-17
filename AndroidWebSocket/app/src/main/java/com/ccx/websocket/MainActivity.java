package com.ccx.websocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText        mEditText;
    private TextView        mTextView;
    private WebSocketClient mWebSocketClient;
    private EditText        mEditText1;
    private EditText        mEditText2;
    private EditText        mEditText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.link).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.msg);
        mEditText1 = (EditText) findViewById(R.id.room_name);
        mEditText2 = (EditText) findViewById(R.id.server_address);
        mEditText3 = (EditText) findViewById(R.id.name);
        mTextView = (TextView) findViewById(R.id.result);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link:
                if (mWebSocketClient != null) {
                    mWebSocketClient.close();
                    mWebSocketClient = null;
                }
                try {
                    mWebSocketClient = new WebSocketClient(new URI(mEditText2.getText().toString() + "/" + mEditText1.getText().toString())) {
                        @Override
                        public void onOpen(ServerHandshake serverHandshake) {
                            final URI uri = getURI();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText("已经链接到服务器" + uri);
                                }
                            });
                        }

                        @Override
                        public void onMessage(final String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String s1 = mTextView.getText().toString();
                                    String s2 = s1 + "\r\n" + s;
                                    mTextView.setText(s2);
                                }
                            });

                        }

                        @Override
                        public void onClose(int i, String s, boolean b) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(mTextView.getText().toString() + "\r\n服务器关闭");
                                    mWebSocketClient = null;
                                }
                            });

                        }

                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText("出错");
                                    mWebSocketClient = null;
                                }
                            });

                        }
                    };
                    mWebSocketClient.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    mWebSocketClient = null;
                }
                break;
            case R.id.send:
                if (mWebSocketClient != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", TextUtils.isEmpty(mEditText3.getText().toString()) ? "李逵" : mEditText3.getText().toString());
                        jsonObject.put("content", mEditText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mWebSocketClient.send(jsonObject.toString());
                    } catch (NotYetConnectedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
