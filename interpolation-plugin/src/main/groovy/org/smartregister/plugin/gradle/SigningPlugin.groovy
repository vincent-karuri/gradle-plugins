package org.smartregister.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Vincent Karuri on 28/02/2020
 */
class SigningPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('configure-signing') {
            doLast {
                def isRunningOnTravis = System.getenv("CI") == "true"
                if (isRunningOnTravis) {
                    // configure keystore
                    project.android.signingConfigs.release.storeFile = file("../brisk_pesa.keystore")
                    project.android.signingConfigs.release.storePassword = System.getenv("keystore_password")
                    project.android.signingConfigs.release.keyAlias = System.getenv("keystore_alias")
                    project.android.signingConfigs.release.keyPassword = System.getenv("keystore_alias_password")
                    project.android.buildTypes.release.signingConfig = project.android.signingConfigs.release
                }
            }
        }
    }
}

