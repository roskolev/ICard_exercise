package com.example.icard_ex.presenters

import android.content.Context

abstract class BasePresenter<T>(val context: Context) {
    protected var view: T? = null
    protected var isFirstSubscribe = true
    protected var isSubscribed = false

    fun attachView(view: T) {
        this.view = view
        onViewAttached(view)
    }

    protected open fun onViewAttached(view: T) {}

    open fun subscribe(){
        isSubscribed = true
    }

    open fun unSubscribe(){
        isSubscribed = false
        isFirstSubscribe = false
    }

    fun detachView() {
        view = null
        onViewDetached()
    }

    protected open fun onViewDetached() {}
}
