package com.plb.conference.ui

import android.Manifest
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.plb.conference.ui.theme.ConferenceTheme


/**
 * Activité principale gérant les réunions
 * Cette activité permet de prendre des photos et d'accéder à une WebView
 */
class MeetingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        enableEdgeToEdge()
        setContent {
            ConferenceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    MeetingsScreen(
                        onCameraPermissionDenied = { finish() }
                    )
                }
            }
        }
    }
}

/**
 * Écran principal des réunions
 */
@Composable
private fun MeetingsScreen(
    onCameraPermissionDenied: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Meetings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CameraScreen(onCameraPermissionDenied = onCameraPermissionDenied)
    }
}

/**
 * Gestion de la caméra et de la prise de photo
 * Inclut la gestion des permissions et l'affichage de la photo
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onCameraPermissionDenied: () -> Unit = {}
) {
    // États de l'interface
    var uri by remember { mutableStateOf<Uri?>(null) }
    var showPhoto by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var launchWebview by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Gestion des permissions de la caméra
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    ) { isGranted ->
        if (!isGranted) onCameraPermissionDenied()
    }

    // Launcher pour la prise de photo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        showPhoto = success
    }

    CameraContent(
        uri = uri,
        showPhoto = showPhoto,
        showDialog = showDialog,
        onTakePhotoClick = {
            if (cameraPermissionState.status.isGranted) {
                uri = PhotoUriManager(context).createPhotoUri()
                cameraLauncher.launch(uri)
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        onPhotoClick = { showDialog = true },
        onDismissDialog = { showDialog = false },
        onOpenWebView = {
            showDialog = false
            launchWebview = true
        }
    )

    // Affichage de la WebView
    if (launchWebview) {
        WebViewDialog(
            onDismiss = { launchWebview = false }
        )
    }
}

/**
 * Contenu de l'écran caméra
 */
@Composable
private fun CameraContent(
    uri: Uri?,
    showPhoto: Boolean,
    showDialog: Boolean,
    onTakePhotoClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onOpenWebView: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TakePhotoButton(onClick = onTakePhotoClick)

        if (showPhoto && uri != null) {
            PhotoPreview(uri = uri, onClick = onPhotoClick)
        }

        if (showDialog) {
            WebViewConfirmationDialog(
                onDismiss = onDismissDialog,
                onConfirm = onOpenWebView
            )
        }
    }
}

/**
 * Bouton de prise de photo
 */
@Composable
private fun TakePhotoButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
        onClick = onClick
    ) {
        Text("Take Photo")
    }
}

/**
 * Aperçu de la photo prise
 */
@Composable
private fun PhotoPreview(uri: Uri, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = "Photo",
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    )
}

/**
 * Dialogue de confirmation pour ouvrir la WebView
 */
@Composable
private fun WebViewConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Voulez-vous ouvrir la webview ?",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Vous allez être redirigé vers une page web.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Button(onClick = onConfirm) {
                        Text("Open")
                    }
                }
            }
        }
    }
}

/**
 * WebView personnalisée avec injection de recherche automatique
 */
@Composable
private fun WebViewDialog(onDismiss: () -> Unit) {
    val query = "plb"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CustomWebView(query = query)

            CloseButton(onClick = onDismiss)
        }
    }
}

/**
 * Composant WebView avec configuration sécurisée
 */
@Composable
private fun CustomWebView(query: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                configureWebViewSettings()
            }
        },
        update = { webView ->
            setupWebView(webView, query)
        }
    )
}

/**
 * Configuration des paramètres de la WebView
 */
private fun WebView.configureWebViewSettings() {
    settings.apply {
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = true
        displayZoomControls = false
        javaScriptEnabled = true
        domStorageEnabled = true
        cacheMode = WebSettings.LOAD_DEFAULT
        allowContentAccess = false
        allowFileAccess = false
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }
}

/**
 * Configuration du comportement de la WebView
 */
private fun setupWebView(webView: WebView, query: String) {
    webView.apply {
        // Activer JavaScript pour un fonctionnement normal des sites web
        settings.javaScriptEnabled = true

        // Cette ligne est la clé : elle permet de partager les cookies avec Chrome
        CookieManager.getInstance().apply {
            setAcceptThirdPartyCookies(webView, true)
            setAcceptCookie(true)
        }

        // Client WebView basique
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // La page est chargée avec les cookies partagés
                injectSearchQuery(view, query)
            }
        }

        loadUrl("https://google.com")
    }
}

/**
 * Injection du script de recherche dans la WebView
 */
private fun injectSearchQuery(view: WebView?, query: String) {
    view?.evaluateJavascript(
        """
        setTimeout(function() {
            var input = document.getElementById('search-form-input');
            if (input) {
                input.value = '$query';
            }
        }, 1000);
        """.trimIndent(),
        null
    )
}

/**
 * Bouton de fermeture de la WebView
 */
@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White
        )
    }
}

/**
 * Gestionnaire pour la création d'URI de photos
 */
class PhotoUriManager(private val context: Context) {
    fun createPhotoUri(): Uri? {
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
}