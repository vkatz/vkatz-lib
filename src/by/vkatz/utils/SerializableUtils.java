package by.vkatz.utils;

import java.io.*;

/**
 * Created by vKatz
 */
public class SerializableUtils {
    public static boolean commit(File file, Object data) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(data);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T restore(File file, Class<T> T) {
        try {
            return (T) new ObjectInputStream(new FileInputStream(file)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return T.newInstance();
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    public static <T> long generateSerialVersionUID(Class<T> t) {
        long val = 5611235136846231L;
        String fullName = t.getName();
        for (int i = 0; i < fullName.length(); i++)
            val = 31 * val + fullName.charAt(i);
        return val;
    }
}
