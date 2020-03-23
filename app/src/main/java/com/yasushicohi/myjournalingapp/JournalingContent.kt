package com.yasushicohi.myjournalingapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class JournalingContent(
    @PrimaryKey(autoGenerate = true)
    var journalingContentId: Int,
    var jornalingContentTitle: String,
    var journalingContentText: String,
    var createDate: String
)