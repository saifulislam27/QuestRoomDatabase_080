package com.example.project9.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project9.data.entity.Mahasiswa
import com.example.project9.repository.RepositoryMhs
import com.example.project9.ui.navigation.AlamatNavigasi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class DetailMhsViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoryMhs: RepositoryMhs,

    ) : ViewModel() {
    private val _nim: String = checkNotNull(savedStateHandle[AlamatNavigasi.DestinasiDetail.NIM])

    val detailUiState: StateFlow<DetailMhsUiState> = repositoryMhs.getMhs(_nim)
        .filterNotNull()
        .map {
            DetailMhsUiState(
                detailUiEvent = it.toDetailUiEvent(),
                isLoading = false,
            )
        }
        .onStart {
            emit(DetailMhsUiState(isLoading = true))
            delay(600)
        }
        .catch {
            emit(
                DetailMhsUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = it.message ?: "Terjadi Kesalahan"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000),
            initialValue = DetailMhsUiState(
                isLoading = true
            ),
        )
}
data class DetailMhsUiState(
    val detailUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
){
    val isUiEventEmpty: Boolean
        get()= detailUiEvent == MahasiswaEvent()
    val isUiEventNotEmpty: Boolean
        get() = detailUiEvent != MahasiswaEvent()
}
fun Mahasiswa.toDetailUiEvent(): MahasiswaEvent {
    return MahasiswaEvent(
        nim = nim,
        nama = nama,
        jenisKelamin = jenisKelamin,
        alamat = alamat,
        kelas = kelas,
        angkatan = angkatan
    )
}