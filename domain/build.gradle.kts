plugins {
    id(Dependencies.BuildPlugins.library)
    kotlin(Dependencies.BuildPlugins.android)
    kotlin(Dependencies.BuildPlugins.kapt)
    id(Dependencies.BuildPlugins.hilt)
    id(Dependencies.BuildPlugins.parcelize)
}

android {
    compileSdk = Config.Application.compileSdk

    defaultConfig {
        minSdk = Config.Application.minSdk
        targetSdk = Config.Application.targetSdk

        testInstrumentationRunner = Config.Application.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile(Config.Application.defaultProguardFile), Config.Application.proguardFile)
        }
        debug {
            isMinifyEnabled = false
        }
        create(Config.BuildType.beta) {
            isMinifyEnabled = false
        }
    }
    flavorDimensions.addAll(listOf(Config.Dimensions.mode))
    productFlavors {
        create(Config.Flavors.lol) {
            dimension = Config.Dimensions.mode
        }
        create(Config.Flavors.practice) {
            dimension = Config.Dimensions.mode
        }
        create(Config.Flavors.gpsAlarm) {
            dimension = Config.Dimensions.mode
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = Config.Application.jvmVersion
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
            )
        )
    }
    sourceSets {
        getByName(Config.SourceSet.main) {
            getByName(Config.Flavors.lol) {
                manifest.srcFile("src/lol/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/lol/java"))
            }
            getByName(Config.Flavors.practice) {
                manifest.srcFile("src/practice/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/base/java", "src/practice/java"))
            }
            getByName(Config.Flavors.gpsAlarm) {
                manifest.srcFile("src/gpsAlarm/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/base/java", "src/gpsAlarm/java"))
            }
        }
        getByName(Config.SourceSet.debug) {
            getByName(Config.Flavors.lol) {
                manifest.srcFile("src/lol/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/lol/java"))
            }
            getByName(Config.Flavors.practice) {
                manifest.srcFile("src/practice/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/base/java", "src/practice/java"))
            }
            getByName(Config.Flavors.gpsAlarm) {
                manifest.srcFile("src/gpsAlarm/AndroidManifest.xml")
                java.setSrcDirs(listOf("src/base/java", "src/gpsAlarm/java"))
            }
        }
    }
}

dependencies {
    arrayOf(
        Dependencies.Jetpack.core,
        Dependencies.Jetpack.datastore,
        Dependencies.Jetpack.pagingCommon,
        Dependencies.Hilt.hiltAndroid
    ).forEach {
        implementation(it)
    }

    arrayOf(
        Dependencies.Hilt.hiltAndroidCompiler,
        Dependencies.Hilt.hiltCompiler
    ).forEach {
        kapt(it)
    }

    arrayOf(
        Dependencies.Test.junit
    ).forEach {
        testImplementation(it)
    }

    arrayOf(
        Dependencies.Test.junitExt
    ).forEach {
        androidTestImplementation(it)
    }
}