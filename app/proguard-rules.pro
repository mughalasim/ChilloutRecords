# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# MAPPING
-printmapping proguard_mapping.txt

-verbose

# RETROFIT
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keep class retrofit.** { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# GLIDE CLASSES
-dontwarn javax.annotation.**
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# SEARCH WIDGET CLASSES
-keep class android.support.v7.widget.SearchView { *; }

# FACEBOOK CLASSES
-keepattributes *Annotation*
-keep class com.facebook.** { *; }

# FIREBASE MODELS
-keepattributes Signature
-keepattributes *Annotation*

# GOOGLE PLAY ADS
-dontwarn com.google.android.gms.**

# USELESS WARNINGS
-ignorewarnings


# YOYO ANIMATION CLASSES
-keep class com.daimajia.* { *; }
-keep interface com.daimajia.* { *; }
-keep public class com.daimajia.* { *; }
-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }
-keep class com.nineoldandroids.* { *; }
-keep interface com.nineoldandroids.* { *; }
-keep public class com.nineoldandroids.* { *; }