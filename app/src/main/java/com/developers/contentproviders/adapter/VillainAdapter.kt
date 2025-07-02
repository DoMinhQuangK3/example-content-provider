package com.developers.contentproviders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.databinding.ItemRowBinding

/**
 * RecyclerView Adapter for displaying Villains using modern ListAdapter with DiffUtil
 */
class VillainAdapter(
    private val onItemClick: (Villains) -> Unit = {}
) : ListAdapter<Villains, VillainAdapter.VillainViewHolder>(VillainsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillainViewHolder {
        val binding = ItemRowBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return VillainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VillainViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class VillainViewHolder(
        private val binding: ItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(villain: Villains) {
            binding.apply {
                nameTextView.text = villain.villainName
                idTextView.text = villain.id.toString()
                seriesTextView.text = villain.villainSeries
            }
        }
    }

    class VillainsDiffCallback : DiffUtil.ItemCallback<Villains>() {
        override fun areItemsTheSame(oldItem: Villains, newItem: Villains): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Villains, newItem: Villains): Boolean {
            return oldItem == newItem
        }
    }
}