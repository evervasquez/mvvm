package pe.mobytes.examplemvvm1.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import pe.mobytes.examplemvvm1.ui.repo.RepoFragment;
import pe.mobytes.examplemvvm1.ui.search.SearchFragment;
import pe.mobytes.examplemvvm1.ui.user.UserFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RepoFragment contributeRepoFragment();

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();
}
