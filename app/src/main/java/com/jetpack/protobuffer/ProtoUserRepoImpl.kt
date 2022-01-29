package com.jetpack.protobuffer

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ProtoUserRepoImpl(
    private val protoDataStore: DataStore<UserStore>
): ProtoUserRepo {
    override suspend fun saveUserLoggedInState(state: Boolean) {
        protoDataStore.updateData { store ->
            store.toBuilder()
                .setIsLoggedIn(state)
                .build()
        }
    }

    override suspend fun getUserLoggedState(): Flow<Boolean> {
        return protoDataStore.data
            .catch { exp ->
                if (exp is IOException) {
                    emit(UserStore.getDefaultInstance())
                } else {
                    throw exp
                }
            }.map { protoBuilder ->
                protoBuilder.isLoggedIn
            }
    }
}