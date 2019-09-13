package pe.mobytes.examplemvvm1.db;

import android.util.SparseIntArray;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.model.RepoSearchResult;

@Dao
public abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Repo... repos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContributors(List<Contributor> contributors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRepos(List<Repo> repos);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createRepoIfNotExists(Repo repo);

    @Query("SELECT * FROM repo WHERE owner_login = :login AND name = :name")
    public abstract LiveData<Repo> load(String login, String name);

    @Query("SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor " +
            "WHERE repoName = :name AND repoOwner = :owner ORDER BY contributions DESC")
    public abstract LiveData<List<Contributor>> loadContributors(String name, String owner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RepoSearchResult result);

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    public abstract LiveData<RepoSearchResult> search(String query);

    @Query("SELECT * FROM Repo WHERE id in (:repoIds)")
    protected abstract LiveData<List<Repo>> loadById(List<Integer> repoIds);

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    public abstract RepoSearchResult findSearchResult(String query);

    @Query("SELECT * FROM Repo WHERE owner_login = :owner ORDER BY starts DESC")
    public abstract LiveData<List<Repo>> loadRepositories(String owner);

    public LiveData<List<Repo>> loadOrdered(List<Integer> repoIds){
        SparseIntArray order = new SparseIntArray();
        int index = 0;

        for(Integer repoId: repoIds){
            order.put(repoId, index++);
        }

        return Transformations.map(loadById(repoIds), new Function<List<Repo>, List<Repo>>() {
            @Override
            public List<Repo> apply(List<Repo> repoList) {
                Collections.sort(repoList, new Comparator<Repo>() {
                    @Override
                    public int compare(Repo repo1, Repo repo2) {
                        int pos1 = order.get(repo1.id);
                        int pos2 = order.get(repo2.id);
                        return  pos1 - pos2;
                    }
                });

                return repoList;
            }
        });
    }
}