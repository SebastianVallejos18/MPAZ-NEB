package cl.duoc.mpazneb.di

import cl.duoc.mpazneb.data.remote.RetrofitInstance
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.PokemonRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository

// Contenedor simple de dependencias: crea una sola instancia de cada repositorio.
// Los ViewModels los reciben por constructor; en las pruebas se reemplazan por mocks de MockK.
object AppModule {
    val usuarioRepository by lazy { UsuarioRepository(RetrofitInstance.usuarioApi) }
    val actividadRepository by lazy { ActividadRepository(RetrofitInstance.actividadApi) }
    val asignacionRepository by lazy { AsignacionRepository(RetrofitInstance.asignacionApi) }
    val pokemonRepository by lazy { PokemonRepository(RetrofitInstance.pokeApi) }
}
