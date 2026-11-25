package es.appblocks.plantilla;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

/**
 * Plantilla de AppBlocks: una app Android que solo muestra, a pantalla completa,
 * la página guardada en assets/www/index.html. AppBlocks sustituye esa página
 * por la app de cada alumno.
 */
public class MainActivity extends Activity {

    private static final int PEDIR_CAMARA_MICRO = 1;
    private static final int PEDIR_UBICACION = 2;
    private static final int ELEGIR_ARCHIVO = 3;

    private WebView web;
    private PermissionRequest peticionWeb;
    private GeolocationPermissions.Callback avisoUbicacion;
    private String origenUbicacion;
    private ValueCallback<Uri[]> avisoArchivo;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        setContentView(web);
        pantallaCompleta();

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setGeolocationEnabled(true);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setTextZoom(100);

        // Los enlaces a otras webs se abren en el navegador del móvil
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception e) { }
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest peticion) {
                runOnUiThread(() -> permisosParaLaWeb(peticion));
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origen, GeolocationPermissions.Callback aviso) {
                if (tiene(Manifest.permission.ACCESS_FINE_LOCATION) || tiene(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    aviso.invoke(origen, true, false);
                    return;
                }
                origenUbicacion = origen;
                avisoUbicacion = aviso;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PEDIR_UBICACION);
            }

            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> aviso, FileChooserParams params) {
                if (avisoArchivo != null) avisoArchivo.onReceiveValue(null);
                avisoArchivo = aviso;
                try {
                    startActivityForResult(params.createIntent(), ELEGIR_ARCHIVO);
                } catch (Exception e) {
                    avisoArchivo = null;
                    return false;
                }
                return true;
            }
        });

        if (estado != null) web.restoreState(estado);
        else web.loadUrl("file:///android_asset/www/index.html");
    }

    private boolean tiene(String permiso) {
        return checkSelfPermission(permiso) == PackageManager.PERMISSION_GRANTED;
    }

    private void permisosParaLaWeb(PermissionRequest peticion) {
        ArrayList<String> faltan = new ArrayList<>();
        for (String r : peticion.getResources()) {
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(r) && !tiene(Manifest.permission.CAMERA)) faltan.add(Manifest.permission.CAMERA);
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r) && !tiene(Manifest.permission.RECORD_AUDIO)) faltan.add(Manifest.permission.RECORD_AUDIO);
        }
        if (faltan.isEmpty()) {
            peticion.grant(peticion.getResources());
            return;
        }
        peticionWeb = peticion;
        requestPermissions(faltan.toArray(new String[0]), PEDIR_CAMARA_MICRO);
    }

    @Override
    public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);
        boolean todos = resultados.length > 0;
        for (int r : resultados) if (r != PackageManager.PERMISSION_GRANTED) todos = false;
        if (codigo == PEDIR_CAMARA_MICRO && peticionWeb != null) {
            if (todos) peticionWeb.grant(peticionWeb.getResources());
            else peticionWeb.deny();
            peticionWeb = null;
        }
        if (codigo == PEDIR_UBICACION && avisoUbicacion != null) {
            boolean ok = tiene(Manifest.permission.ACCESS_FINE_LOCATION) || tiene(Manifest.permission.ACCESS_COARSE_LOCATION);
            avisoUbicacion.invoke(origenUbicacion, ok, false);
            avisoUbicacion = null;
        }
    }

    @Override
    protected void onActivityResult(int codigo, int resultado, Intent datos) {
        super.onActivityResult(codigo, resultado, datos);
        if (codigo == ELEGIR_ARCHIVO && avisoArchivo != null) {
            avisoArchivo.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultado, datos));
            avisoArchivo = null;
        }
    }

    @SuppressWarnings("deprecation")
    private void pantallaCompleta() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
            WindowInsetsController c = w.getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            w.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean conFoco) {
        super.onWindowFocusChanged(conFoco);
        if (conFoco) pantallaCompleta();
    }

    // El botón Atrás del móvil vuelve a la pantalla anterior de la app
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle salida) {
        super.onSaveInstanceState(salida);
        if (web != null) web.saveState(salida);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (web != null) web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
    }
}
