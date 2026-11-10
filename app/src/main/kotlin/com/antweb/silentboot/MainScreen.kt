package com.antweb.silentboot

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onHelpNavigate: () -> Unit, onToggle: (enabled: Boolean) -> Unit) {
    // This would usually live in a view model
    // But to keep the app as small as possible, let's have this on a screen level for now
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    var isEnabled by remember {
        mutableStateOf(sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false))
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    modifier = Modifier.semantics { heading() },
                    text = stringResource(R.string.app_name)
                )
            })
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))

                EnableDisableSection(
                    isEnabled = isEnabled,
                    onToggle = { newValue ->
                        isEnabled = newValue
                        onToggle(newValue)
                    }
                )

                Spacer(Modifier.weight(1f))

                OutlinedButton(
                    onClick = onHelpNavigate,
                    colors = ButtonDefaults.buttonColors(),
                    modifier = Modifier.defaultMinSize(minWidth = 120.dp),
                ) {
                    Text(stringResource(R.string.labelHelp))
                }
            }
        }
    }
}

@Composable
private fun EnableDisableSection(
    isEnabled: Boolean,
    onToggle: (enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateDesc = if (isEnabled) {
        stringResource(R.string.stateDescriptionEnabled)
    } else {
        stringResource(R.string.stateDescriptionDisabled)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .toggleable(
                value = isEnabled,
                onValueChange = { onToggle(it) }
            )
            .padding(16.dp)
            .clearAndSetSemantics {
                stateDescription = stateDesc
                toggleableState = ToggleableState(isEnabled)
                role = Role.Switch
            },
    ) {
        val image = if (isEnabled) {
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

        val summary = if (isEnabled) {
            stringResource(R.string.summaryEnabled)
        } else {
            stringResource(R.string.summaryDisabled)
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = null,
        )

        Spacer(Modifier.height(16.dp))

        Text(summary)
    }
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
