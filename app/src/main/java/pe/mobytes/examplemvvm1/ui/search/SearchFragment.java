package pe.mobytes.examplemvvm1.ui.search;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.binding.FragmentDataBindingComponent;
import pe.mobytes.examplemvvm1.databinding.FragmentSearchBinding;
import pe.mobytes.examplemvvm1.di.Injectable;
import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.repository.Resource;
import pe.mobytes.examplemvvm1.ui.common.NavigationController;
import pe.mobytes.examplemvvm1.ui.common.RepoListAdapter;
import pe.mobytes.examplemvvm1.ui.common.RetryCall;
import pe.mobytes.examplemvvm1.utils.AutoClearedValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelProvider;

    @Inject
    NavigationController navigationController;

    androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<FragmentSearchBinding> binding;
    AutoClearedValue<RepoListAdapter> adapter;

    private SearchViewModel searchViewModel;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSearchBinding dataBindign = DataBindingUtil.inflate(inflater,
                R.layout.fragment_search, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBindign);

        return dataBindign.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchViewModel = ViewModelProviders.of(this, viewModelProvider).get(SearchViewModel.class);
        initRecyclerView();

        RepoListAdapter rvAdapter = new RepoListAdapter(dataBindingComponent, new RepoListAdapter.RepoClickCallback() {
            @Override
            public void onClick(Repo repo) {
                navigationController.navigateToRepo(repo.owner.login, repo.name);
            }
        }, true);

        // recyceview
        binding.get().repoList.setAdapter(rvAdapter);
        adapter = new AutoClearedValue<>(this, rvAdapter);

        initSearchInputListener();

        binding.get().setCallback(new RetryCall() {
            @Override
            public void retry() {
                searchViewModel.refresh();
            }
        });
    }

    private void initSearchInputListener(){
        binding.get().input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    SearchFragment.this.doSearch(textView);
                    return true;
                }
                return false;
            }
        });

        binding.get().input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    doSearch(view);
                    return true;
                }
                return false;
            }
        });
    }

    private void doSearch(View view){
        String query = binding.get().input.getText().toString();
        dismissKeyboard(view.getWindowToken());
        binding.get().setQuery(query);
        searchViewModel.setQuery(query);
    }

    private void initRecyclerView(){
        binding.get().repoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if(lastPosition == adapter.get().getItemCount() - 1){
                    searchViewModel.loadNextPage();
                }
            }
        });

        searchViewModel.getResults().observe(this, new Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(Resource<List<Repo>> result) {
                binding.get().setSearchResource(result);
                binding.get().setResultCount((result == null || result.data == null) ? 0: result.data.size());
                adapter.get().replace(result == null? null: result.data);
                binding.get().executePendingBindings();
            }
        });

        searchViewModel.getLoadMoreStatus().observe(this, new Observer<SearchViewModel.LoadMoreState>() {
            @Override
            public void onChanged(SearchViewModel.LoadMoreState loadingMore) {
                if(loadingMore == null){
                    binding.get().setLoadingMore(false);
                }else{
                    binding.get().setLoadingMore(loadingMore.isRunning());
                    String error = loadingMore.getErrorMessageIfNoHandled();
                    if(error != null){
                        Log.d("TAG1", "Error in loadMore");
                    }
                }

                binding.get().executePendingBindings();
            }
        });
    }

    private void dismissKeyboard(IBinder windowToken){
        FragmentActivity activity = getActivity();
        if(activity != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }
}
