package com.gjad.remember.di



import android.app.Application
import androidx.room.Room
import com.gjad.remember.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloApp {

    /*@Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(
        auth = Firebase.auth
    )*/

    @Provides
    @Singleton
    fun provideEntidadesDB(app: Application): EntidadesDB {
        return Room.databaseBuilder(
            app,
            EntidadesDB::class.java,
            "entidades_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecordatorioRepositorio(db: EntidadesDB): RecordatorioRepositorio {
        return RecordatorioRepositorioImplementacion(
            db.recordatoriodao)
    }

    @Provides
    @Singleton
    fun provideTareaRepositorio(db: EntidadesDB): TareaRepositorio {
        return TareaRepositorioImplementacion(
            db.tododao)
    }
}