package pe.mobytes.examplemvvm1.repository;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.Objects;

import pe.mobytes.examplemvvm1.AppExecutors;
import pe.mobytes.examplemvvm1.api.ApiResponse;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null));

        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType data) {
                result.removeSource(dbSource);

                if (NetworkBoundResource.this.shouldFetch(data)) {
                    NetworkBoundResource.this.fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, (ResultType newData) -> {
                        NetworkBoundResource.this.setValue(Resource.success(newData));
                    });
                }
            }
        });
    }


    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiReponse = createCall();

        //muestra los datos locales por mientras hace la peticion
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType newData) {
                NetworkBoundResource.this.setValue(Resource.loading(newData));
            }
        });

        result.addSource(apiReponse, response -> {
            result.removeSource(apiReponse);
            result.removeSource(dbSource);

            if (response.isSuccessFul()) {
                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        NetworkBoundResource.this.saveCallResult(NetworkBoundResource.this.processResponse(response));
                        appExecutors.mainTheread().execute(() -> {
                            result.addSource(NetworkBoundResource.this.loadFromDb(), newData ->
                                    NetworkBoundResource.this.setValue(Resource.success(newData)));
                        });
                    }
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }


    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    @MainThread
    protected abstract boolean shouldFetch(ResultType data);

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    protected void onFetchFailed() {
    }

    ;

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> reponse) {
        return reponse.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

}
