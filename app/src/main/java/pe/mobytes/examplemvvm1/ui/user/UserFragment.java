package pe.mobytes.examplemvvm1.ui.user;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.binding.FragmentDataBindingComponent;
import pe.mobytes.examplemvvm1.databinding.FragmentUserBinding;
import pe.mobytes.examplemvvm1.di.Injectable;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.model.User;
import pe.mobytes.examplemvvm1.repository.Resource;
import pe.mobytes.examplemvvm1.ui.common.NavigationController;
import pe.mobytes.examplemvvm1.ui.common.RepoListAdapter;
import pe.mobytes.examplemvvm1.ui.common.RetryCall;
import pe.mobytes.examplemvvm1.utils.AutoClearedValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements Injectable {

    private static final String LOGIN_KEY = "login";

    @Inject
    ViewModelProvider.Factory viewModelfactory;

    @Inject
    NavigationController navigationController;

    androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private UserViewModel userViewModel;

    AutoClearedValue<FragmentUserBinding> binding;

    private AutoClearedValue<RepoListAdapter> adapter;

    public static UserFragment create(String login){
        UserFragment userFragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(LOGIN_KEY, login);
        userFragment.setArguments(args);
        return userFragment;
    }

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentUserBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user,
                container, false, dataBindingComponent);

        dataBinding.setRetryCallback(new RetryCall() {
            @Override
            public void retry() {
                userViewModel.retry();
            }
        });

        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel = ViewModelProviders.of(this, viewModelfactory).get(UserViewModel.class);
        userViewModel.setLogin(getArguments().getString(LOGIN_KEY));

        userViewModel.getUser().observe(this, new Observer<Resource<User>>() {
            @Override
            public void onChanged(Resource<User> userResource) {
                binding.get().setUser(userResource == null ? null: userResource.data);
                binding.get().setUserResource(userResource);
                binding.get().executePendingBindings();
            }
        });

        RepoListAdapter rvadapter = new RepoListAdapter(dataBindingComponent, new RepoListAdapter.RepoClickCallback() {
            @Override
            public void onClick(Repo repo) {
                navigationController.navigateToRepo(repo.owner.login, repo.name);
            }
        }, false);

        binding.get().repoList.setAdapter(rvadapter);
        this.adapter = new AutoClearedValue<>(this, rvadapter);
        initRepoList();
    }

    private void initRepoList(){
        userViewModel.getRepositories().observe(this, new Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(Resource<List<Repo>> listResource) {
                if(listResource == null){
                    adapter.get().replace(null);
                }else{
                    adapter.get().replace(listResource.data);
                }
            }
        });
    }

}
