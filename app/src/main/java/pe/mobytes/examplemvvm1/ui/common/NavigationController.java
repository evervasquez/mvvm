package pe.mobytes.examplemvvm1.ui.common;

import androidx.fragment.app.FragmentManager;
import javax.inject.Inject;
import pe.mobytes.examplemvvm1.MainActivity;
import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.ui.repo.RepoFragment;
import pe.mobytes.examplemvvm1.ui.search.SearchFragment;
import pe.mobytes.examplemvvm1.ui.user.UserFragment;


public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity activity){
        this.containerId = R.id.container;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void navigateToSearch(){
        SearchFragment searchFragment = new SearchFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, searchFragment)
                .commitAllowingStateLoss();
    }

    public void navigateToRepo(String owner, String name){
        RepoFragment fragment = RepoFragment.create(owner, name);
        String tag = "repo"+"/"+"/"+name;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToUser(String login){
        UserFragment fragment = UserFragment.create(login);
        String tag = "user"+"/"+login;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
