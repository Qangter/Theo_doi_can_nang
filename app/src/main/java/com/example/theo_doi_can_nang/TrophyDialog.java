package com.example.theo_doi_can_nang;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.webkit.WebViewAssetLoader;

import com.example.theo_doi_can_nang.data.Achievement;

public class TrophyDialog {

    public static void show(Context context, Achievement achievement) {
        if (context == null || achievement == null) {
            return;
        }

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_trophy, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvTrophyTitle = view.findViewById(R.id.tvTrophyTitle);
        TextView tvTrophyDescription = view.findViewById(R.id.tvTrophyDescription);
        WebView webView3D = view.findViewById(R.id.webView3D);
        Button btnDismiss = view.findViewById(R.id.btnDismiss);

        tvTrophyTitle.setText(achievement.getTitle());
        tvTrophyDescription.setText(achievement.getDescription());

        // WebViewAssetLoader giúp nạp file .glb trong assets qua HTTPS URL ảo mà không bị lỗi CORS Security
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
                .build();

        webView3D.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        // Cấu hình WebView hiển thị Mô hình 3D xoay 360 độ
        WebSettings settings = webView3D.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView3D.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView3D.setBackgroundColor(Color.TRANSPARENT);

        String modelFile = achievement.getModelFileName() != null ? achievement.getModelFileName() : "beginning_trophy.glb";

        // Encode the query value so a future asset name cannot break the viewer URL.
        String viewerUrl = "https://appassets.androidplatform.net/assets/three_viewer.html?model="
                + Uri.encode(modelFile);
        webView3D.loadUrl(viewerUrl);

        btnDismiss.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
