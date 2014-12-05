 package net.infidea.contextmon.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * ContextProvider;
 * A content provider for managing accumulated contexts
 * 
 * @author Moon Kwon Kim
 * Revision History:
 * v0.1, 131210, Moon Kwon Kim 
 */
public class ContextProvider extends ContentProvider {
	public static final Uri URI_CONTEXTS = Uri.parse("content://kr.co.smartylab.cma/contexts");
	private static final String AUTHORITY = "kr.co.smartylab.cma";
	private static UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "contexts", 1);
	}

	private ContextDBHelper contextDBHandler;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		contextDBHandler = new ContextDBHelper(getContext());
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = contextDBHandler.getWritableDatabase();
		long id = db.insert(ContextDBHelper.TABLE_NAME, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.withAppendedPath(URI_CONTEXTS, ""+id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor cursor = contextDBHandler.getReadableDatabase().query(
				ContextDBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		Log.d("QUERY", ""+cursor.getCount());
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
