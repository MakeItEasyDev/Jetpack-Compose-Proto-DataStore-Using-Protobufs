package com.jetpack.protobuffer

import kotlinx.coroutines.flow.Flow

interface ProtoUserRepo {
    suspend fun saveUserLoggedInState(state: Boolean)
    suspend fun getUserLoggedState(): Flow<Boolean>
}