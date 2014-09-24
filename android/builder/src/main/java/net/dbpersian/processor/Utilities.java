package net.dbpersian.processor;


import java.io.IOException;
import java.io.InputStream;


final class Utilities
{
    private Utilities() {}

    public static String convertStreamToString(InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String readResourceAsString(Class clazz, String resName) throws IOException
    {
        final InputStream templateIS = clazz.getResourceAsStream(resName);
        try {
            return Utilities.convertStreamToString(templateIS);
        } finally {
            if (templateIS != null) {
                templateIS.close();
            }
        }
    }

    public static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
