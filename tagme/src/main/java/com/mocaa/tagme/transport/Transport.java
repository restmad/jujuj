package com.mocaa.tagme.transport;

import android.content.Context;

public interface Transport {

	public Object getMsg(Context context, Connection conn, Object obj, String[] array);
}
