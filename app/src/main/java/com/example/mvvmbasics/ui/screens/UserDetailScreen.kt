package com.example.mvvmbasics.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.ui.components.InfoChip
import com.example.mvvmbasics.ui.components.UserAvatar
import com.example.mvvmbasics.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    user: User,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "User Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GradientStart,
                                GradientEnd
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1000)) + 
                                scaleIn(animationSpec = tween(1000))
                    ) {
                        UserAvatar(
                            name = user.name,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1000, delayMillis = 200)) +
                                slideInVertically(animationSpec = tween(1000, delayMillis = 200))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = "@${user.username}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // Content
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 400)) +
                        slideInVertically(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Contact Information
                    SectionCard(
                        title = "Contact Information",
                        icon = Icons.Default.ContactMail
                    ) {
                        ContactInfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = user.email
                        )
                        
                        if (user.phone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactInfoItem(
                                icon = Icons.Default.Phone,
                                label = "Phone",
                                value = user.phone
                            )
                        }
                        
                        if (user.website.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactInfoItem(
                                icon = Icons.Default.Language,
                                label = "Website",
                                value = user.website
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Address Information
                    if (user.address.street.isNotEmpty() || user.address.city.isNotEmpty()) {
                        SectionCard(
                            title = "Address",
                            icon = Icons.Default.LocationOn
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
                                ContactInfoItem(
                                    icon = Icons.Default.Home,
                                    label = "Address",
                                    value = fullAddress
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    
                    // Company Information
                    if (user.company.name.isNotEmpty()) {
                        SectionCard(
                            title = "Company",
                            icon = Icons.Default.Business
                        ) {
                            ContactInfoItem(
                                icon = Icons.Default.Business,
                                label = "Company",
                                value = user.company.name
                            )
                            
                            if (user.company.catchPhrase.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ContactInfoItem(
                                    icon = Icons.Default.Campaign,
                                    label = "Motto",
                                    value = user.company.catchPhrase
                                )
                            }
                            
                            if (user.company.bs.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ContactInfoItem(
                                    icon = Icons.Default.TrendingUp,
                                    label = "Business",
                                    value = user.company.bs
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { /* Handle email action */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Email")
                        }
                        
                        if (user.phone.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { /* Handle call action */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Call")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
private fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 