package com.mh.transitionbetweenscenes.fragments

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mh.transitionbetweenscenes.R
import com.mh.transitionbetweenscenes.databinding.BaseHolderScenesLayoutBinding

abstract class BaseHolderScenesFragment<T : ViewDataBinding, K : ViewDataBinding>(
    @LayoutRes private val defaultLayoutRes: Int,
    @LayoutRes private val alternativeLayoutRes: Int
) : Fragment() {

    private val defaultTransition by lazy { ChangeBounds() }
    private var rootBinding: BaseHolderScenesLayoutBinding? = null
    private var defaultScene: Scene? = null
    private var alternativeScene: Scene? = null
    protected var bindingForDefaultScene: T? = null
    protected var bindingForAlternativeScene: K? = null

    companion object {
        const val PARENT_CHILD_POSITION = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootBinding =
            DataBindingUtil.inflate(inflater, R.layout.base_holder_scenes_layout, container, false)
        return rootBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    prepareEnterActionForScene(it) {
                        initAlternativeScene()
                    }
                    TransitionManager.go(it, defaultTransition)
                }
            }
            false -> {
                defaultScene?.let {
                    prepareEnterActionForScene(it) {
                        initDefaultScene()
                    }
                    TransitionManager.go(it, defaultTransition)
                }
            }
        }
    }

    private fun prepareEnterActionForScene(
        scene: Scene,
        initScene: () -> Unit = {}
    ) {
        scene.apply {
            setEnterAction {
                takeIf { sceneRoot.childCount > 0 }?.let {
                    if (it == defaultScene)
                        bindingForDefaultScene = bindViewsFromScene(it)
                    else
                        bindingForAlternativeScene = bindViewsFromScene(it)
                }
                initScene()
            }
        }
    }

    private fun <T : ViewDataBinding> bindViewsFromScene(scene: Scene): T? {
        return DataBindingUtil.bind(scene.sceneRoot.getChildAt(PARENT_CHILD_POSITION))
    }

    abstract fun initDefaultScene()

    abstract fun initAlternativeScene()

    abstract fun initProperties()
}