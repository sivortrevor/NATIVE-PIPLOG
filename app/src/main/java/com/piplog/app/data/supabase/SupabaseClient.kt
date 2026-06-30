package com.piplog.app.data.supabase

import com.piplog.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://mtmulkzigwjfrruuwwga.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im10bXVsa3ppZ3dqZnJydXV3d2dhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODIwMzA0NDQsImV4cCI6MjA5NzYwNjQ0NH0.oD1ZSco5Djn52wV3dALPjbZ-VRxbrOxHR_Bokzp3AhA"
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    val auth: Auth by lazy { client.auth }
    val postgrest: Postgrest by lazy { client.postgrest }
    val storage: Storage by lazy { client.storage }

    const val TRADES_TABLE = "trades"
    const val PROFILES_TABLE = "profiles"
    const val JOURNAL_TABLE = "journal_entries"
    const val SCREENSHOTS_BUCKET = "trade-screenshots"
}
