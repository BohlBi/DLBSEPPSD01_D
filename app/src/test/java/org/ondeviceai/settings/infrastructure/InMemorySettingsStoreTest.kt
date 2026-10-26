package org.ondeviceai.settings.infrastructure

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemorySettingsStoreTest {

    @Test
    fun `uses GPU by default`() {
        assertTrue(InMemorySettingsStore().useGpu.value)
    }

    @Test
    fun `setUseGpu changes the value`() {
        val store = InMemorySettingsStore()

        store.setUseGpu(false)

        assertFalse(store.useGpu.value)
    }
}
