package pe.mobytes.examplemvvm1.di;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pe.mobytes.examplemvvm1.api.WebServiceApi;
import pe.mobytes.examplemvvm1.db.GithubDb;
import pe.mobytes.examplemvvm1.db.RepoDao;
import pe.mobytes.examplemvvm1.db.UserDao;
import pe.mobytes.examplemvvm1.utils.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Singleton
    @Provides
    WebServiceApi provideWebServiceApi(){
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(WebServiceApi.class);
    }

    @Singleton
    @Provides
    GithubDb provideGithubDb(Application application){
        return Room.databaseBuilder(application, GithubDb.class, "github.db").build();
    }

    @Singleton
    @Provides
    UserDao providesUserDao(GithubDb db){
        return db.userDao();
    }

    @Singleton
    @Provides
    RepoDao providesRepoDao(GithubDb db){
        return db.repoDao();
    }
}

