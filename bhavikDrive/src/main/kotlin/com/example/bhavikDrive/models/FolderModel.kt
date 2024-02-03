package com.example.bhavikDrive.models

import lombok.EqualsAndHashCode
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
class FolderModel {
    var id: String? = null
    var name: String? = null
    var link: String? = null
}