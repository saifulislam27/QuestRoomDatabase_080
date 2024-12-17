package com.example.project9.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project9.data.entity.Mahasiswa
import com.example.project9.repository.RepositoryMhs
import com.example.project9.ui.navigation.AlamatNavigasi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdateMhsViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoryMhs: RepositoryMhs
) : ViewModel() {

    var updateUIState by mutableStateOf(MhsUIState())
        private set

    private val _nim: String = checkNotNull(savedStateHandle[AlamatNavigasi.DestinasiEdit.NIM])

    init {
        viewModelScope.launch {
            updateUIState = repositoryMhs.getMhs(_nim)
                .filterNotNull()
                .first()
                .toUIStateMhs()
        }
    }

    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        updateUIState = updateUIState.copy(
            mahasiswaEvent = mahasiswaEvent,
        )
    }

    fun validateFields(): Boolean {
        val event = updateUIState.mahasiswaEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "NIM Tidak Boleh Kosong",
            nama = if (event.nama.isNotEmpty()) null else "Nama Tidak Boleh Kosong",
            jenisKelamin = if (event.jenisKelamin.isNotEmpty()) null else "Jenis Kelamin Tidak Boleh Kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "Alamat Tidak Boleh Kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "Kelas Tidak Boleh Kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "Angkatan Tidak Boleh Kosong",
        )

        updateUIState = updateUIState.copy(isEntryValid = errorState)
        return errorState.isValid()
    }
    fun updateData() {
        val currentEvent = updateUIState.mahasiswaEvent

        if (validateFields()) {
            viewModelScope.launch {
                try {
                    repositoryMhs.updateMhs(currentEvent.toMahasiswaEntity())
                    updateUIState = updateUIState.copy(
                        snackBarMessage = "Data Berhasil Diupdate",
                        mahasiswaEvent = MahasiswaEvent(),
                        isEntryValid = FormErrorState(),
                    )
                    println("snackBarMessage Diatur: ${updateUIState.snackBarMessage}")
                } catch (e: Exception) {
                    updateUIState = updateUIState.copy(
                        snackBarMessage = "Data Gagal Diupdate"
                    )
                }
            }
        } else  {
            updateUIState = updateUIState.copy(
                snackBarMessage = "Data Gagal Diupdate"
            )
        }
    }

    fun resetSnackBarMessage() {
        updateUIState = updateUIState.copy(snackBarMessage = null)
    }
}

fun Mahasiswa.toUIStateMhs(): MhsUIState = MhsUIState(
    mahasiswaEvent = this.toDetailUiEvent(),
)
