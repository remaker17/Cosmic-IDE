package org.cosmicide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import org.cosmicide.MainActivity

class IdeFragment<T : ViewBinding> : BaseFragment {

  @Suppress("PropertyName")
  protected var _binding: T? = null

  protected val binding: T
    get() = checkNotNull(_binding) { "Cannot access ViewHolder. Fragment may have been destroyed." }

  private var bind: ((View) -> T)? = null

  private var inflate: ((LayoutInflater, ViewGroup?, Boolean) -> T)? = null

  protected var activity: MainActivity? = null

  constructor(@LayoutRes layout: Int, bind: (View) -> T) : super(layout) {
    this.bind = bind
  }

  constructor(inflate: (LayoutInflater, ViewGroup?, Boolean) -> T) {
    this.inflate = inflate
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    activity = requireActivity() as MainActivity
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    this.bind?.let { bind ->
      return super.onCreateView(inflater, container, savedInstanceState)!!
        .also {
          _binding = bind(it)
        }
    }

    return inflate!!.invoke(inflater, container, false).let {
      _binding = it
      binding.root
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
    activity = null
  }
}