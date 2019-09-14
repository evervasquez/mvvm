package pe.mobytes.examplemvvm1.ui.repo;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.binding.FragmentDataBindingComponent;
import pe.mobytes.examplemvvm1.databinding.FragmentRepoBinding;
import pe.mobytes.examplemvvm1.di.Injectable;
import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.repository.Resource;
import pe.mobytes.examplemvvm1.ui.common.NavigationController;
import pe.mobytes.examplemvvm1.ui.common.RetryCall;
import pe.mobytes.examplemvvm1.utils.AutoClearedValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepoFragment extends Fragment implements Injectable {

    private static final String REPO_OWNER_KEY = "repo_owner";
    private static final String REPO_NAME_KEY = "repo_name";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RepoViewModel repoViewModel;

    @Inject
    NavigationController navigationController;

    androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<FragmentRepoBinding> binding;

    AutoClearedValue<ContributorAdapter> adapter;

    public RepoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repoViewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(REPO_OWNER_KEY) && args.containsKey(REPO_NAME_KEY)) {
            repoViewModel.setId(args.getString(REPO_OWNER_KEY), args.getString(REPO_NAME_KEY));
        } else {
            repoViewModel.setId(null, null);
        }

        LiveData<Resource<Repo>> repo = repoViewModel.getRepo();
        repo.observe(this, new Observer<Resource<Repo>>() {
            @Override
            public void onChanged(Resource<Repo> repoResource) {
                binding.get().setRepo(repoResource == null ? null : repoResource.data);
                binding.get().setRepoResource(repoResource);
                binding.get().executePendingBindings();
            }
        });

        ContributorAdapter adapter = new ContributorAdapter(dataBindingComponent, new ContributorAdapter.ContributorClickCallback() {
            @Override
            public void onClick(Contributor contributor) {
                navigationController.navigateToUser(contributor.getLogin());
            }
        });

        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().contributorList.setAdapter(adapter);
        initContributorList(repoViewModel);
    }

    private void initContributorList(RepoViewModel viewModel) {
        viewModel.getContributors().observe(this, new Observer<Resource<List<Contributor>>>() {
            @Override
            public void onChanged(Resource<List<Contributor>> listResource) {
                if (listResource != null && listResource.data != null) {
                    adapter.get().replace(listResource.data);
                } else {
                    adapter.get().replace(Collections.emptyList());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentRepoBinding repoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo,
                container, false);
        repoBinding.setRetryCallback(new RetryCall() {
            @Override
            public void retry() {
                repoViewModel.retry();
            }
        });

        binding = new AutoClearedValue<>(this, repoBinding);
        return repoBinding.getRoot();
    }

    public static RepoFragment create(String owner, String name) {
        RepoFragment repoFragment = new RepoFragment();
        Bundle args = new Bundle();
        args.putString(REPO_OWNER_KEY, owner);
        args.putString(REPO_NAME_KEY, name);
        repoFragment.setArguments(args);
        return repoFragment;
    }
}
