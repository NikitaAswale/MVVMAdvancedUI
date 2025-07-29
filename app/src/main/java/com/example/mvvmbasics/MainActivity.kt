package com.example.mvvmbasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.data.remote.RetrofitClient
import com.example.mvvmbasics.data.repository.UserRepository
import com.example.mvvmbasics.ui.components.*
import com.example.mvvmbasics.ui.screens.ModernUserDetailScreen
import com.example.mvvmbasics.ui.theme.*
import com.example.mvvmbasics.viewmodel.UiState
import com.example.mvvmbasics.viewmodel.UserViewModel
import com.example.mvvmbasics.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository(RetrofitClient.apiService)
    private val userViewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVVMBasicsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModernUserManagementApp(userViewModel = userViewModel)
                }
            }
        }
    }
}

@Composable
fun ModernUserManagementApp(
    userViewModel: UserViewModel,
    navController: NavHostController = rememberNavController()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Animated background
        ModernAnimatedBackground(
            modifier = Modifier.fillMaxSize()
        )
        
        // Main content
        NavHost(
            navController = navController,
            startDestination = "user_list"
        ) {
            composable("user_list") {
                ModernUserListScreen(
                    userViewModel = userViewModel,
                    onUserClick = { user ->
                        navController.navigate("user_detail/${user.id}")
                    }
                )
            }
            
            composable("user_detail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                val uiState by userViewModel.uiState.collectAsState()
                
                when (val currentState = uiState) {
                    is UiState.Success -> {
                        val user = currentState.data.find { it.id == userId }
                        if (user != null) {
                            ModernUserDetailScreen(
                                user = user,
                                onBackClick = { navController.popBackStack() }
                            )
                        } else {
                            ModernErrorState(
                                message = "User not found",
                                onRetry = { navController.popBackStack() }
                            )
                        }
                    }
                    else -> {
                        ModernLoadingState()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernUserListScreen(
    userViewModel: UserViewModel,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by userViewModel.uiState.collectAsState()
    val searchQuery by userViewModel.searchQuery.collectAsState()
    val filteredUsers by userViewModel.filteredUsers.collectAsState()
    val listState = rememberLazyListState()
    
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Handle refresh
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            ModernTopAppBar(userViewModel = userViewModel, uiState = uiState)
        },
        floatingActionButton = {
            // Modern Floating Action Button
            ModernFloatingActionButton(
                onClick = { userViewModel.refreshUsers() },
                icon = Icons.Default.Refresh,
                contentDescription = "Refresh users",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modern Search Bar
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { userViewModel.searchUsers(it) },
                    onClear = { userViewModel.clearSearch() }
                )
                
                // Content
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = {
                        isRefreshing = true
                        userViewModel.refreshUsers()
                    }
                ) {
                    when (uiState) {
                        is UiState.Loading -> {
                            ModernLoadingState()
                        }
                        
                        is UiState.Success -> {
                            if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                                ModernEmptySearchState()
                            } else {
                                ModernUserList(
                                    users = filteredUsers,
                                    onUserClick = onUserClick,
                                    listState = listState
                                )
                            }
                        }
                        
                        is UiState.Error -> {
                            ModernErrorState(
                                message = (uiState as UiState.Error).message,
                                onRetry = { userViewModel.refreshUsers() }
                            )
                        }
                        
                        is UiState.Empty -> {
                            ModernEmptyState()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    userViewModel: UserViewModel,
    uiState: UiState<List<User>>
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Team Directory",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (val currentState = uiState) {
                        is UiState.Success -> "${currentState.data.size} amazing people"
                        is UiState.Loading -> "Loading..."
                        is UiState.Error -> "Error occurred"
                        is UiState.Empty -> "No users"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            GlassmorphismCard(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { userViewModel.refreshUsers() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
    }
}
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ModernUserList(
    users: List<User>,
    onUserClick: (User) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = users,
            key = { user -> user.id }
        ) { user ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(600)
                ) + slideInVertically(
                    animationSpec = tween(600)
                ) + scaleIn(
                    animationSpec = tween(600)
                )
            ) {
                ModernUserCard(
                    user = user,
                    onClick = onUserClick
                )
            }
        }
    }
}

@Composable
private fun ModernEmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
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
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = "No results",
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
                    text = "Try searching with different keywords or browse our amazing team members.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}