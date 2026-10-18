package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.util.CyberMatrixBackground
import com.example.util.cyberGlass
import com.example.util.performCyberHaptic

data class DashboardCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val prompt: String
)

@Composable
fun HomeDashboardView(
    userName: String,
    onPromptSelected: (String) -> Unit,
    onOpenTool: (Int) -> Unit,
    onPickImage: () -> Unit,
    onStartVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dashboardCards = listOf(
        DashboardCardItem("⚡ Ask Mivazo", "Universal Intelligence", Icons.Default.QuestionAnswer, CyberCyan, "Hello Mivazo, what are your top capabilities today, Boss?"),
        DashboardCardItem("✍️ AI Writer", "Stories, Emails & Copy", Icons.Default.Create, CyberCyan, "Write an inspiring futuristic story about AI and human synergy, Boss."),
        DashboardCardItem("💻 AI Coder", "Kotlin, Python, Web", Icons.Default.Code, CyberGreen, "Show me a clean, reactive Kotlin StateFlow architecture pattern for Android, Boss."),
        DashboardCardItem("🌎 Translator", "Multi-Language Engine", Icons.Default.Language, CyberPurple, "Translate: 'Welcome to Mohsin Website powered by Mivazo AI' into Urdu, Arabic, and French, Boss."),
        DashboardCardItem("🖼️ Analyze Image", "Multimodal Vision", Icons.Default.Image, CyberCyan, "Please analyze this image in detail and extract all key elements, Boss."),
        DashboardCardItem("🎤 Voice Chat", "Hands-Free Speech", Icons.Default.GraphicEq, CyberPurple, "I would like to have a conversational brainstorming session with you, Boss."),
        DashboardCardItem("📚 Summarize", "Bullet & Key Insights", Icons.Default.Notes, CyberPink, "Provide a crisp executive summary with bullet points, Boss:"),
        DashboardCardItem("💡 Ideas", "Brainstorm Innovation", Icons.Default.Lightbulb, Color(0xFFFFB300), "Give me 5 breakthrough AI startup concepts for 2026, Boss.")
    )

    val suggestionChips = listOf(
        "Write a sci-fi story",
        "Create an Android app",
        "Explain Quantum Computing",
        "Generate startup ideas",
        "Translate to Urdu"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Greeting Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .cyberGlass(cornerRadius = 20.dp, borderColor = CyberCyan.copy(alpha = 0.4f))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome back, $userName 👋",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mivazo AI Neural Core is online and ready for your commands.",
                        color = CyberCyan.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, CyberCyan, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cyber_core),
                        contentDescription = "Core Hologram",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "NEURAL COMMANDS & TOOLS",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Cyber Grid Cards
        for (i in dashboardCards.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardCard(
                    item = dashboardCards[i],
                    onClick = {
                        performCyberHaptic(context)
                        when (dashboardCards[i].title) {
                            "🖼️ Analyze Image" -> onPickImage()
                            "🎤 Voice Chat" -> onStartVoice()
                            "✍️ AI Writer" -> onOpenTool(0)
                            "🌎 Translator" -> onOpenTool(1)
                            "💻 AI Coder" -> onOpenTool(2)
                            "📚 Summarize" -> onOpenTool(3)
                            else -> onPromptSelected(dashboardCards[i].prompt)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                if (i + 1 < dashboardCards.size) {
                    DashboardCard(
                        item = dashboardCards[i + 1],
                        onClick = {
                            performCyberHaptic(context)
                            when (dashboardCards[i + 1].title) {
                                "🖼️ Analyze Image" -> onPickImage()
                                "🎤 Voice Chat" -> onStartVoice()
                                "✍️ AI Writer" -> onOpenTool(0)
                                "🌎 Translator" -> onOpenTool(1)
                                "💻 AI Coder" -> onOpenTool(2)
                                "📚 Summarize" -> onOpenTool(3)
                                else -> onPromptSelected(dashboardCards[i + 1].prompt)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Suggestion Chips
        Text(
            text = "QUICK SUGGESTIONS",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (chip in suggestionChips) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .clickable {
                            performCyberHaptic(context)
                            onPromptSelected("$chip, Boss.")
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = chip,
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberCyan.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DashboardCard(
    item: DashboardCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        item.accentColor.copy(alpha = 0.5f),
                        CyberSurfaceVariant,
                        item.accentColor.copy(alpha = 0.15f)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(item.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                color = CyberTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.subtitle,
                color = CyberTextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
