package com.piplog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.piplog.app.ui.navigation.BottomNavItem
import com.piplog.app.ui.navigation.Screen
import com.piplog.app.ui.theme.*
import com.piplog.app.ui.navigation.PipLogNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PipLogTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    BottomNavItem(Screen.Dashboard, Icons.Default.Dashboard, "Home"),
                    BottomNavItem(Screen.Calendar, Icons.Default.History, "Calendar"),
                    BottomNavItem(Screen.Analytics, Icons.Default.BarChart, "Analytics"),
                    BottomNavItem(Screen.Profile, Icons.Default.Person, "Profile")
                )

                val showBottomBar = currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.Calendar.route,
                    Screen.Analytics.route,
                    Screen.Profile.route
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            // Standard scaffold bottomBar is usually flat, we want it to float.
                            // We will place the dock in the Box overlay instead for better control.
                        }
                    ) { innerPadding ->
                        ThemedBackground(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Surface(
                                color = Color.Transparent,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = if (showBottomBar) 112.dp else innerPadding.calculateBottomPadding())
                            ) {
                                PipLogNavHost(navController = navController)
                            }
                        }
                    }

                    if (showBottomBar) {
                        GlassmorphicDock(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                            items = bottomNavItems,
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(Screen.Dashboard.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onAddClick = {
                                navController.navigate(Screen.AddTrade.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassmorphicDock(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    
    // Proportions matched to reference image
    val dockBgColor = if (isLight) {
        Color.White.copy(alpha = 0.85f)
    } else {
        Color(0xFF0D1B2A).copy(alpha = 0.85f)
    }
    
    val dockBorderColor = if (isLight) {
        Color.Black.copy(alpha = 0.15f)
    } else {
        Color.White.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Shadow & Background (Blurred)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(28.dp))
                .background(dockBgColor)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            dockBorderColor,
                            dockBorderColor.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        )

        // LAYER 2: Content (Sharp)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Items
            items.take(2).forEach { item ->
                DockItem(
                    item = item,
                    isSelected = currentRoute == item.screen.route,
                    onClick = { onNavigate(item.screen.route) }
                )
            }

            // Central "+" Button (Elevated)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .offset(y = (-2).dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = Color(0xFF3B82F6),
                        spotColor = Color(0xFF3B82F6)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6), // Vibrant Blue
                                Color(0xFF1D4ED8)  // Deep Blue
                            )
                        )
                    )
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Right Items
            items.drop(2).forEach { item ->
                DockItem(
                    item = item,
                    isSelected = currentRoute == item.screen.route,
                    onClick = { onNavigate(item.screen.route) }
                )
            }
        }
    }
}

@Composable
fun DockItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    // Higher contrast colors for icons and text
    val activeColor = if (isLight) Color(0xFF1D4ED8) else Color.White
    val inactiveColor = if (isLight) Color(0xFF475569) else Color(0xFF94A3B8)

    val color by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(200)
    )

    Column(
        modifier = Modifier
            .width(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = color,
            modifier = Modifier
                .size(22.dp)
                .scale(scale)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            ),
            color = color
        )
    }
}
