package com.example.mvvmbasics.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.ui.theme.*

// Modern Glassmorphism Card
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isSystemInDarkTheme()) GlassGradientDark else GlassGradientLight
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        if (isSystemInDarkTheme()) GlassBorderDark else GlassBorderLight,
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .blur(radius = 0.5.dp)
    ) {
        content()
    }
}

// Modern Neumorphism Card
@Composable
fun NeumorphismCard(
    modifier: Modifier = Modifier,
    isPressed: Boolean = false,
    content: @Composable () -> Unit
) {
    val elevation = if (isPressed) 2.dp else 8.dp
    val shadowColor = if (isSystemInDarkTheme()) NeumorphismShadowDark else NeumorphismShadowLight
    
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(20.dp),
                spotColor = shadowColor
            )
            .background(
                color = if (isSystemInDarkTheme()) NeumorphismDark else NeumorphismLight,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        content()
    }
}

// Modern Floating Action Button with Glassmorphism
@Composable
fun ModernFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = modifier
            .scale(animatedScale * pulseScale)
            .shadow(
                elevation = animatedElevation,
                shape = CircleShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
    ) {
        GlassmorphismCard(
            modifier = Modifier
                .size(64.dp)
                .clickable { onClick() }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// Enhanced Modern Search Bar with better animations
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )
    
    val animatedIconScale by animateFloatAsState(
        targetValue = if (isFocused) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    GlassmorphismCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(animatedBorderColor, Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated search icon with pulse effect
            val infiniteTransition = rememberInfiniteTransition(label = "search_pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_alpha"
            )
            
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
                modifier = Modifier
                    .size(24.dp)
                    .scale(animatedIconScale)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { 
                    Text(
                        "Search amazing people...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

// Modern Animated Status Indicator
@Composable
fun ModernStatusIndicator(
    isOnline: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isOnline) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isOnline) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    Box(
        modifier = modifier
            .size(12.dp)
            .scale(pulseScale)
            .background(
                color = if (isOnline) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isOnline) AccentGreen.copy(alpha = pulseAlpha) else Color.Transparent,
                shape = CircleShape
            )
    )
}

// Enhanced Modern User Card with status indicator and better animations
@Composable
fun ModernUserCard(
    user: User,
    onClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else if (isHovered) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = when {
            isPressed -> 4.dp
            isHovered -> 16.dp
            else -> 12.dp
        },
        animationSpec = tween(durationMillis = 200)
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = if (isHovered) 2f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    
    GlassmorphismCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .scale(animatedScale)
            .rotate(animatedRotation)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .clickable { onClick(user) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modern Avatar with gradient border and status indicator
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = GradientSunset
                            ),
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                ) {
                    ModernUserAvatar(
                        name = user.name,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Status indicator positioned at bottom-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                    ) {
                        ModernStatusIndicator(
                            isOnline = (user.id % 3 == 0), // Simulate online status
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // Name with gradient text
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Username with accent color
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Email with subtle styling
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Modern chevron with animation
                val chevronRotation by animateFloatAsState(
                    targetValue = if (isHovered) 90f else 0f,
                    animationSpec = tween(durationMillis = 200)
                )
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation)
                )
            }
            
            // Company info with modern chip
            if (user.company.name.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                ModernInfoChip(
                    icon = Icons.Outlined.Business,
                    text = user.company.name,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            }
            
            // Contact info chips
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModernInfoChip(
                    icon = Icons.Outlined.Email,
                    text = "Email",
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
                
                if (user.phone.isNotEmpty()) {
                    ModernInfoChip(
                        icon = Icons.Outlined.Phone,
                        text = "Phone",
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

// Modern Avatar with advanced styling
@Composable
fun ModernUserAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=6366F1&color=fff&size=128&bold=true"
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = GradientSunset
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar for $name",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

// Modern Info Chip
@Composable
fun ModernInfoChip(
    icon: ImageVector,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Modern Animated Progress Indicator
@Composable
fun ModernProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "progress_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                            MaterialTheme.colorScheme.primary
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

// Enhanced Modern Loading State with animated gradient and progress
@Composable
fun ModernLoadingState(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val gradientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_rotation"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated gradient circle with pulse effect
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = GradientSunset,
                            center = androidx.compose.ui.geometry.Offset.Zero
                        ),
                        shape = CircleShape
                    )
                    .rotate(gradientRotation)
                    .padding(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Animated loading text
            val textAlpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "text_alpha"
            )
            
            Text(
                text = "Loading amazing people...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Please wait while we fetch the latest data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress indicator
            ModernProgressIndicator(
                progress = 0.7f, // Simulate progress
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

// Modern Error State with glassmorphism
@Composable
fun ModernErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphismCard(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                // Animated error icon
                val iconScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(iconScale)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Modern retry button
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .height(48.dp)
                        .background(
                            brush = Brush.horizontalGradient(GradientSunset),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Try Again",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Modern Empty State
@Composable
fun ModernEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphismCard(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PeopleOutline,
                    contentDescription = "No users",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "No users found",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Try adjusting your search or check back later for new additions to our amazing team.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 

// Modern Animated Background with Floating Particles
@Composable
fun ModernAnimatedBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    // Create multiple floating particles
    repeat(8) { index ->
        val particleOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_$index"
        )
        
        val particleScale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + index * 300,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale_$index"
        )
        
        val particleAlpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2500 + index * 400,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha_$index"
        )
        
        Box(
            modifier = modifier
                .offset(
                    x = (50 + index * 40).dp,
                    y = (100 + index * 60 + particleOffset).dp
                )
                .size((20 + index * 5).dp)
                .scale(particleScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = particleAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(radius = 2.dp)
        )
    }
}

// Modern Card with Hover Effect
@Composable
fun ModernHoverCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isHovered) 16.dp else 8.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    GlassmorphismCard(
        modifier = modifier
            .scale(animatedScale)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            .clickable { onClick() }
    ) {
        content()
    }
} 