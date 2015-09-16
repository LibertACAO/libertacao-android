package com.libertacao.libertacao.persistence;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.libertacao.libertacao.data.Event;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    //RUN Make-project BEFORE running this class!!

    public static void main(String[] args) throws Exception {
        final Class<?>[] classes = new Class[]{
                Event.class
        };

        writeConfigFile("ormlite_config.txt", classes);
    }
}
