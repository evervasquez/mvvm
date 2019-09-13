package pe.mobytes.examplemvvm1.repository;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import pe.mobytes.examplemvvm1.AppExecutors;
import pe.mobytes.examplemvvm1.api.ApiResponse;
import pe.mobytes.examplemvvm1.api.WebServiceApi;
import pe.mobytes.examplemvvm1.db.UserDao;
import pe.mobytes.examplemvvm1.model.User;

@Singleton
public class UserRepository {

    private final UserDao userDao;
    private final WebServiceApi githubService;
    private final AppExecutors appExecutors;

    @Inject
    UserRepository(AppExecutors appExecutors, UserDao userDao, WebServiceApi webServiceApi){
        this.userDao = userDao;
        this.appExecutors = appExecutors;
        this.githubService = webServiceApi;
    }

    public LiveData<Resource<User>> loadUser(String login){
        return new NetworkBoundResource<User, User>(appExecutors){

            @Override
            protected boolean shouldFetch(User data) {
                return data == null;
            }

            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.findByLogin(login);
            }

            @Override
            protected void saveCallResult(User user) {
                userDao.insert(user);
            }

            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return githubService.getUser(login);
            }
        }.asLiveData();
    }
}
