package com.example.beering.feature.auth.join.domain

import com.example.beering.data.ApiResult
import com.example.beering.data.auth.dto.CheckIdResult
import com.example.beering.data.auth.dto.CheckNameResult
import com.example.beering.data.auth.dto.JoinResponse
import com.example.beering.data.auth.dto.LoginResponse

interface UserRepository {
    suspend fun checkId(id : String) : ApiResult<CheckIdResult>
    suspend fun checkNickName(name : String) : ApiResult<CheckNameResult>
    suspend fun requestJoin(username: String, password: String, nickname: String, checkBoxList : ArrayList<Boolean>) : ApiResult<JoinResponse>
    suspend fun login(id : String, pw : String) : ApiResult<LoginResponse>
}