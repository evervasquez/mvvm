package pe.mobytes.examplemvvm1.ui.common;

import androidx.fragment.app.FragmentManager;
import javax.inject.Inject;
import pe.mobytes.examplemvvm1.MainActivity;
import pe.mobytes.examplemvvm1.R;


public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity activity){
        this.containerId = R.id.container;
        this.fragmentManager = activity.getSupportFragmentManager();
    }
}
