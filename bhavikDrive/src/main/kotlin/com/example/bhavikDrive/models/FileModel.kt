package com.example.bhavikDrive.models

import lombok.EqualsAndHashCode
import lombok.Getter
import lombok.Setter
import java.io.Serializable

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
class FileModel: Serializable {
    var id: String? = null
    var name: String? = null
    var link: String? = null
    var size: String? = null
    var thumbnailLink: String? = null
    var isShared = false
}