/*
 * This file is part of Cosmic IDE.
 * Cosmic IDE is a free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Cosmic IDE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Cosmic IDE. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cosmicide.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.cosmicide.databinding.CompileLogItemBinding
import org.cosmicide.fragment.CompileInfoFragment.LogItem

class CompileLogAdapter(val logs: ArrayList<LogItem>) : RecyclerView.Adapter<CompileLogAdapter.LogsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder =
        LogsViewHolder(
            CompileLogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        val log = logs[position]
        holder.binding.log.text = "[{$log.kind}] $log.message"
    }

    override fun getItemCount() = logs.size

    inner class LogsViewHolder(val binding: CompileLogItemBinding) : RecyclerView.ViewHolder(binding.root)
}
