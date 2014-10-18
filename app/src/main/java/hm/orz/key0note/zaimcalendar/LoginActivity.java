package hm.orz.key0note.zaimcalendar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class LoginActivity extends ActionBarActivity {

    private static final String CALLBACK = "http://zaimcalendar_callback/";
    private static final String LOGIN_COMPLETE_URL = "https://auth.zaim.net/users/auth";

    private ZaimOAuthClient mZaimOAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(this.getClass().getName(), "onPageFinished url = " + url);

                if (url.equals(LOGIN_COMPLETE_URL)) {
                    webView.loadUrl("javascript:window.alert(document.getElementsByTagName(\'code\')[0].innerHTML);");
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(this.getClass().getName(), "url = " + url);
                Log.d(this.getClass().getName(), "message = " + message);

                // argument message is OAuth Verifier
                mZaimOAuthClient.access(message, new ZaimOAuthClient.AccessCallback() {
                    @Override
                    public void onComplete() {
                        Log.d(this.getClass().getName(), "ACCESS_TOKEN : " + mZaimOAuthClient.getToken());
                        Log.d(this.getClass().getName(), "ACCESS_TOKEN_SECRET : " + mZaimOAuthClient.getTokenSecret());

                        // back to main activity
                        finish();
                    }
                });

                return true;
            }
        });

        mZaimOAuthClient = new ZaimOAuthClient();
        mZaimOAuthClient.request(CALLBACK, new ZaimOAuthClient.RequestCallback() {
            @Override
            public void onComplete(String authUrl) {
                Log.d(this.getClass().getName(), "authUrl = " + authUrl);
                webView.loadUrl(authUrl);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.web_view);
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
