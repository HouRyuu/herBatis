package jp.ac.jec.herBatis.io;

import java.io.InputStream;

public class Resources {

    /**
     * クラスローダーで資源ファイルをロードする
     *
     * @param resource 資源ファイルパース
     * @return InputStream
     */
    public static InputStream getResourceAsStream(String resource) {
        return Resources.class.getClassLoader().getResourceAsStream(resource);
    }

}
