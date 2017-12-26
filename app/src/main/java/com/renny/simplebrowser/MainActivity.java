package com.renny.simplebrowser;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebView;

public class MainActivity extends AppCompatActivity implements WebViewFragment.OnReceivedListener {
    private final String WEB_TAG = "simple_web";
    WebViewFragment webViewFragment;
    HomePageFragment mHomePageFragment;
    TextView titleView;
    GestureLayout mGestureLayout;
    private String homePage = "https://juejin.im/user/5795bb80d342d30059f14b1c";
    FragmentTransaction trans;
    private boolean isOnHomePage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleView = findViewById(R.id.title);
        mGestureLayout = findViewById(R.id.gesture_layout);
        trans = getSupportFragmentManager().beginTransaction();
        mHomePageFragment = new HomePageFragment();
        mHomePageFragment.setGoPageListener(new HomePageFragment.goPageListener() {
            @Override
            public void onGopage(String url) {
                goWebview(url);
            }
        });
        trans.add(R.id.container, mHomePageFragment);
        trans.addToBackStack(null);
        trans.commit();
        isOnHomePage = true;
        mGestureLayout.setGestureListener(new GestureLayout.GestureListener() {
            @Override
            public boolean dragStartedEnable(int edgeFlags, ImageView view) {
                if (webViewFragment == null) {
                    return false;
                }
                WebView webView = webViewFragment.getWebView();
                if (webView == null) {
                    return false;
                }
                WebBackForwardList list = webView.copyBackForwardList();
                int size = list.getSize();
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    return webView.canGoBack() || !isOnHomePage;
                } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                    if (isOnHomePage) {
                        return size > 0;
                    } else {
                        return webView.canGoForward();
                    }
                } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                    return !isOnHomePage;
                }
                return false;
            }

            @Override
            public void onViewMaxPositionReleased(int edgeFlags, ImageView view) {
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    backPreviousPage();
                } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                    goNextPage();
                } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                    goHomePage();
                }
            }

            @Override
            public void onViewMaxPositionArrive(int edgeFlags, ImageView view) {
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    view.setBackgroundResource(R.color.color_btn_major_deep);
                } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                    view.setBackgroundResource(R.color.color_btn_major_deep);
                } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                    view.setBackgroundResource(R.color.color_btn_major_deep);
                }
            }
        });


    }

    private void goWebview(String url) {
        if (webViewFragment == null || !TextUtils.isEmpty(url)) {
            webViewFragment = new WebViewFragment();
            Bundle args = new Bundle();
            args.putString("url", url);
            webViewFragment.setArguments(args);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                webViewFragment).commit();
        isOnHomePage = false;
    }

    private void goNextPage() {
        WebView webView = webViewFragment.getWebView();
        webView.goForward();

    }

    private void goHomePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                mHomePageFragment).commit();
        titleView.setText(" ");
        isOnHomePage = true;
    }

    private void backPreviousPage() {
        WebView webView = webViewFragment.getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            goHomePage();
        }
    }

    @Override
    public void onBackPressed() {
        WebView webView = webViewFragment.getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else super.onBackPressed();
    }

    @Override
    public void onReceivedTitle(String title) {
        titleView.setText(title);
    }


}