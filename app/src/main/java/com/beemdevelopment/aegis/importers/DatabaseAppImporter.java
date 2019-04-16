package com.beemdevelopment.aegis.importers;

import android.content.Context;
import android.content.pm.PackageManager;

import com.topjohnwu.superuser.io.SuFile;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DatabaseAppImporter implements DatabaseImporter {
    private static Map<String, Class<? extends DatabaseAppImporter>> _importers;
    static {
        // note: keep this list sorted alphabetically
        LinkedHashMap<String, Class<? extends DatabaseAppImporter>> importers = new LinkedHashMap<>();
        importers.put("Google Authenticator", GoogleAuthAppImporter.class);
        importers.put("Steam", SteamAppImporter.class);
        _importers = Collections.unmodifiableMap(importers);
    }

    private SuFile _path;
    private Context _context;

    protected DatabaseAppImporter(Context context, String pkgName, String subPath) throws DatabaseImporterException {
        _context = context;

        try {
            PackageManager man = context.getPackageManager();
            _path = new SuFile(man.getApplicationInfo(pkgName, 0).dataDir, subPath);
        } catch (PackageManager.NameNotFoundException e) {
            throw new DatabaseImporterException(e);
        }
    }

    protected SuFile getPath() {
        return _path;
    }

    public abstract void parse() throws DatabaseImporterException;

    public abstract DatabaseImporterResult convert() throws DatabaseImporterException;

    public abstract boolean isEncrypted();

    public Context getContext() {
        return _context;
    }

    public static DatabaseAppImporter create(Context context, Class<? extends DatabaseAppImporter> type) {
        try {
            return type.getConstructor(Context.class).newInstance(context);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Class<? extends DatabaseAppImporter>> getImporters() {
        return _importers;
    }
}
