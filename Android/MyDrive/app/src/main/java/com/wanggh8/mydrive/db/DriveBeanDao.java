package com.wanggh8.mydrive.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wanggh8.mydrive.bean.DriveBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DRIVE_BEAN".
*/
public class DriveBeanDao extends AbstractDao<DriveBean, String> {

    public static final String TABLENAME = "DRIVE_BEAN";

    /**
     * Properties of entity DriveBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Name = new Property(0, String.class, "name", false, "NAME");
        public final static Property MyName = new Property(1, String.class, "myName", false, "MY_NAME");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
        public final static Property IconId = new Property(3, int.class, "iconId", false, "ICON_ID");
        public final static Property Id = new Property(4, String.class, "id", true, "ID");
        public final static Property AccessToken = new Property(5, String.class, "accessToken", false, "ACCESS_TOKEN");
    }


    public DriveBeanDao(DaoConfig config) {
        super(config);
    }
    
    public DriveBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DRIVE_BEAN\" (" + //
                "\"NAME\" TEXT," + // 0: name
                "\"MY_NAME\" TEXT," + // 1: myName
                "\"TYPE\" TEXT," + // 2: type
                "\"ICON_ID\" INTEGER NOT NULL ," + // 3: iconId
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 4: id
                "\"ACCESS_TOKEN\" TEXT);"); // 5: accessToken
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DRIVE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DriveBean entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        String myName = entity.getMyName();
        if (myName != null) {
            stmt.bindString(2, myName);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
        stmt.bindLong(4, entity.getIconId());
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(5, id);
        }
 
        String accessToken = entity.getAccessToken();
        if (accessToken != null) {
            stmt.bindString(6, accessToken);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DriveBean entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        String myName = entity.getMyName();
        if (myName != null) {
            stmt.bindString(2, myName);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
        stmt.bindLong(4, entity.getIconId());
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(5, id);
        }
 
        String accessToken = entity.getAccessToken();
        if (accessToken != null) {
            stmt.bindString(6, accessToken);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
    }    

    @Override
    public DriveBean readEntity(Cursor cursor, int offset) {
        DriveBean entity = new DriveBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // name
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // myName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // type
            cursor.getInt(offset + 3), // iconId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // accessToken
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DriveBean entity, int offset) {
        entity.setName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setMyName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIconId(cursor.getInt(offset + 3));
        entity.setId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAccessToken(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final String updateKeyAfterInsert(DriveBean entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(DriveBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DriveBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}