package com.example.accessoryprovidersample;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import co.poynt.os.model.CashDrawerStatus;
import co.poynt.os.services.v1.IPoyntCashDrawerService;
import co.poynt.os.services.v1.IPoyntCashDrawerServiceListener;

public class MyCashDrawerService extends Service {
    private static final String TAG = MyCashDrawerService.class.getSimpleName();

    public static final String ACTION_OPEN_DRAWER = "poynt.intent.action.CASH_DRAWER";
    private String accessoryModelName;
    private String accessoryCategory;
    private UsbDevice attachedUsbDevice;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accessoryModelName = intent.getStringExtra(CustomAccessoryReceiver.EXTRA_MODEL_NAME);
        accessoryCategory = intent.getStringExtra(CustomAccessoryReceiver.EXTRA_MODEL_CATEGORY);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            attachedUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            onCashDrawerAttached(attachedUsbDevice,
                    accessoryModelName, accessoryCategory);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            onCashDrawerDetached(device, accessoryModelName,
                    accessoryCategory);
            if (device.getProductId() == attachedUsbDevice.getProductId()
                    && device.getVendorId() == attachedUsbDevice.getVendorId()) {
                attachedUsbDevice = null;
            }
        } else if (ACTION_OPEN_DRAWER.equals(intent.getAction())) {
            open();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Implements interface to provide draw open command to the client via service api.
     */
    private final IPoyntCashDrawerService.Stub mBinder = new IPoyntCashDrawerService.Stub() {
        @Override
        public void openDrawer(String s, IPoyntCashDrawerServiceListener iPoyntCashDrawerServiceListener) throws RemoteException {
            Log.d(TAG,"openDrawer()");
            Handler h = new Handler(getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "Sending open command to cashdrawer");
                    open();
                }
            });
        }

        @Override
        public void getDrawerStatus(final String requestId, final IPoyntCashDrawerServiceListener iPoyntCashDrawerServiceListener)
                throws RemoteException {
            Log.d(TAG, "getDrawerStatus " +  requestId);
            Handler h = new Handler(getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "Sending open command to cashdrawer");
                    getStatus(requestId, iPoyntCashDrawerServiceListener);
                }
            });
        }

    };

    public void open(){
        Log.d(TAG, "open: called!!!");
        //TODO Send the command to the cash drawer to open it

    }

    private void onCashDrawerAttached(UsbDevice device, String modelName, String category) {
        // SET UP
    }
    private void onCashDrawerDetached(UsbDevice device, String modelName, String category) {
        // CLEAN UP
    }

    public void getStatus(String requestId, IPoyntCashDrawerServiceListener listener) {
        CashDrawerStatus drawerStatus = null;
        boolean  connected = false;

        // TODO add logic to check if cashdrawer is connected

        if (connected) {
            drawerStatus = new CashDrawerStatus(
                    CashDrawerStatus.Code.CONNECTED, "Cash Drawer connected");
        }else {
            // TODO  add logic to determine if cashdrawer is DISCONNECTED, UNAVAILABLE OR IN ERROR STATE
//            drawerStatus = new CashDrawerStatus(
//                    CashDrawerStatus.Code.DISCONNECTED, "Cash Drawer disconnected");
//            drawerStatus = new CashDrawerStatus(
//                    CashDrawerStatus.Code.ERROR, "Cash Drawer connection failed");
//            drawerStatus = new CashDrawerStatus(
//                    CashDrawerStatus.Code.UNAVAILABLE, "Cash Drawer is unavailable");
        }
        if (listener != null && drawerStatus != null) {
            try {
                Log.d(TAG, " cashDrawer getStatus : " + drawerStatus.getCode());
                listener.onResponse(drawerStatus, requestId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
