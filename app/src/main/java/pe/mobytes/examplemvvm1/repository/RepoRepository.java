package pe.mobytes.examplemvvm1.repository;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import pe.mobytes.examplemvvm1.AppExecutors;
import pe.mobytes.examplemvvm1.api.ApiResponse;
import pe.mobytes.examplemvvm1.api.WebServiceApi;
import pe.mobytes.examplemvvm1.db.GithubDb;
import pe.mobytes.examplemvvm1.db.RepoDao;
import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.model.RepoSearchResponse;
import pe.mobytes.examplemvvm1.model.RepoSearchResult;
import pe.mobytes.examplemvvm1.utils.AbsentLiveData;
import pe.mobytes.examplemvvm1.utils.RateLimiter;

@Singleton
public class RepoRepository {

    private final GithubDb db;
    private final RepoDao repoDao;
    private final WebServiceApi serviceApi;
    private final AppExecutors appExecutors;

    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public RepoRepository(GithubDb db, RepoDao repoDao, WebServiceApi serviceApi, AppExecutors appExecutors) {
        this.db = db;
        this.repoDao = repoDao;
        this.serviceApi = serviceApi;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<List<Repo>>> loadRepos(String owner){
        return new NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors){

            @Override
            protected boolean shouldFetch(List<Repo> data) {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner);
            }

            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return repoDao.loadRepositories(owner);
            }

            @Override
            protected void saveCallResult(List<Repo> items) {
                repoDao.insertRepos(items);
            }

            @Override
            protected LiveData<ApiResponse<List<Repo>>> createCall() {
                return serviceApi.getRepos(owner);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimit.reset(owner);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Repo>> loadRepo(String owner, String name){
        return new NetworkBoundResource<Repo, Repo>(appExecutors){

            @Override
            protected boolean shouldFetch(Repo data) {
                return data == null;
            }

            @Override
            protected LiveData<Repo> loadFromDb() {
                return repoDao.load(owner, name);
            }

            @Override
            protected void saveCallResult(Repo repo) {
                repoDao.insert(repo);
            }

            @Override
            protected LiveData<ApiResponse<Repo>> createCall() {
                return serviceApi.getRepo(owner, name);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Contributor>>> loadContributors(String owner, String name){
        return new NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors){

            @Override
            protected boolean shouldFetch(List<Contributor> data) {
                return data == null || data.isEmpty();
            }

            @Override
            protected LiveData<List<Contributor>> loadFromDb() {
                return repoDao.loadContributors(name, owner);
            }

            @Override
            protected void saveCallResult(List<Contributor> contributors) {
                for(Contributor contributor: contributors){
                    contributor.setRepoName(name);
                    contributor.setRepoOwner(owner);
                }

                db.beginTransaction();
                try{
                    repoDao.createRepoIfNotExists(new Repo(Repo.UNKNOWN_ID, name, owner+"/"+name,
                            "", 0, new Repo.Owner(owner, null)));
                    repoDao.insertContributors(contributors);
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
            }

            @Override
            protected LiveData<ApiResponse<List<Contributor>>> createCall() {
                return serviceApi.getContributors(owner, name);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query){
        FetchNextSearchPageTask fetchNextSearchPageTask = new FetchNextSearchPageTask(
                query, serviceApi, db
        );
        appExecutors.network().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    public LiveData<Resource<List<Repo>>> search(String query){
        return new NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors){

            @Override
            protected boolean shouldFetch(List<Repo> data) {
                return data == null;
            }

            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return Transformations.switchMap(repoDao.search(query), new Function<RepoSearchResult, LiveData<List<Repo>>>() {
                    @Override
                    public LiveData<List<Repo>> apply(RepoSearchResult searchData) {
                        if(searchData == null){
                            return AbsentLiveData.create();
                        }else{
                            return repoDao.loadOrdered(searchData.repoIds);
                        }
                    }
                });
            }

            @Override
            protected void saveCallResult(RepoSearchResponse item) {
                List<Integer> repoIds = item.getRepoIds();
                RepoSearchResult repoSearchResult = new RepoSearchResult(
                        query, repoIds, item.getTotal(), item.getNextPage()
                );

                db.beginTransaction();
                try{
                    repoDao.insertRepos(item.getItems());
                    repoDao.insert(repoSearchResult);
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
            }

            @Override
            protected LiveData<ApiResponse<RepoSearchResponse>> createCall() {
                return serviceApi.searchRepos(query);
            }

            @Override
            protected RepoSearchResponse processResponse(ApiResponse<RepoSearchResponse> response) {
                RepoSearchResponse body = response.body;
                if(body != null){
                    body.setNextPage(response.getNextPage());
                }

                return body;
            }
        }.asLiveData();
    }
}
