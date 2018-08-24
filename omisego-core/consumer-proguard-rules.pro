# OMG
-keep class co.omisego.omisego.model.** { *; }
-keep class co.omisego.omisego.websocket.enum.** { *; }

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
-printmapping out.map
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.codehaus.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Kotlinx Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
