package pe.mobytes.examplemvvm1.repository;

public class Resource<T> {

    public final Status status;
    public final String message;
    public final T data;


    public Resource(Status status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Resource<T> success(T data){
        return new Resource<>(Status.SUCCESS, null, data);
    }

    public static <T> Resource<T> error(String message, T data){
        return new Resource<>(Status.ERROR, message, data);
    }

    public static <T> Resource<T> loading(T data){
        return new Resource<>(Status.LOADING, null, data);
    }
}
