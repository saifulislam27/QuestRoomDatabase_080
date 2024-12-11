package com.example.project9.repository

import com.example.project9.data.entity.Mahasiswa

interface RepositoryMhs {

    suspend fun insertMhs(mahasiswa: Mahasiswa)
}