package org.tonyq.ciloadanalyzer;

import java.util.Objects;

/**
 * 載入資訊的資料結構
 */
public class LoadInfo {
    private final String type;
    private final String path;
    private final String propertyName;
    private final String className;

    public LoadInfo(String type, String path, String propertyName, String className) {
        this.type = type;
        this.path = path;
        this.propertyName = propertyName;
        this.className = className;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadInfo loadInfo = (LoadInfo) o;
        return propertyName.equals(loadInfo.propertyName) && 
               type.equals(loadInfo.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, type);
    }

    @Override
    public String toString() {
        return String.format("LoadInfo{type='%s', path='%s', propertyName='%s', className='%s'}", 
            type, path, propertyName, className);
    }
}