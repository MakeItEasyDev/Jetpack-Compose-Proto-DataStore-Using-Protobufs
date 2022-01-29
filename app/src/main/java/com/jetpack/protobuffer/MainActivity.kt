package com.jetpack.protobuffer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.jetpack.protobuffer.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val DATA_STORE_FILE_NAME = "user_store.pb"
    val Context.userDataStore: DataStore<UserStore> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserStoreSerializer
    )
    private var userRepo: ProtoUserRepo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            userRepo = ProtoUserRepoImpl(userDataStore)
            ProtoBufferTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Proto Buffer",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        ProtoBuffer()
                    }
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ProtoBuffer() {
        val stateFlag = remember { mutableStateOf(false) }
        lifecycleScope.launch {
            userRepo?.getUserLoggedState()?.collect { state ->
                withContext(Dispatchers.Main) {
                    stateFlag.value = state
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (stateFlag.value) loginColor else logoutColor
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "User Logged in State ${stateFlag.value}",
                fontSize = 25.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    lifecycleScope.launch {
                        userRepo?.saveUserLoggedInState(true)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(green)
            ) {
                Text(
                    text = "Log-In",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(5.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    lifecycleScope.launch {
                        userRepo?.saveUserLoggedInState(false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(red)
            ) {
                Text(
                    text = "Log-Out",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}














