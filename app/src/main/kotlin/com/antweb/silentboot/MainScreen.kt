package com.antweb.silentboot

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager

@Composable
fun MainScreen(onHelpNavigate: () -> Unit, onToggle: (enabled: Boolean) -> Unit) {
    // This would usually live in a view model
    // But to keep the app as small as possible, let's have this on a screen level for now
    val context = LocalContext.current
    var isEnabled by remember {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        mutableStateOf(sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false))
    }

    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }

    var hasNotificationPermission by remember {
        mutableStateOf(checkNotificationPermission(context))
    }

    val isPreview = LocalInspectionMode.current
    var hasModesPermission by remember {
        if (isPreview) {
            mutableStateOf(false)
        } else {
            mutableStateOf(checkModesPermission(context))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    val settingsLauncher = rememberLauncherForActivityResult(OpenSettingsResultContract()) {
        hasModesPermission = checkModesPermission(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        if (showPermissionDialog) {
            PermissionDialog(
                onConfirmation = {
                    showPermissionDialog = false
                    settingsLauncher.launch(Unit)
                },
                onDismissRequest = { showPermissionDialog = false },
            )
        }

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(48.dp))

                EnableDisableSection(
                    isEnabled = isEnabled,
                    onToggle = { newValue ->
                        isEnabled = newValue
                        onToggle(newValue)
                    },
                )

                Spacer(Modifier.height(24.dp))

                PermissionInfo(
                    hasNotificationPermission = hasNotificationPermission,
                    onNotificationPermissionClick = {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    hasModesPermission = hasModesPermission,
                    onModesPermissionClick = { showPermissionDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.weight(1f))

                OutlinedButton(
                    onClick = onHelpNavigate,
                    colors = ButtonDefaults.buttonColors(),
                    modifier = Modifier.defaultMinSize(minWidth = 120.dp),
                ) {
                    Text(stringResource(R.string.labelHelp))
                }

                VersionText(context, Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun EnableDisableSection(
    isEnabled: Boolean,
    onToggle: (enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stateDesc =
        if (isEnabled) {
            stringResource(R.string.stateDescriptionEnabled)
        } else {
            stringResource(R.string.stateDescriptionDisabled)
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .toggleable(
                    value = isEnabled,
                    onValueChange = { onToggle(it) },
                )
                .padding(16.dp)
                .clearAndSetSemantics {
                    stateDescription = stateDesc
                    toggleableState = ToggleableState(isEnabled)
                    role = Role.Switch
                },
    ) {
        val image =
            if (isEnabled) {
                painterResource(R.drawable.status_image_enabled)
            } else {
                painterResource(R.drawable.status_image_disabled)
            }

        Image(
            painter = image,
            modifier = Modifier.size(150.dp),
            contentDescription = "",
        )

        Spacer(Modifier.height(32.dp))

        val summary =
            if (isEnabled) {
                stringResource(R.string.summaryEnabled)
            } else {
                stringResource(R.string.summaryDisabled)
            }

        Switch(
            checked = isEnabled,
            onCheckedChange = null,
        )

        Spacer(Modifier.height(16.dp))

        Text(textAlign = TextAlign.Center, text = summary)
    }
}

@Composable
private fun PermissionDialog(onDismissRequest: () -> Unit, onConfirmation: () -> Unit) {
    AlertDialog(
        title = { Text(stringResource(R.string.permissionDialogTitle)) },
        text = { Text(stringResource(R.string.permissionDialogText)) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(stringResource(R.string.permissionDialogEnable))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.permissionDialogCancel))
            }
        },
    )
}

@Composable
private fun PermissionInfo(
    hasNotificationPermission: Boolean,
    onNotificationPermissionClick: () -> Unit,
    hasModesPermission: Boolean,
    onModesPermissionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        val count = 2

        PermissionListItem(
            hasPermission = hasNotificationPermission,
            onClick = onNotificationPermissionClick,
            content = { Text(stringResource(R.string.notificationPermissionLabel)) },
            count = count,
            index = 0,
        )

        PermissionListItem(
            hasPermission = hasModesPermission,
            onClick = onModesPermissionClick,
            content = { Text(stringResource(R.string.modesPermissionLabel)) },
            count = count,
            index = 1,
        )
    }
}

@Composable
private fun PermissionListItem(
    hasPermission: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    count: Int,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val colors =
        ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)

    // No option to conditionally specify an onClick action?
    // Use a different overload if not clickable to get proper screen reader semantics
    if (hasPermission) {
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index, count),
            colors = colors,
            content = content,
            supportingContent = { Text(stringResource(R.string.permissionGrantedLabel)) },
            modifier = modifier,
        )
    } else {
        SegmentedListItem(
            onClick = onClick,
            shapes = ListItemDefaults.segmentedShapes(index, count),
            colors = colors,
            content = content,
            supportingContent = { Text(stringResource(R.string.permissionNotGrantedLabel)) },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.baseline_error_24),
                    contentDescription = stringResource(
                        R.string.permissionRequiresAttentionContentDescription,
                    ),
                )
            },
            modifier = modifier,
        )
    }
}

@Composable
private fun VersionText(context: Context, modifier: Modifier = Modifier) {
    val version = if (!LocalInspectionMode.current) {
        // Throws UnsupportedOperationException in preview
        context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.PackageInfoFlags.of(0),
        ).versionName
    } else {
        "4.0"
    }

    if (version != null) {
        Text(
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall,
            text = stringResource(R.string.labelVersion, version),
        )
    }
}

private fun checkNotificationPermission(context: Context): Boolean =
    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

private fun checkModesPermission(context: Context): Boolean {
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    return notificationManager.isNotificationPolicyAccessGranted
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    SilentBootTheme {
        MainScreen(
            onHelpNavigate = {},
            onToggle = {},
        )
    }
}
