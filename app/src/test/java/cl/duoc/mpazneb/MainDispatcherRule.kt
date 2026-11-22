package cl.duoc.mpazneb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regla de JUnit que reemplaza Dispatchers.Main por un dispatcher de prueba
 * (igual que en el repositorio my-pokemon del profesor).
 *
 * viewModelScope usa Dispatchers.Main, que no existe en las pruebas unitarias (JVM, sin Android).
 * Con StandardTestDispatcher las corrutinas no corren hasta llamar a advanceUntilIdle().
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
