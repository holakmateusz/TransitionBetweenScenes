package com.mh.transitionbetweenscenes.fragments

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.mh.transitionbetweenscenes.R
import com.mh.transitionbetweenscenes.databinding.BaseHolderScenesLayoutBinding

abstract class BaseHolderScenesFragment<T : ViewBinding, K : ViewBinding>(
    @LayoutRes private val defaultLayoutRes: Int,
    @LayoutRes private val alternativeLayoutRes: Int
) : Fragment(R.layout.base_holder_scenes_layout) {

    private var rootBinding: BaseHolderScenesLayoutBinding? = null
    private var defaultScene: Scene? = null
    private var alternativeScene: Scene? = null
    private val defaultTransition by lazy { ChangeBounds() }

    protected abstract var bindingForDefaultScene: T?
    protected abstract var bindingForAlternativeScene: K?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootBinding = BaseHolderScenesLayoutBinding.bind(view)
        initScenes()
        triggerSceneChanges(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingForDefaultScene = null
        bindingForAlternativeScene = null
        rootBinding = null
    }

    private fun initScenes() {
        defaultScene =
            Scene.getSceneForLayout(rootBinding?.rootContainer, defaultLayoutRes, activity)
        alternativeScene =
            Scene.getSceneForLayout(rootBinding?.rootContainer, alternativeLayoutRes, activity)
        initProperties()
    }

    protected fun triggerSceneChanges(alternative: Boolean) {
        when (alternative) {
            true -> {
                alternativeScene?.let {
                    prepareEnterActionForScene(it) { initAlternativeScene() }
                    TransitionManager.go(it, defaultTransition)
                }
            }
            false -> {
                defaultScene?.let {
                    prepareEnterActionForScene(it) { initDefaultScene() }
                    TransitionManager.go(it, defaultTransition)
                }
            }
        }
    }

    private fun prepareEnterActionForScene(
        scene: Scene,
        initScene: () -> Unit = {}
    ) {
        scene.setEnterAction {
            initScene()
        }
    }

    abstract fun initDefaultScene()

    abstract fun initAlternativeScene()

    abstract fun initProperties()
}