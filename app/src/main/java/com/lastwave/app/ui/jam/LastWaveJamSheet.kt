package com.lastwave.app.ui.jam

import com.lastwave.app.util.appText

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lastwave.app.R
import com.lastwave.app.data.jam.ChatMessagePayload
import com.lastwave.app.data.jam.JamProtocolConstants
import com.lastwave.app.data.jam.JamRepository
import com.lastwave.app.data.jam.ParticipantPayload
import com.lastwave.app.ui.common.ArtworkImage

private fun formatMs(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%02d:%02d", min, sec)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastWaveJamSheet(
    viewModel: LastWaveJamViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val jamProgress by viewModel.progress.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()
    val cooldownUntil by viewModel.cooldownUntilMs.collectAsStateWithLifecycle()
    val roomCounts by viewModel.roomCounts.collectAsStateWithLifecycle()
    val locked = cooldownUntil > 0L
    LaunchedEffect(uiState.isConnected) {
        while (!uiState.isConnected) {
            viewModel.refreshRoomCounts()
            kotlinx.coroutines.delay(15_000L)
        }
    }
    var replyTo by remember { mutableStateOf<com.lastwave.app.data.jam.ChatMessagePayload?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val isDark = com.lastwave.app.ui.theme.LocalIsDarkTheme.current
    val LimeAccent = if (isDark) Color(0xFFC9FB00) else Color(0xFF2E6B12)
    val DarkTeal = if (isDark) Color(0xFF012226) else Color.White

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var chatInputText by remember { mutableStateOf("") }
    var showAdvancedServer by remember { mutableStateOf(false) }
    var customServerUrlInput by remember(uiState.serverUrl) { mutableStateOf(uiState.serverUrl) }

    val chatListState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LimeAccent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lastwave_logo_mono),
                            contentDescription = null,
                            tint = DarkTeal,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = appText(com.lastwave.app.R.string.settings_jam_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LimeAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = if (uiState.isConnected) appText(com.lastwave.app.R.string.ui_extra_6990f01ad9) else if (uiState.isConnecting) appText(com.lastwave.app.R.string.ui_connecting_48b998) else appText(com.lastwave.app.R.string.ui_listen_together_7b3aef),
                                    color = LimeAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = appText(com.lastwave.app.R.string.recognize_close))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!uiState.isConnected) {
                // Pre-connection: SimpMusic / Metrolist segmented tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = LimeAccent,
                        )
                    },
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(appText(com.lastwave.app.R.string.ui_join_room_902fc2), fontWeight = FontWeight.Bold) },
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(appText(com.lastwave.app.R.string.ui_create_room_7a913e), fontWeight = FontWeight.Bold) },
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display Name field (persisted across sessions)
                OutlinedTextField(
                    value = uiState.pendingDisplayName,
                    onValueChange = { viewModel.setPendingDisplayName(it) },
                    label = { Text(appText(com.lastwave.app.R.string.ui_your_display_name_ade88f)) },
                    placeholder = { Text(appText(com.lastwave.app.R.string.ui_listener_37ea46)) },
                    leadingIcon = {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = LimeAccent)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                    ),
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTabIndex == 0) {
                    JamStationsList(
                        accent = LimeAccent,
                        onAccent = DarkTeal,
                        enabled = !uiState.isConnecting,
                        onJoin = { viewModel.joinJamSession(it) },
                        counts = roomCounts,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Join Room Tab
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = appText(com.lastwave.app.R.string.ui_enter_6_character_room_code_517686),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )

                            OutlinedTextField(
                                value = uiState.pendingInputCode,
                                onValueChange = { viewModel.setPendingCode(it.take(6)) },
                                label = { Text(appText(com.lastwave.app.R.string.ui_room_code_a66508)) },
                                placeholder = { Text(appText(com.lastwave.app.R.string.ui_e_g_l86nsd_711ef8)) },
                                trailingIcon = {
                                    TextButton(
                                        onClick = {
                                            val clip = clipboard.getText()?.text?.trim().orEmpty()
                                            if (clip.isNotBlank()) {
                                                viewModel.setPendingCode(com.lastwave.app.data.jam.parseJamCode(clip).take(6))
                                            }
                                        },
                                    ) {
                                        Text(appText(com.lastwave.app.R.string.ui_extra_a204ac9b05), color = LimeAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 4.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LimeAccent,
                                ),
                            )

                            Button(
                                onClick = { viewModel.joinJamSession() },
                                enabled = uiState.pendingInputCode.length >= 4 && !uiState.isConnecting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LimeAccent,
                                    contentColor = DarkTeal,
                                ),
                            ) {
                                Text(
                                    text = if (uiState.isConnecting) appText(com.lastwave.app.R.string.ui_connecting_to_room_b245a7) else appText(com.lastwave.app.R.string.ui_join_jam_room_d6ece9),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                } else {
                    // Create Room Tab
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = appText(com.lastwave.app.R.string.ui_room_permissions_control_27171b),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )

                            // Collaborative Option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE) 1.5.dp else 1.dp,
                                        color = if (uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE) LimeAccent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable { viewModel.setPendingRoomMode(JamProtocolConstants.MODE_COLLABORATIVE) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE,
                                    onClick = { viewModel.setPendingRoomMode(JamProtocolConstants.MODE_COLLABORATIVE) },
                                    colors = RadioButtonDefaults.colors(selectedColor = LimeAccent),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(appText(com.lastwave.app.R.string.ui_collaborative_default_2ecbc5), fontWeight = FontWeight.Bold)
                                    Text(
                                        appText(com.lastwave.app.R.string.ui_anyone_in_room_can_play_pause_seek_and_change_so_e64f25),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            // Host Only Option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY) 1.5.dp else 1.dp,
                                        color = if (uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY) LimeAccent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable { viewModel.setPendingRoomMode(JamProtocolConstants.MODE_HOST_ONLY) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY,
                                    onClick = { viewModel.setPendingRoomMode(JamProtocolConstants.MODE_HOST_ONLY) },
                                    colors = RadioButtonDefaults.colors(selectedColor = LimeAccent),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(appText(com.lastwave.app.R.string.ui_host_only_broadcast_ed3937), fontWeight = FontWeight.Bold)
                                    Text(
                                        appText(com.lastwave.app.R.string.ui_only_you_control_playback_guests_listen_along_in_ab2d69),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.startJamSession() },
                                enabled = !uiState.isConnecting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LimeAccent,
                                    contentColor = DarkTeal,
                                ),
                            ) {
                                Icon(Icons.Filled.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isConnecting) appText(com.lastwave.app.R.string.ui_creating_room_47cdda) else appText(com.lastwave.app.R.string.ui_start_jam_session_cb8d60),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Expandable Custom Server Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showAdvancedServer = !showAdvancedServer }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(appText(com.lastwave.app.R.string.ui_custom_server_settings_bff284), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        if (showAdvancedServer) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                AnimatedVisibility(visible = showAdvancedServer) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = customServerUrlInput,
                            onValueChange = { customServerUrlInput = it },
                            label = { Text(appText(com.lastwave.app.R.string.ui_websocket_server_url_371447), fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.resetServerUrl()
                                    customServerUrlInput = JamRepository.DEFAULT_JAM_SERVER_WS
                                    Toast.makeText(context, appText(com.lastwave.app.R.string.ui_reset_to_default_server_50b81d), Toast.LENGTH_SHORT).show()
                                },
                            ) {
                                Text(appText(com.lastwave.app.R.string.ui_reset_default_c46b7a), fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.setCustomServerUrl(customServerUrlInput)
                                    Toast.makeText(context, appText(com.lastwave.app.R.string.ui_server_url_saved_3d183e), Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                            ) {
                                Text(appText(com.lastwave.app.R.string.ui_save_server_465cac), fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                // In Active Room State
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = if (uiState.roomName.isEmpty()) appText(com.lastwave.app.R.string.ui_room_code_8572d5) else appText(com.lastwave.app.R.string.ui_1_s_room_code_bd8068, (uiState.roomName.uppercase()).toString()),
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                        )

                        Text(
                            text = uiState.roomCode,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 5.sp,
                            color = LimeAccent,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 2.dp),
                        ) {
                            OutlinedButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(uiState.roomCode))
                                    Toast.makeText(context, appText(com.lastwave.app.R.string.ui_code_copied_1_s_d6fb47, (uiState.roomCode).toString()), Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(appText(com.lastwave.app.R.string.ui_copy_code_1589b6))
                            }

                            Button(
                                onClick = { viewModel.shareInvite(uiState.roomCode) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LimeAccent,
                                    contentColor = DarkTeal,
                                ),
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(appText(com.lastwave.app.R.string.ui_share_invite_758c66), fontWeight = FontWeight.Bold)
                            }
                        }

                        // Room Mode toggle (Host can live-toggle between Collaborative and Host-Only)
                        if (uiState.isHost) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp),
                            ) {
                                Text(
                                    text = appText(com.lastwave.app.R.string.ui_mode_fbb6d6),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE) LimeAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .clickable { viewModel.changeActiveRoomMode(JamProtocolConstants.MODE_COLLABORATIVE) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = appText(com.lastwave.app.R.string.ui_collaborative_67efce),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE) LimeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY) LimeAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .clickable { viewModel.changeActiveRoomMode(JamProtocolConstants.MODE_HOST_ONLY) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = appText(com.lastwave.app.R.string.ui_host_only_b2a141),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY) LimeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = appText(com.lastwave.app.R.string.ui_live_sync_1_s_e868c3, (if (uiState.roomMode == JamProtocolConstants.MODE_HOST_ONLY) appText(com.lastwave.app.R.string.ui_host_only_b2a141) else appText(com.lastwave.app.R.string.ui_collaborative_67efce)).toString()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Synced Now Playing Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                            ) {
                                ArtworkImage(
                                    name = uiState.currentTrackTitle,
                                    artist = uiState.currentTrackArtist,
                                    embeddedUrl = uiState.currentArtworkUrl,
                                    fallbackIcon = Icons.Filled.MusicNote,
                                    modifier = Modifier.size(46.dp),
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.currentTrackTitle.ifBlank { appText(com.lastwave.app.R.string.ui_no_track_playing_326cae) },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = uiState.currentTrackArtist.ifBlank { appText(com.lastwave.app.R.string.ui_jam_sync_47885d) } +
                                        if (jamProgress.durationMs > 0L) " \u2022 ${formatMs(jamProgress.positionMs)} / ${formatMs(jamProgress.durationMs)}" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Playback Controls (Enabled if host or collaborative)
                        val canControl = uiState.isHost || uiState.roomMode == JamProtocolConstants.MODE_COLLABORATIVE
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.togglePlayback() },
                                enabled = canControl && !(locked && uiState.isPlaying),
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (uiState.isPlaying) appText(com.lastwave.app.R.string.wave_widget_pause) else appText(com.lastwave.app.R.string.dl_play_cd),
                                    tint = if (canControl) LimeAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                )
                            }
                            IconButton(
                                onClick = { viewModel.skipNext() },
                                enabled = canControl && !locked,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = appText(com.lastwave.app.R.string.ui_skip_3da474),
                                    tint = if (canControl) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                JamCooldownHint(cooldownUntil, Modifier.padding(bottom = 6.dp))
                JamUpNext(
                    queue = uiState.queue,
                    isAdmin = uiState.isMainHost,
                    accent = LimeAccent,
                )
                Spacer(modifier = Modifier.height(10.dp))

                JamNoticeBanner(
                    notice = notice,
                    onDismiss = viewModel::clearNotice,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                // Participants Horizontal Strip
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_listeners_1_s_906cd4, (uiState.participants.size).toString()),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.participants) { p ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (p.isHost) LimeAccent else MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = p.name.firstOrNull()?.uppercase() ?: "L",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (p.isHost) DarkTeal else Color.White,
                                    )
                                }
                                Text(
                                    text = p.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                )
                                if (p.isHost) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(LimeAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp),
                                    ) {
                                        Text(roleBadge(p, uiState.isRadio), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LimeAccent)
                                    }
                                }
                                HostActionButton(
                                    participant = p,
                                    myUserId = uiState.myUserId,
                                    isMainHost = uiState.isMainHost,
                                    coHostId = uiState.coHostId,
                                    accent = LimeAccent,
                                    onGrant = viewModel::grantHost,
                                    onRevoke = viewModel::revokeHost,
                                    isRadio = uiState.isRadio,
                                    onMakeAdmin = viewModel::makeAdmin,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // In-room live chat
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        LazyColumn(
                            state = chatListState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            if (chatMessages.isEmpty()) {
                                item {
                                    Text(
                                        text = appText(com.lastwave.app.R.string.ui_in_room_chat_is_ready_say_hello_957d23),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 12.dp),
                                    )
                                }
                            } else {
                                items(chatMessages) { msg ->
                                    Column(modifier = Modifier.clickable { replyTo = msg }) {
                                        msg.replyTo?.let { ReplyQuote(it, LimeAccent) }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                text = msg.userName + ":",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (msg.isHost) LimeAccent else MaterialTheme.colorScheme.primary,
                                            )
                                            Text(
                                                text = msg.text,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        replyTo?.let { target ->
                            ReplyPreviewBar(
                                message = target,
                                accent = LimeAccent,
                                onCancel = { replyTo = null },
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = { Text(appText(com.lastwave.app.R.string.ui_type_a_message_09bdff), fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                            )

                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        viewModel.sendChat(chatInputText, replyTo?.id)
                                        chatInputText = ""
                                        replyTo = null
                                    }
                                },
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = appText(com.lastwave.app.R.string.ui_send_9bc257), tint = LimeAccent)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.leaveSession() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                ) {
                    Text(if (uiState.isHost) appText(com.lastwave.app.R.string.ui_leave_end_jam_session_6fde9e) else appText(com.lastwave.app.R.string.ui_leave_jam_session_0a7836), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
