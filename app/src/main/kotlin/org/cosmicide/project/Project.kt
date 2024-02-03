package org.cosmicide.project

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Project(
    val root: File,
    val language: Language
): ExternalProject(root, language), Parcelable {}
