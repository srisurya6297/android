package com.nextcloud.client.mixins

import android.os.Bundle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.same
import org.junit.Test
import org.mockito.Mockito

class MixinRegistryTest {

    @Test
    fun `callbacks are invoked in order of calls and mixin registration`() {
        // GIVEN
        //      registry has 2 mixins registered
        val registry = MixinRegistry()
        val firstMixin = mock<ActivityMixin>()
        val secondMixin = mock<ActivityMixin>()
        registry.add(firstMixin, secondMixin)

        // WHEN
        //      all lifecycle callbacks are invoked
        val bundle = mock<Bundle>()
        registry.onCreate(bundle)
        registry.onStart()
        registry.onResume()
        registry.onPause()
        registry.onStop()
        registry.onDestroy()

        // THEN
        //      callbacks are invoked in order of mixin registration
        //      callbacks are invoked in order of registry calls
        Mockito.inOrder(firstMixin, secondMixin).apply {
            verify(firstMixin).onCreate(same(bundle))
            verify(secondMixin).onCreate(same(bundle))
            verify(firstMixin).onStart()
            verify(secondMixin).onStart()
            verify(firstMixin).onResume()
            verify(secondMixin).onResume()
            verify(firstMixin).onPause()
            verify(secondMixin).onPause()
            verify(firstMixin).onStop()
            verify(secondMixin).onStop()
            verify(firstMixin).onDestroy()
            verify(secondMixin).onDestroy()
        }
    }
}
