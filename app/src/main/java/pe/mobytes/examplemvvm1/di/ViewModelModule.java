package pe.mobytes.examplemvvm1.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import pe.mobytes.examplemvvm1.ui.repo.RepoViewModel;
import pe.mobytes.examplemvvm1.ui.search.SearchViewModel;
import pe.mobytes.examplemvvm1.ui.user.UserViewModel;
import pe.mobytes.examplemvvm1.viewmodel.GithubViewModelFactory;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel bindUserViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(RepoViewModel.class)
    abstract ViewModel bindRepoViewModel(RepoViewModel repoViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelfactory(GithubViewModelFactory factory);
}
