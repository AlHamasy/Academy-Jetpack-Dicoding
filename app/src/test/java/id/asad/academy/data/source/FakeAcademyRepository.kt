package id.asad.academy.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.asad.academy.data.source.local.LocalDataSource
import id.asad.academy.data.source.local.entity.ContentEntity
import id.asad.academy.data.source.local.entity.CourseEntity
import id.asad.academy.data.source.local.entity.CourseWithModule
import id.asad.academy.data.source.local.entity.ModuleEntity
import id.asad.academy.data.source.remote.ApiResponse
import id.asad.academy.data.source.remote.RemoteDataSource
import id.asad.academy.data.source.remote.response.ContentResponse
import id.asad.academy.data.source.remote.response.CourseResponse
import id.asad.academy.data.source.remote.response.ModuleResponse
import id.asad.academy.utils.AppExecutors
import id.asad.academy.vo.Resource

class FakeAcademyRepository(private val remoteDataSource: RemoteDataSource,
                            private val localDataSource: LocalDataSource,
                            private val appExecutors: AppExecutors) : AcademyDataSource {

    override fun getAllCourses(): LiveData<Resource<List<CourseEntity>>> {

        return object : NetworkBoundResource<List<CourseEntity>, List<CourseResponse>>(appExecutors) {
            public override fun loadFromDB(): LiveData<List<CourseEntity>> =
                localDataSource.getAllCourses()
            override fun shouldFetch(data: List<CourseEntity>?): Boolean =
                data == null || data.isEmpty()
            public override fun createCall(): LiveData<ApiResponse<List<CourseResponse>>> =
                remoteDataSource.getAllCourses()
            public override fun saveCallResult(courseResponses: List<CourseResponse>) {
                val courseList = ArrayList<CourseEntity>()
                for (response in courseResponses) {
                    val course = CourseEntity(response.id,
                        response.title,
                        response.description,
                        response.date,
                        false,
                        response.imagePath)
                    courseList.add(course)
                }
                localDataSource.insertCourses(courseList)
            }
        }.asLiveData()
    }

    override fun getCourseWithModules(courseId: String): LiveData<Resource<CourseWithModule>> {

        return object : NetworkBoundResource<CourseWithModule, List<ModuleResponse>>(appExecutors) {
            override fun loadFromDB(): LiveData<CourseWithModule> =
                localDataSource.getCourseWithModules(courseId)
            override fun shouldFetch(courseWithModule: CourseWithModule?): Boolean =
                courseWithModule?.mModules == null || courseWithModule.mModules.isEmpty()
            override fun createCall(): LiveData<ApiResponse<List<ModuleResponse>>> =
                remoteDataSource.getModules(courseId)
            override fun saveCallResult(moduleResponses: List<ModuleResponse>) {
                val moduleList = ArrayList<ModuleEntity>()
                for (response in moduleResponses) {
                    val course = ModuleEntity(response.moduleId,
                        response.courseId,
                        response.title,
                        response.position,
                        false)
                    moduleList.add(course)
                }
                localDataSource.insertModules(moduleList)
            }
        }.asLiveData()
    }

    override fun getAllModulesByCourse(courseId: String): LiveData<Resource<List<ModuleEntity>>> {

        return object : NetworkBoundResource<List<ModuleEntity>, List<ModuleResponse>>(appExecutors) {
            override fun loadFromDB(): LiveData<List<ModuleEntity>> =
                localDataSource.getAllModulesByCourse(courseId)
            override fun shouldFetch(modules: List<ModuleEntity>?): Boolean =
                modules == null || modules.isEmpty()
            override fun createCall(): LiveData<ApiResponse<List<ModuleResponse>>> =
                remoteDataSource.getModules(courseId)
            override fun saveCallResult(moduleResponses: List<ModuleResponse>) {
                val moduleList = ArrayList<ModuleEntity>()
                for (response in moduleResponses) {
                    val course = ModuleEntity(response.moduleId,
                        response.courseId,
                        response.title,
                        response.position,
                        false)
                    moduleList.add(course)
                }
                localDataSource.insertModules(moduleList)
            }
        }.asLiveData()
    }

    override fun getContent(moduleId: String): LiveData<Resource<ModuleEntity>> {

        return object : NetworkBoundResource<ModuleEntity, ContentResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<ModuleEntity> =
                localDataSource.getModuleWithContent(moduleId)
            override fun shouldFetch(moduleEntity: ModuleEntity?): Boolean =
                moduleEntity?.contentEntity == null
            override fun createCall(): LiveData<ApiResponse<ContentResponse>> =
                remoteDataSource.getContent(moduleId)
            override fun saveCallResult(contentResponse: ContentResponse) =
                localDataSource.updateContent(contentResponse.content.toString(), moduleId)
        }.asLiveData()
    }

    override fun getBookmarkedCourses(): LiveData<List<CourseEntity>> {
        return localDataSource.getBookmarkedCourses()
    }

    override fun setCourseBookmark(course: CourseEntity, state: Boolean) {
        return appExecutors.diskIO().execute { localDataSource.setCourseBookmark(course, state) }
    }

    override fun setReadModule(module: ModuleEntity) {
        return appExecutors.diskIO().execute { localDataSource.setReadModule(module) }
    }


}