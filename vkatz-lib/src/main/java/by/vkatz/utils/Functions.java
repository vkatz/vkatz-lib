package by.vkatz.utils;

/**
 * Created by Katz on 15.06.2016.
 */

public class Functions {
    public interface Func0<Result> {
        Result execute();
    }

    public interface Func1<Result, Param1> {
        Result execute(Param1 param1);
    }

    public interface Func2<Result, Param1, Param2> {
        Result execute(Param1 param1, Param2 param2);
    }

    public interface Func3<Result, Param1, Param2, Param3> {
        Result execute(Param1 param1, Param2 param2, Param3 param3);
    }
}
