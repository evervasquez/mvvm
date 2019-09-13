package pe.mobytes.examplemvvm1.utils;

import androidx.lifecycle.LiveData;

public class AbsentLiveData extends LiveData {
    public AbsentLiveData() {
        postValue(null);
    }

    public static <T> LiveData<T> create(){
        return new AbsentLiveData();
    }
}
