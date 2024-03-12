package com.example.digimess

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class SupaDB {

    companion object {
        fun getClient(): SupabaseClient {
            return createSupabaseClient(
                supabaseUrl = "https://rdgomvjmsabrydklvvee.supabase.co",
                supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJkZ29tdmptc2Ficnlka2x2dmVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDQ5NjYxMzksImV4cCI6MjAyMDU0MjEzOX0.pDGt8-NqPHpgZChkFBR0nd_69Jyk_cjiISy_m-OynlM",
            ){
                install(Postgrest)
            }
        }
    }
}