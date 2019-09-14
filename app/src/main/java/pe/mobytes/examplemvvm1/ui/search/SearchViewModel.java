package pe.mobytes.examplemvvm1.ui.search;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import pe.mobytes.examplemvvm1.model.Repo;
import pe.mobytes.examplemvvm1.repository.RepoRepository;
import pe.mobytes.examplemvvm1.repository.Resource;
import pe.mobytes.examplemvvm1.utils.AbsentLiveData;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<Resource<List<Repo>>> results;
    private final NextPageHandler nextPageHandler;

    @Inject
    SearchViewModel(RepoRepository repository) {
        nextPageHandler = new NextPageHandler(repository);
        results = Transformations.switchMap(query, new Function<String, LiveData<Resource<List<Repo>>>>() {
            @Override
            public LiveData<Resource<List<Repo>>> apply(String search) {
                if (search == null || search.trim().length() == 0) {
                    return AbsentLiveData.create();
                } else {
                    return repository.search(search);
                }
            }
        });
    }

    public LiveData<Resource<List<Repo>>> getResults(){
        return results;
    }

    public void setQuery(String originalInput){
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if(Objects.equals(input, query.getValue())){
            return;
        }

        nextPageHandler.reset();
        query.setValue(input);
    }

    public LiveData<LoadMoreState> getLoadMoreStatus(){
        return nextPageHandler.getLoadMorestate();
    }

    public void loadNextPage(){
        String value = query.getValue();
        if(value == null || value.trim().length() == 0){
            return;
        }
        nextPageHandler.queryNextPage(value);
    }

    public void refresh(){
        if(query.getValue() != null){
            query.setValue(query.getValue());
        }
    }

    static class LoadMoreState {

        private final boolean running;
        private final String errorMessage;
        private boolean handledError = false;

        public LoadMoreState(boolean running, String errorMessage) {
            this.errorMessage = errorMessage;
            this.running = running;
        }

        boolean isRunning() {
            return running;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNoHandled() {
            if (handledError) {
                return null;
            }

            handledError = true;
            return errorMessage;
        }
    }

    static class NextPageHandler implements Observer<Resource<Boolean>> {
        private LiveData<Resource<Boolean>> nexPageLiveData;
        private final MutableLiveData<LoadMoreState> loadMorestate = new MutableLiveData<>();
        private String query;
        private final RepoRepository repoRepository;
        boolean hasMore;

        @Override
        public void onChanged(Resource<Boolean> result) {
            if (result == null) {
                reset();
            } else {
                switch (result.status) {
                    case SUCCESS:
                        hasMore = Boolean.TRUE.equals(result.data);
                        unregister();
                        loadMorestate.setValue(new LoadMoreState(false, null));
                        break;
                    case ERROR:
                        hasMore = true;
                        unregister();
                        loadMorestate.setValue(new LoadMoreState(false, result.message));
                        break;
                }
            }
        }

        NextPageHandler(RepoRepository repoRepository) {
            this.repoRepository = repoRepository;
            reset();
        }

        void queryNextPage(String query) {
            if (Objects.equals(this.query, query)) {
                return;
            }
            unregister();
            this.query = query;
            nexPageLiveData = repoRepository.searchNextPage(query);
            nexPageLiveData.observeForever(this);
        }

        private void unregister() {
            if (nexPageLiveData != null) {
                nexPageLiveData.removeObserver(this);
                nexPageLiveData = null;
                if (hasMore) {
                    query = null;
                }
            }
        }

        private void reset() {
            unregister();
            hasMore = true;
            loadMorestate.setValue(new LoadMoreState(false, null));
        }

        MutableLiveData<LoadMoreState> getLoadMorestate() {
            return loadMorestate;
        }
    }
}
