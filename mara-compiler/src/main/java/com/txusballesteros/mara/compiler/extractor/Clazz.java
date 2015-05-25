package com.txusballesteros.mara.compiler.extractor;

public class Clazz extends Model {
    private final String packageName;
    private final String className;

    public Clazz(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getCannonicalName() {
        if (!getPackageName().isEmpty()) {
            return String.format("%s.%s", getPackageName(), getClassName());
        } else {
            return getClassName();
        }
    }

    @Override
    public String toString() {
        return String.format("%s.%s", packageName, className);
    }
}
