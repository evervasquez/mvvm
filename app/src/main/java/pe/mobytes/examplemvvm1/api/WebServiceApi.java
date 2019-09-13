package pe.mobytes.examplemvvm1.api;

import androidx.lifecycle.LiveData;

import java.util.List;

import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.model.RepoSearchResponse;
import pe.mobytes.examplemvvm1.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceApi {

    @GET("user/{login}")
    LiveData<ApiResponse<User>> getUser(@Path("login") String login);

    @GET("user/{login}/repos")
    LiveData<ApiResponse<List<Repo>>> getRepos(@Path("login") String login);

    @GET("repos/{owner}/{name}")
    LiveData<ApiResponse<Repo>> getRepo(@Path("owner") String owner, @Path("name") String name);

    @GET("repos/{owner}/{name}/contributors")
    LiveData<ApiResponse<List<Contributor>>> getContributors(@Path("owner") String owner,
                                                             @Path("name") String name);


    @GET("search/repositories")
    LiveData<ApiResponse<RepoSearchResponse>> searchRepos(@Query("q") String query);

    @GET("search/repositories")
    Call<RepoSearchResponse> searchRepos(@Query("q") String query, @Query("page") int page);
}
