// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
	repositories {
		google()
	}
	dependencies {
		classpath("com.android.tools.build:gradle:3.2.+")
	}
}

plugins {
	kotlin("jvm") version "1.3.10"
}

allprojects {
	repositories {
		google()
		jcenter()
	}
}
