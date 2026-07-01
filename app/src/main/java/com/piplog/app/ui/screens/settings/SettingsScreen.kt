package com.piplog.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piplog.app.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val themeViewModel = LocalThemeViewModel.current
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val scrollState = rememberScrollState()

    ThemedBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // --- APPEARANCE SECTION ---
        SettingsSectionHeader("APPEARANCE")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppearanceCard(
                title = "Dark",
                subtitle = "Deep navy",
                icon = Icons.Default.DarkMode,
                isSelected = isDarkMode,
                onClick = { themeViewModel.setDarkMode(true) },
                modifier = Modifier.weight(1f)
            )
            AppearanceCard(
                title = "Light",
                subtitle = "Bright clean",
                icon = Icons.Default.LightMode,
                isSelected = !isDarkMode,
                onClick = { themeViewModel.setDarkMode(false) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- ACCOUNT SECTION ---
        SettingsSectionHeader("ACCOUNT")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            SettingsListItem(
                icon = Icons.Outlined.Person,
                title = "Profile",
                onClick = { /* Navigate to Profile */ }
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), thickness = 1.dp)
            SettingsListItem(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                onClick = { /* Navigate to Notifications */ }
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), thickness = 1.dp)
            SettingsListItem(
                icon = Icons.Outlined.Shield,
                title = "Security",
                onClick = { /* Navigate to Security */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SUPPORT SECTION ---
        SettingsSectionHeader("SUPPORT")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            SettingsListItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help & feedback",
                onClick = { /* Navigate to Help */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SIGN OUT BUTTON ---
        Surface(
            onClick = onSignOut,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }

        // RESERVED SPACE FOR FLOATING DOCK
        Spacer(modifier = Modifier.height(112.dp))
    }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun AppearanceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Primary else Color.Transparent
    val bgColor = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) 
                  else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)

    Surface(
        onClick = onClick,
        color = bgColor,
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) BorderStroke(2.dp, Primary) else null,
        modifier = modifier.height(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Primary else Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}
