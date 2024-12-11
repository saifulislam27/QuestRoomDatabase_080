package com.example.project9.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.project9.data.entity.Mahasiswa

@Dao
interface MahasiswaDao {

    @Insert
    suspend fun insertMahasiswa(mahasiswa: Mahasiswa)
}