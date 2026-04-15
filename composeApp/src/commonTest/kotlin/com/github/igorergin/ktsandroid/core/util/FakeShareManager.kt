package com.github.igorergin.ktsandroid.core.util

class FakeShareManager : ShareManager {
    var sharedText: String? = null
    override fun shareText(text: String) {
        sharedText = text
    }
}
