package kr.co.smartylab.cma.monitor;

import android.content.ContentValues;

/**
IContextMonitor;
An interface to provide gathered contexts to other applications

Author: Moon Kwon Kim
Revision History:
v0.1 ----------------- 131207, Moon Kwon Kim
Adding start, stop, and getContexts
v0.2 ----------------- 131226, Moon Kwon Kim
Adding getCurrentContexts
*/

interface IContextMonitor {
	void start();
	void stop();
	ContentValues[] getContexts(int type, int maxNum);
	ContentValues[] getCurrentContexts(int type);
}