package com.example.mvvmbasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
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
import com.example.mvvmbasics.ui.screens.UserDetailScreen
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
                    UserManagementApp(userViewModel = userViewModel)
                }
            }
        }
    }
}

@Composable
fun UserManagementApp(
    userViewModel: UserViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "user_list"
    ) {
        composable("user_list") {
            UserListScreen(
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
                        UserDetailScreen(
                            user = user,
                            onBackClick = { navController.popBackStack() }
                        )
                    } else {
                        // Handle user not found
                        ErrorState(
                            message = "User not found",
                            onRetry = { navController.popBackStack() }
                        )
                    }
                }
                else -> {
                    // Loading or error state
                    LoadingState()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
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
                                is UiState.Success -> "${currentState.data.size} users"
                                is UiState.Loading -> "Loading..."
                                is UiState.Error -> "Error occurred"
                                is UiState.Empty -> "No users"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { userViewModel.refreshUsers() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GradientStart,
                                GradientEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Discover Amazing People",
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Search Bar
            SearchBar(
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
                        LoadingState()
                    }
                    
                    is UiState.Success -> {
                        if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                            // No search results
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = "No results",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "No users found",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    
                                    Text(
                                        text = "Try searching with different keywords",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = filteredUsers,
                                    key = { user -> user.id }
                                ) { user ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(
                                            animationSpec = tween(600)
                                        ) + slideInVertically(
                                            animationSpec = tween(600)
                                        )
                                    ) {
                                        UserCard(
                                            user = user,
                                            onClick = onUserClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    is UiState.Error -> {
                        ErrorState(
                            message = (uiState as UiState.Error).message,
                            onRetry = { userViewModel.refreshUsers() }
                        )
                    }
                    
                    is UiState.Empty -> {
                        EmptyState()
                    }
                }
            }
        }
    }
}