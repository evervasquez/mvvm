package pe.mobytes.examplemvvm1.db;

import androidx.room.TypeConverter;
import androidx.room.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class GithubTypeConverters {

    @TypeConverter
    public static List<Integer> stringToIntList(String data){
        if(data ==null){
            return Collections.emptyList();
        }

        return StringUtil.splitToIntList(data);
    }

    @TypeConverter
    public static String inListToString(List<Integer> ints){
        return StringUtil.joinIntoString(ints);
    }
}
