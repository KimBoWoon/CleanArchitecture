package com.lol.ui.vh

import com.domain.lol.dto.Passive
import com.lol.base.BaseVH
import com.lol.databinding.VhChampionPassiveBinding
import util.Log

class ChampionPassiveVH(
    override val binding: VhChampionPassiveBinding
) : BaseVH<VhChampionPassiveBinding, Passive>(binding) {
    override fun bind(item: Passive?) {
        runCatching {
            item ?: run {
                Log.e("ChampionPassiveVH item is null")
                return
            }
        }.onSuccess { passiveInfo ->
            binding.apply {
                passive = passiveInfo
            }
        }.onFailure { e ->
            Log.printStackTrace(e)
        }
    }
}