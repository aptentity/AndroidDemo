package com.borg.androidemo.devices.api;

/**连接的回调接口
 * Created by yiping.cyp on 2015/9/17.
 */
public interface DeviceConnectListener {

    public static final int AUTH_CONNECTED = 5;
    public static final int AUTH_BINDED = 6;
    public static final int AUTH_BINDBYOTHER = 7;

    /**　开始连接的回调通知
     * @param conn 连接接口
     */
    public void onConnecting(final DeviceConnection conn);

    /**断开连接的回调通知
     * @param conn  连接接口
     */
    public void onDisconnected(final DeviceConnection conn);

    /**连接成功的回调通知
     * @param conn   连接接口
     * @param authCode 连接状态通知
     */
    public void onConnected(final DeviceConnection conn, int authCode);
}
