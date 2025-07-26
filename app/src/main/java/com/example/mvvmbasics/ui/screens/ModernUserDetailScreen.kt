package com.example.mvvmbasics.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.ui.components.*
import com.example.mvvmbasics.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernUserDetailScreen(
    user: User,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModernTopAppBar(
                title = "Profile",
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section with Parallax Effect
            ModernHeroSection(user = user, isVisible = isVisible)
            
            // Content Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 400)) +
                        slideInVertically(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Contact Information
                    ModernSectionCard(
                        title = "Contact Information",
                        icon = Icons.Outlined.ContactMail
                    ) {
                        ModernContactInfoItem(
                            icon = Icons.Outlined.Email,
                            label = "Email",
                            value = user.email
                        )
                        
                        if (user.phone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ModernContactInfoItem(
                                icon = Icons.Outlined.Phone,
                                label = "Phone",
                                value = user.phone
                            )
                        }
                        
                        if (user.website.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ModernContactInfoItem(
                                icon = Icons.Outlined.Language,
                                label = "Website",
                                value = user.website
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Address Information
                    if (user.address.street.isNotEmpty() || user.address.city.isNotEmpty()) {
                        ModernSectionCard(
                            title = "Location",
                            icon = Icons.Outlined.LocationOn
                        ) {
                            val fullAddress = buildString {
                                if (user.address.street.isNotEmpty()) {
                                    append(user.address.street)
                                    if (user.address.suite.isNotEmpty()) {
                                        append(", ${user.address.suite}")
                                    }
                                }
                                if (user.address.city.isNotEmpty()) {
                                    if (isNotEmpty()) append("\n")
                                    append(user.address.city)
                                    if (user.address.zipcode.isNotEmpty()) {
                                        append(" ${user.address.zipcode}")
                                    }
                                }
                            }
                            
                            if (fullAddress.isNotEmpty()) {
                                ModernContactInfoItem(
                                    icon = Icons.Outlined.Home,
                                    label = "Address",
                                    value = fullAddress
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Company Information
                    if (user.company.name.isNotEmpty()) {
                        ModernSectionCard(
                            title = "Company",
                            icon = Icons.Outlined.Business
                        ) {
                            ModernContactInfoItem(
                                icon = Icons.Outlined.Business,
                                label = "Company",
                                value = user.company.name
                            )
                            
                            if (user.company.catchPhrase.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ModernContactInfoItem(
                                    icon = Icons.Outlined.Campaign,
                                    label = "Motto",
                                    value = user.company.catchPhrase
                                )
                            }
                            
                            if (user.company.bs.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ModernContactInfoItem(
                                    icon = Icons.Outlined.TrendingUp,
                                    label = "Business",
                                    value = user.company.bs
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Modern Action Buttons
                    ModernActionButtons(user = user)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { 
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            GlassmorphismCard(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ModernHeroSection(
    user: User,
    isVisible: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = GradientSunset
                    )
                )
        )
        
        // Floating elements
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000)) + 
                    scaleIn(animationSpec = tween(1000))
        ) {
            // Floating circles for decoration
            repeat(3) { index ->
                val infiniteTransition = rememberInfiniteTransition(label = "floating_$index")
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 20f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000 + index * 500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "offset_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size((60 + index * 20).dp)
                        .offset(
                            x = (100 + index * 80).dp,
                            y = (50 + index * 40 + offset).dp
                        )
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .blur(radius = 2.dp)
                )
            }
        }
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 200)) +
                        scaleIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                // Large avatar with glow effect
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .background(
                            brush = Brush.radialGradient(GradientSunset),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    ModernUserAvatar(
                        name = user.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 400)) +
                        slideInVertically(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.radialGradient(GradientSunset),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            content()
        }
    }
}

@Composable
private fun ModernContactInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon with subtle background
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ModernActionButtons(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email Button
        ModernActionButton(
            icon = Icons.Outlined.Email,
            text = "Email",
            onClick = { /* Handle email action */ },
            modifier = Modifier.weight(1f),
            gradient = GradientOcean
        )
        
        // Phone Button (if available)
        if (user.phone.isNotEmpty()) {
            ModernActionButton(
                icon = Icons.Outlined.Phone,
                text = "Call",
                onClick = { /* Handle call action */ },
                modifier = Modifier.weight(1f),
                gradient = GradientNeon
            )
        }
    }
}

@Composable
private fun ModernActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .background(
                brush = Brush.horizontalGradient(gradient),
                shape = RoundedCornerShape(28.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
} 