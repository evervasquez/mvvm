package pe.mobytes.examplemvvm1.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "login")
public class User {

    @NonNull
    @SerializedName("login")
    public final String login;

    @SerializedName("avatar_url")
    public final String avatarUrl;

    @SerializedName("name")
    public final String name;

    @SerializedName("company")
    public final String company;

    @SerializedName("repos_url")
    public final String reposUrl;

    @SerializedName("blog")
    public final String blog;

    public User(String login, String avatarUrl, String name, String company, String reposUrl, String blog) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.company = company;
        this.reposUrl = reposUrl;
        this.blog = blog;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getBlog() {
        return blog;
    }
}
