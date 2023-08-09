package com.dicoding.picodiploma.storyapp.repository

class MockAuthRepository : AuthRepositoryInterface {
    override fun getToken(): String {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUNZZVUyb2VsWFhCcDlLSVQiLCJpYXQiOjE2ODc5ODE2MTh9.uCAX6bQqbZVk9eYdeVtlF4sZ-OEYrvDMehsA1ZlLejU"
    }
}