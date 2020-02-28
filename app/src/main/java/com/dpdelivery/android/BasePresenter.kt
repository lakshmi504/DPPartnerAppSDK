package com.dpdelivery.android

interface BasePresenter<T> {

    /**
     * Binds presenter with a navigationView when resumed. The Presenter will perform initialization here.
     *
     * @param view the navigationView associated with this presenter
     */
    fun takeView(view: T?)

    /**
     * Drops the reference to the view when destroyed
     */
    fun dropView()
}