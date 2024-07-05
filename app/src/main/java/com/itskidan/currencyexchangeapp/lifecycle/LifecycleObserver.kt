package com.itskidan.currencyexchangeapp.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

class LifecycleObserver : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_ANY -> Timber.tag("MyLog").d("LifeCycle: OnAny")
            Lifecycle.Event.ON_CREATE -> Timber.tag("MyLog").d("LifeCycle: OnCreate")
            Lifecycle.Event.ON_START -> Timber.tag("MyLog").d("LifeCycle: OnStart")
            Lifecycle.Event.ON_RESUME -> Timber.tag("MyLog").d("LifeCycle: OnResume")
            Lifecycle.Event.ON_PAUSE -> Timber.tag("MyLog").d("LifeCycle: OnPause")
            Lifecycle.Event.ON_STOP -> Timber.tag("MyLog").d("LifeCycle: OnStop")
            Lifecycle.Event.ON_DESTROY -> Timber.tag("MyLog").d("LifeCycle: OnDestroy")
            else -> Timber.tag("MyLog").d("LifeCycle: ")
        }
    }
}