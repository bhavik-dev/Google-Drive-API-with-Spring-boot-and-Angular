package com.example.bhavikDrive.models

import lombok.Builder
import lombok.Data

@Data
@Builder
class AuthorizationDetails {
    var type: String? = null
    var role: String? = null
    var emailAddress: String? = null
}