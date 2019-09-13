package pe.mobytes.examplemvvm1.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.model.RepoSearchResult;
import pe.mobytes.examplemvvm1.model.User;

@Database(entities = {User.class, Repo.class, Contributor.class, RepoSearchResult.class}, version = 1)
public abstract class GithubDb extends RoomDatabase {

    abstract public UserDao userDao();
    abstract public RepoDao repoDao();
}
