package org.cosmicide.mapper

import org.cosmicide.project.ExternalProject
import org.cosmicide.project.Project

fun ExternalProject.toProject(): Project {
    return Project(this.root, this.language)
}

fun Project.toExternalProject(): ExternalProject {
    return ExternalProject(this.root, this.language)
}
