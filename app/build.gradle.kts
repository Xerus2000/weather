import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("com.android.application")
	id("kotlin-android")
	id("kotlin-android-extensions")
}

android {
	compileSdkVersion(26)
	defaultConfig {
		applicationId = "xerus2000.github.com.weather"
		minSdkVersion(21)
		targetSdkVersion(26)
		versionCode = 1
		versionName = "1.0"
		testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
	}
}

dependencies {
	kotlin("stdlib-jdk7")
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "0.30.0")
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", "0.30.0")
	
	fun androidSupport(name: String) = implementation("com.android.support", name, "26.1.0")
	androidSupport("appcompat-v7")
	androidSupport("recyclerview-v7")
	implementation("com.android.support.constraint:constraint-layout:1.1.3")
	
	implementation("android.arch.lifecycle:livedata-core:1.1.1")
	
	implementation("com.google.android.gms", "play-services-location", "16.0.0")
	
	implementation("com.squareup.retrofit2", "retrofit", "2.4.0")
	implementation("com.squareup.retrofit2", "converter-gson", "2.4.0")
	
	androidTestImplementation("com.android.support.test:runner:1.0.2")
	androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
	testImplementation("junit:junit:4.12")
}

kotlin.experimental.coroutines = Coroutines.ENABLE