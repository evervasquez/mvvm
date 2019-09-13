package pe.mobytes.examplemvvm1.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import pe.mobytes.examplemvvm1.api.ApiResponse;
import pe.mobytes.examplemvvm1.api.WebServiceApi;
import pe.mobytes.examplemvvm1.db.GithubDb;
import pe.mobytes.examplemvvm1.model.RepoSearchResponse;
import pe.mobytes.examplemvvm1.model.RepoSearchResult;
import retrofit2.Response;

public class FetchNextSearchPageTask implements Runnable {
    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private final String query;
    private final WebServiceApi serviceApi;
    private final GithubDb db;

    public FetchNextSearchPageTask(String query, WebServiceApi serviceApi, GithubDb db) {
        this.query = query;
        this.serviceApi = serviceApi;
        this.db = db;
    }

    @Override
    public void run() {
        RepoSearchResult current = db.repoDao().findSearchResult(query);

        if (current == null) {
            liveData.postValue(null);
            return;
        }
        final Integer nextPage = current.next;
        if (nextPage == null) {
            liveData.postValue(Resource.success(false));
            return;
        }

        try {
            Response<RepoSearchResponse> response = serviceApi.searchRepos(query, nextPage).execute();
            ApiResponse<RepoSearchResponse> apiReponse = new ApiResponse<RepoSearchResponse>(response);
            if (apiReponse.isSuccessFul()) {
                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.repoIds);
                ids.addAll(apiReponse.body.getRepoIds());

                RepoSearchResult merged = new RepoSearchResult(query, ids,
                        apiReponse.body.total, apiReponse.getNextPage());

                try {
                    db.beginTransaction();
                    db.repoDao().inser(merged);
                    db.repoDao().insertRepos(apiReponse.body.getItems());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                liveData.postValue(Resource.success(apiReponse.getNextPage() != null));
            } else {
                liveData.postValue(Resource.error(apiReponse.errorMessage, true));
            }
        } catch (Exception e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }


    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }
}
