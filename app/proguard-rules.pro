# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# SQLCipher
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep app entities/models for reflection-based libs
-keep class com.moneymind.ai.data.local.entity.** { *; }
