package com.practice.ui.fragments.vm

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.data.practice.paging.source.PokemonSource
import com.domain.practice.dto.PokemonModel
import com.domain.practice.usecase.PokemonApiUseCase
import com.practice.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PokemonListVM @Inject constructor(
    private val pokemonApiUseCase: PokemonApiUseCase
) : BaseVM() {
    val pokemonPageFlow: Flow<PagingData<PokemonModel>> = Pager(
        PagingConfig(pageSize = 20, initialLoadSize = 20)
    ) {
        PokemonSource(pokemonApiUseCase) // TODO 유즈 케이스를 di 사용해야함
    }.flow.map {
        it.insertHeaderItem(item = PokemonModel.PokemonHeader("PokemonHeader"))
            .insertFooterItem(item = PokemonModel.PokemonFooter("PokemonFooter"))
    }.cachedIn(viewModelScope)
}