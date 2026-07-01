package com.piplog.app.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.piplog.app.ui.components.*
import com.piplog.app.ui.screens.settings.SettingsScreen
import com.piplog.app.ui.theme.*
import com.piplog.app.utils.TradeUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddTrade: () -> Unit,
    onNavigateToTrades: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val today = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.US).format(Date()).uppercase()
    }
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    ThemedBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp) // Space for floating dock
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showSettingsSheet = true }) {
                    Icon(
                        Icons.Outlined.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                PipLogLogo(size = 32.dp)
            }

            // Greeting Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = today,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$greeting, ",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = (uiState.profile?.displayName ?: "TREVOR").uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " Protect capital first. Profits follow.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Market Overview
            MarketOverviewSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Account Balance Card
            AccountBalanceCard(uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Metrics Grid
            MetricsGrid(uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add",
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    onClick = onNavigateToAddTrade
                )
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = "History",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTrades
                )
                QuickActionButton(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Calendar",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCalendar
                )
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = "Journal",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToJournal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Section
            ChartSection(uiState)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Settings Bottom Sheet
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                scrimColor = Color.Black.copy(alpha = 0.4f),
                dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)) },
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier.fillMaxHeight(0.85f)
            ) {
                SettingsScreen(
                    onNavigateBack = { showSettingsSheet = false },
                    onSignOut = {
                        showSettingsSheet = false
                        // Handle sign out logic or navigation
                    }
                )
            }
        }
    }
}

@Composable
fun AccountBalanceCard(uiState: DashboardUiState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACCOUNT BALANCE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            
            Surface(
                color = Color.Red.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = " -26.60%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = TradeUtils.formatCurrency(uiState.metrics?.balance ?: 7340.0),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        Text(
            text = "Equity ${TradeUtils.formatCurrency(uiState.metrics?.balance ?: 7340.0)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ChartSection(uiState: DashboardUiState) {
    var selectedPeriod by remember { mutableStateOf("1M") }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Period Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("1D", "1W", "1M", "3M", "1Y", "ALL").forEach { period ->
                val isSelected = period == selectedPeriod
                Surface(
                    onClick = { selectedPeriod = period },
                    color = if (isSelected) Primary else Color.Transparent,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Interactive Smooth Chart
        InteractiveSmoothChart(
            period = selectedPeriod,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun InteractiveSmoothChart(
    period: String,
    modifier: Modifier = Modifier
) {
    // Generate dummy data based on period for demonstration
    val points = remember(period) {
        val count = when(period) {
            "1D" -> 24
            "1W" -> 7
            "1M" -> 30
            else -> 20
        }
        List(count) { i ->
            i.toFloat() / (count - 1) to (0.2f + Math.random().toFloat() * 0.6f)
        }
    }

    var selectedPoint by remember { mutableStateOf<Offset?>(null) }
    val markerColor = MaterialTheme.colorScheme.onBackground
    val markerGlowColor = Primary.copy(alpha = 0.4f)
    val lineDashColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .pointerInput(period) {
                detectTapGestures { offset ->
                    selectedPoint = offset
                }
            }
            .pointerInput(period) {
                // For crosshair tracking
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pos = event.changes.first().position
                        selectedPoint = pos
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            if (points.isEmpty()) return@Canvas

            val linePath = androidx.compose.ui.graphics.Path()
            val scaledPoints = points.map { (x, y) -> 
                Offset(x * width, (1 - y) * height)
            }

            linePath.moveTo(scaledPoints[0].x, scaledPoints[0].y)
            
            // Draw smooth curve using cubic beziers
            for (i in 0 until scaledPoints.size - 1) {
                val p0 = scaledPoints[i]
                val p1 = scaledPoints[i + 1]
                
                val cx1 = p0.x + (p1.x - p0.x) / 2f
                val cy1 = p0.y
                val cx2 = p0.x + (p1.x - p0.x) / 2f
                val cy2 = p1.y
                
                linePath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
            }

            // Line stroke
            drawPath(
                path = linePath,
                brush = Brush.horizontalGradient(listOf(Primary, Color(0xFF64B5F6))),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Fill gradient
            val fillPath = androidx.compose.ui.graphics.Path().apply {
                addPath(linePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Primary.copy(alpha = 0.3f), Color.Transparent)
                )
            )

            // Draw selection marker and tooltip
            selectedPoint?.let { point ->
                // Find closest point on X axis
                val closestPoint = scaledPoints.minByOrNull { Math.abs(it.x - point.x) }
                closestPoint?.let { cp ->
                    // Glow effect at point
                    drawCircle(
                        color = markerColor,
                        radius = 6.dp.toPx(),
                        center = cp,
                        style = Fill
                    )
                    drawCircle(
                        color = markerGlowColor,
                        radius = 12.dp.toPx(),
                        center = cp,
                        style = Fill
                    )
                    
                    // Vertical line
                    drawLine(
                        color = lineDashColor,
                        start = Offset(cp.x, 0f),
                        end = Offset(cp.x, height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
        }
    }
}

@Composable
fun MarketOverviewSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Market Overview",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .blur(4.dp)
                )
                Text(
                    text = " LIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of Market Cards with micro-interactions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MarketCard("GBPUSD", 0.25, modifier = Modifier.weight(1f))
                MarketCard("EURUSD", -0.12, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MarketCard("USDJPY", 0.08, modifier = Modifier.weight(1f))
                MarketCard("XAUUSD", 1.10, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MarketCard(pair: String, change: Double, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f)

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { },
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pair,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (change >= 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (change >= 0) Color.Green else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${String.format("%.2f", Math.abs(change))}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (change >= 0) Color.Green else Color.Red
                    )
                )
            }
        }
    }
}

@Composable
fun MetricsGrid(uiState: DashboardUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricItem("NET P/L", "-$2,660...", Color.Red, modifier = Modifier.weight(1f))
        MetricItem("TODAY", "$0.00", MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        MetricItem("DRAWDOWN", "-84.4%", Color.Red, modifier = Modifier.weight(1f))
    }
}

@Composable
fun MetricItem(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        color = if (isPrimary) Primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = if (isPrimary) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        modifier = modifier.scale(scale)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
