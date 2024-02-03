/*
 * This file is part of Cosmic IDE.
 * Cosmic IDE is a free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Cosmic IDE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Cosmic IDE. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cosmicide.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cosmicide.R
import org.cosmicide.build.BuildReporter
import org.cosmicide.build.BuildReportKind
import org.cosmicide.common.BaseBindingFragment
import org.cosmicide.compile.Compiler
import org.cosmicide.databinding.FragmentCompileInfoBinding
import org.cosmicide.project.Project
import org.cosmicide.util.ProjectHandler

/**
 * A fragment for displaying information about the compilation process.
 */
class CompileInfoFragment : BaseBindingFragment<FragmentCompileInfoBinding>() {
    private val project: Project? = ProjectHandler.getProject()
    private val logs: List<LogItem> = emptyList()
    private var compiler: Compiler? = null
    private var adapter: CompileLogAdapter? = null

    override fun getViewBinding() = FragmentCompileInfoBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CompileLogAdapter(logs)
        compiler = Compiler(requireContext(), checkProject(project)) { report ->
            if (report.message.isEmpty()) {
                return@BuildReporter
            }
            binding.logList.post {
                addLogItem(kind = report.kind, message = report.message);
            }
        }

        binding.toolbar.apply {
            subtitle = project?.name
            setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                compiler?.compile()
                if (reporter.buildSuccess) {
                    withContext(Dispatchers.Main) {
                        navigateToProjectOutputFragment()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.logList.post {
                        addLogItem(kind = BuildReportKind.ERROR, message = e.message)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        compiler = null
    }

    private fun checkProject(project: Project?): Project {
        if (project == null) {
            throw IllegalStateException("No project set")
        }
        return project!!
    }

    private fun navigateToProjectOutputFragment() {
        parentFragmentManager.commit {
            add(R.id.fragment_container, ProjectOutputFragment())
            addToBackStack(null)
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }

    private fun addLogItem(kind: BuildReportKind, message: String) {
        logs.add(
            LogItem(
                kind = kind,
                message = message
            )
        )

        adapter.notifyItemInserted(logs.size())
        binding.logList.smoothScrollToPosition(logs.size() - 1)
    }

    data class LogItem(
        val kind: BuildReportKind,
        val message: String
    )
}