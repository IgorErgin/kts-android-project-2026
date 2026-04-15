package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.feature.detail.domain.model.GithubContent
import com.github.igorergin.ktsandroid.feature.detail.domain.model.PullRequest
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.RepositoryId
import com.mikepenz.markdown.m3.Markdown
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.back
import ktsandroidproject.composeapp.generated.resources.commit_message
import ktsandroidproject.composeapp.generated.resources.create_button
import ktsandroidproject.composeapp.generated.resources.create_issue
import ktsandroidproject.composeapp.generated.resources.decline
import ktsandroidproject.composeapp.generated.resources.description
import ktsandroidproject.composeapp.generated.resources.details_language
import ktsandroidproject.composeapp.generated.resources.error_network
import ktsandroidproject.composeapp.generated.resources.error_prefix
import ktsandroidproject.composeapp.generated.resources.error_server
import ktsandroidproject.composeapp.generated.resources.error_unauthorized
import ktsandroidproject.composeapp.generated.resources.error_unknown
import ktsandroidproject.composeapp.generated.resources.file_decode_error
import ktsandroidproject.composeapp.generated.resources.file_path
import ktsandroidproject.composeapp.generated.resources.forks_count
import ktsandroidproject.composeapp.generated.resources.issue_title_label
import ktsandroidproject.composeapp.generated.resources.no_description
import ktsandroidproject.composeapp.generated.resources.parent_dir
import ktsandroidproject.composeapp.generated.resources.readme_not_found
import ktsandroidproject.composeapp.generated.resources.readme_title
import ktsandroidproject.composeapp.generated.resources.retry
import ktsandroidproject.composeapp.generated.resources.select_file
import ktsandroidproject.composeapp.generated.resources.selected_file_prefix
import ktsandroidproject.composeapp.generated.resources.share
import ktsandroidproject.composeapp.generated.resources.stars_count
import ktsandroidproject.composeapp.generated.resources.tab_favorites
import ktsandroidproject.composeapp.generated.resources.tab_files
import ktsandroidproject.composeapp.generated.resources.tab_info
import ktsandroidproject.composeapp.generated.resources.tab_pulls
import ktsandroidproject.composeapp.generated.resources.upload_file
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    repoNameTitle: String,
    onBack: () -> Unit,
    vm: DetailViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.sideEffect.collectLatest { effect ->
            when (effect) {
                is DetailSideEffect.ShowError -> {
                    val message = when (val err = effect.error) {
                        AppError.Network -> getString(Res.string.error_network)
                        AppError.Server -> getString(Res.string.error_server)
                        is AppError.Unknown -> err.message ?: getString(Res.string.error_unknown)
                        AppError.Unauthorized -> getString(Res.string.error_unauthorized)
                    }
                    snackbarHostState.showSnackbar(message)
                }
                is DetailSideEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(getString(effect.message))
                }
            }
        }
    }

    DetailContent(
        repoNameTitle = repoNameTitle,
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRetry = vm::loadAll,
        onFavoriteClick = vm::toggleFavorite,
        onShare = vm::onShareClick,
        onAddIssueClick = { vm.setIssueDialogVisible(true) },
        onUploadClick = { vm.setUploadDialogVisible(true) },
        onFolderClick = vm::loadContents,
        onFileClick = vm::onFileClick,
        onCloseFile = vm::closeFileViewer,
        onPullRequestClick = vm::onPullRequestClick,
        onClosePullRequest = vm::closePullRequestDetail,
        onTabSelected = vm::onTabSelected
    )

    if (state.isIssueDialogVisible) {
        CreateIssueDialog(
            isSending = state.isIssueSending,
            error = state.issueError,
            onDismiss = { vm.setIssueDialogVisible(false) },
            onConfirm = { title, body -> vm.createIssue(title, body) }
        )
    }

    if (state.isUploadDialogVisible) {
        UploadFileDialog(
            isUploading = state.isUploading,
            error = state.uploadError,
            currentPath = state.currentPath,
            onDismiss = { vm.setUploadDialogVisible(false) },
            onConfirm = { path, message, content -> vm.uploadFile(path, message, content) }
        )
    }
}

@Composable
private fun AppError.toMessage(): String {
    return when (this) {
        AppError.Network -> stringResource(Res.string.error_network)
        AppError.Server -> stringResource(Res.string.error_server)
        is AppError.Unknown -> message ?: stringResource(Res.string.error_unknown)
        AppError.Unauthorized -> stringResource(Res.string.error_unauthorized)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    repoNameTitle: String,
    state: DetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShare: () -> Unit,
    onAddIssueClick: () -> Unit,
    onUploadClick: () -> Unit,
    onFolderClick: (String) -> Unit,
    onFileClick: (GithubContent.File) -> Unit,
    onCloseFile: () -> Unit,
    onPullRequestClick: (PullRequest) -> Unit,
    onClosePullRequest: () -> Unit,
    onTabSelected: (Int) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(repoNameTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(Res.string.tab_favorites),
                            tint = if (state.isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(Res.string.share))
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.selectedTabIndex == 0) {
                IconButton(
                    onClick = onAddIssueClick,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = stringResource(Res.string.create_issue), tint = Color.White)
                }
            } else if (state.selectedTabIndex == 1) {
                IconButton(
                    onClick = onUploadClick,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = stringResource(Res.string.upload_file), tint = Color.White)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                ErrorView(error = state.error, onRetry = onRetry, modifier = Modifier.align(Alignment.Center))
            } else {
                Column {
                    TabRow(selectedTabIndex = state.selectedTabIndex) {
                        Tab(
                            selected = state.selectedTabIndex == 0,
                            onClick = { onTabSelected(0) },
                            text = { Text(stringResource(Res.string.tab_info)) }
                        )
                        Tab(
                            selected = state.selectedTabIndex == 1,
                            onClick = { onTabSelected(1) },
                            text = { Text(stringResource(Res.string.tab_files)) }
                        )
                        Tab(
                            selected = state.selectedTabIndex == 2,
                            onClick = { onTabSelected(2) },
                            text = { Text(stringResource(Res.string.tab_pulls)) }
                        )
                    }

                    when (state.selectedTabIndex) {
                        0 -> {
                            state.repository?.let { repo ->
                                RepositoryInfo(repo, state.readme, state.isReadmeLoading)
                            }
                        }

                        1 -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (state.selectedFile != null) {
                                    FileViewer(
                                        file = state.selectedFile,
                                        onClose = onCloseFile
                                    )
                                } else if (state.isContentsLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                } else if (state.contentsError != null) {
                                    ErrorView(
                                        error = state.contentsError,
                                        onRetry = { onFolderClick("") },
                                        Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    FilesList(
                                        contents = state.contents,
                                        currentPath = state.currentPath,
                                        onFolderClick = onFolderClick,
                                        onFileClick = onFileClick
                                    )
                                }
                            }
                        }

                        2 -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (state.selectedPullRequest != null) {
                                    PullRequestDetail(
                                        pullRequest = state.selectedPullRequest,
                                        onClose = onClosePullRequest
                                    )
                                } else {
                                    PullRequestList(
                                        pulls = state.pullRequests,
                                        isLoading = state.isPullsLoading,
                                        error = state.pullsError,
                                        onRetry = onRetry,
                                        onPullRequestClick = onPullRequestClick
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorView(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(Res.string.error_prefix) + error.toMessage(), color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.retry))
        }
    }
}

@Composable
fun FilesList(
    contents: List<GithubContent>,
    currentPath: String,
    onFolderClick: (String) -> Unit,
    onFileClick: (GithubContent.File) -> Unit
) {
    LazyColumn {
            if (currentPath.isNotEmpty()) {
                item {
                    val parentPath = if (currentPath.contains("/")) currentPath.substringBeforeLast("/") else ""
                    ListItem(
                    headlineContent = { Text(stringResource(Res.string.parent_dir)) },
                    leadingContent = {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable { onFolderClick(parentPath) }
                )
                HorizontalDivider()
            }
        }

        items(contents) { content ->
            when (content) {
                is GithubContent.Directory -> {
                    ListItem(
                        headlineContent = { Text(content.name) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { onFolderClick(content.path) }
                    )
                }

                is GithubContent.File -> {
                    ListItem(
                        headlineContent = { Text(content.name) },
                        leadingContent = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.clickable { onFileClick(content) }
                    )
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun FileViewer(file: GithubContent.File, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Закрыть")
            }
            Text(file.name, style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            val decodedContent = remember(file.content) {
                try {
                    file.content?.decodeBase64String() ?: ""
                } catch (e: Exception) {
                    ""
                }
            }
            if (decodedContent.isEmpty() && file.content != null) {
                Text(stringResource(Res.string.file_decode_error), color = MaterialTheme.colorScheme.error)
            } else {
                SelectionContainer {
                    Text(decodedContent, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun PullRequestList(
    pulls: List<PullRequest>,
    isLoading: Boolean,
    error: AppError?,
    onRetry: () -> Unit,
    onPullRequestClick: (PullRequest) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        ErrorView(error, onRetry)
    } else {
        LazyColumn {
            items(pulls) { pr ->
                ListItem(
                    headlineContent = { Text(pr.title) },
                    supportingContent = {
                        Text("#${pr.number} by ${pr.userLogin} \u2022 ${pr.state}")
                    },
                    leadingContent = {
                        AsyncImage(
                            model = pr.userAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    },
                    modifier = Modifier.clickable { onPullRequestClick(pr) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PullRequestDetail(pullRequest: PullRequest, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text("PR #${pullRequest.number}", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(pullRequest.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = pullRequest.userAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(pullRequest.userLogin, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text("\u2022 ${pullRequest.state}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Markdown(pullRequest.body ?: stringResource(Res.string.no_description))
        }
    }
}

@Composable
fun RepositoryInfo(repo: RepositoryDetail, readme: String?, isReadmeLoading: Boolean) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(repo.fullName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(repo.description.ifBlank { stringResource(Res.string.no_description) }, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatView(label = stringResource(Res.string.stars_count), value = repo.starsCount.toString(), icon = Icons.Default.Star)
            StatView(label = stringResource(Res.string.forks_count), value = repo.forksCount.toString(), icon = Icons.Default.AccountTree)
            StatView(label = stringResource(Res.string.details_language), value = repo.language ?: "N/A", icon = Icons.Default.Code)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(stringResource(Res.string.readme_title), style = MaterialTheme.typography.titleLarge)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (isReadmeLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (readme != null) {
            Markdown(readme)
        } else {
            Text(stringResource(Res.string.readme_not_found), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun UploadFileDialog(
    isUploading: Boolean,
    error: AppError?,
    currentPath: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var path by remember { mutableStateOf(if (currentPath.isEmpty()) "" else "$currentPath/") }
    var message by remember { mutableStateOf("") }
    var contentBase64 by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val picker = rememberFilePickerLauncher { file ->
        scope.launch {
            file?.readBytes()?.let { bytes ->
                contentBase64 = bytes.encodeBase64()
                fileName = file.name
                if (path.endsWith("/")) {
                    path += file.name
                } else if (path.isEmpty()) {
                    path = file.name
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.upload_file)) },
        text = {
            Column {
                OutlinedTextField(
                    value = path,
                    onValueChange = { path = it },
                    label = { Text(stringResource(Res.string.file_path)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(stringResource(Res.string.commit_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { picker.launch() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (fileName.isEmpty()) stringResource(Res.string.select_file) else stringResource(Res.string.selected_file_prefix, fileName))
                }
                if (error != null) {
                    Text(error.toMessage(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(path, message, contentBase64) },
                enabled = !isUploading && path.isNotEmpty() && message.isNotEmpty() && contentBase64.isNotEmpty()
            ) {
                Text(stringResource(Res.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.decline))
            }
        }
    )
}

@Composable
fun CreateIssueDialog(
    isSending: Boolean,
    error: AppError?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.create_issue)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(Res.string.issue_title_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text(stringResource(Res.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (error != null) {
                    Text(error.toMessage(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, body) },
                enabled = !isSending && title.isNotEmpty()
            ) {
                Text(stringResource(Res.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.decline))
            }
        }
    )
}

@Composable
fun StatView(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun DetailPreview() {
    MaterialTheme {
        DetailContent(
            repoNameTitle = "Example Repo",
            state = DetailUiState(
                repository = RepositoryDetail(
                    id = RepositoryId(1L),
                    name = "Example",
                    fullName = "owner/Example",
                    description = "Description",
                    starsCount = 100,
                    forksCount = 10,
                    openIssuesCount = 5,
                    ownerLogin = "owner",
                    ownerAvatarUrl = "",
                    htmlUrl = "https://github.com/owner/Example",
                    language = "Kotlin"
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onRetry = {},
            onShare = {},
            onAddIssueClick = {},
            onUploadClick = {},
            onFolderClick = {},
            onFileClick = {},
            onCloseFile = {},
            onPullRequestClick = {},
            onClosePullRequest = {},
            onTabSelected = {},
            onFavoriteClick = {}
        )
    }
}
