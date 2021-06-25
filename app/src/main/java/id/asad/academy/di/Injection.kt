package id.asad.academy.di

import android.content.Context
import id.asad.academy.data.source.AcademyRepository
import id.asad.academy.data.source.local.LocalDataSource
import id.asad.academy.data.source.local.room.AcademyDatabase
import id.asad.academy.data.source.remote.RemoteDataSource
import id.asad.academy.utils.AppExecutors
import id.asad.academy.utils.JsonHelper

object Injection {

    fun provideRepository(context: Context): AcademyRepository {

        val database = AcademyDatabase.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance(JsonHelper(context))
        val localDataSource = LocalDataSource.getInstance(database.academyDao())
        val appExecutors = AppExecutors()
        return AcademyRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }
}