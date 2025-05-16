/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.ai.samples.genai_image_description

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.android.ai.samples.geminimultimodal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenAIImageDescriptionScreen(viewModel: GenAIImageDescriptionViewModel = hiltViewModel()) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imageDescriptionResult = viewModel.resultGenerated.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let {
            imageUri = it
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(text = stringResource(id = R.string.genai_image_description_title_bar))
                })
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Displayed image
            Card(
                modifier = Modifier
                    .size(width = 450.dp, height = 450.dp)
                    .padding(20.dp)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Select image button
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.genai_image_description_add_image),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Generate image description button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        showBottomSheet = true
                        viewModel.getImageDescription(imageUri, context)
                    }, modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.genai_image_description_run_inference),
                        fontSize = 20.sp
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearGeneratedText()
                }, sheetState = sheetState
            ) {
                Text(
                    text = imageDescriptionResult.value,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                )
            }
        }
    }
}