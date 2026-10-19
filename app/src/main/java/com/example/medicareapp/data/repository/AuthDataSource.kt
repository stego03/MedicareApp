
package com.example.medicareapp.data.repository

interface AuthDataSource {

    fun utenteCorrente(): String?

    fun logout()
}
