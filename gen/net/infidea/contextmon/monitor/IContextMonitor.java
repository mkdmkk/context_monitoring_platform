/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/mkk/Development/ws-eclipse-android/Context Monitoring Platform/src/net/infidea/contextmon/monitor/IContextMonitor.aidl
 */
package net.infidea.contextmon.monitor;
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
public interface IContextMonitor extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.infidea.contextmon.monitor.IContextMonitor
{
private static final java.lang.String DESCRIPTOR = "net.infidea.contextmon.monitor.IContextMonitor";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.infidea.contextmon.monitor.IContextMonitor interface,
 * generating a proxy if needed.
 */
public static net.infidea.contextmon.monitor.IContextMonitor asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.infidea.contextmon.monitor.IContextMonitor))) {
return ((net.infidea.contextmon.monitor.IContextMonitor)iin);
}
return new net.infidea.contextmon.monitor.IContextMonitor.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_start:
{
data.enforceInterface(DESCRIPTOR);
this.start();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_getContexts:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
android.content.ContentValues[] _result = this.getContexts(_arg0, _arg1);
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
case TRANSACTION_getCurrentContexts:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
android.content.ContentValues[] _result = this.getCurrentContexts(_arg0);
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.infidea.contextmon.monitor.IContextMonitor
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void start() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_start, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public android.content.ContentValues[] getContexts(int type, int maxNum) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.ContentValues[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(maxNum);
mRemote.transact(Stub.TRANSACTION_getContexts, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(android.content.ContentValues.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.content.ContentValues[] getCurrentContexts(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.ContentValues[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_getCurrentContexts, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(android.content.ContentValues.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_start = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getContexts = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getCurrentContexts = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void start() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public android.content.ContentValues[] getContexts(int type, int maxNum) throws android.os.RemoteException;
public android.content.ContentValues[] getCurrentContexts(int type) throws android.os.RemoteException;
}
