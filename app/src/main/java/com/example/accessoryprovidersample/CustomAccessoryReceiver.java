package com.example.accessoryprovidersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class CustomAccessoryReceiver extends BroadcastReceiver {
    private static final String TAG = CustomAccessoryReceiver.class.getSimpleName();
    public static final String ACCESSORY_ATTACHED = "co.poynt.accessory.manager.action.ACCESSORY_ATTACHED";
    public static final String ACCESSORY_DETACHED = "co.poynt.accessory.manager.action.ACCESSORY_DETACHED";
    public static final String EXTRA_USB_DEVICE = "co.poynt.accessory.manager.extra.USB_DEVICE";
    public static final String EXTRA_MODEL_NAME = "co.poynt.accessory.manager.extra.MODEL_NAME";
    public static final String EXTRA_MODEL_CATEGORY = "co.poynt.accessory.manager.extra.MODEL_CATEGORY";
    public static final String EXTRA_MODEL_VERSION = "co.poynt.accessory.manager.extra.MODEL_VERSION";
    public static final String CATEGORY_CASH_DRAWER = "Cash Drawer";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: From onReceive ");
        if (ACCESSORY_ATTACHED.equals(intent.getAction())) {
            UsbDevice device = intent.getParcelableExtra(EXTRA_USB_DEVICE);
            String category = intent.getStringExtra(EXTRA_MODEL_CATEGORY);
            if (device != null) {
                Intent service_intent = null;
                if (CATEGORY_CASH_DRAWER.equals(category)) {
                    service_intent = new Intent(context, MyCashDrawerService.class);
                } // other device type if supported.
                if (service_intent != null){
                    service_intent.setAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    service_intent.putExtra(EXTRA_MODEL_NAME, intent.getStringExtra(EXTRA_MODEL_NAME));
                    service_intent.putExtra(EXTRA_MODEL_CATEGORY, intent.getStringExtra(EXTRA_MODEL_CATEGORY));
                    service_intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                    context.startService(service_intent);
                }
            }

        } else if (ACCESSORY_DETACHED.equals(intent.getAction())) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(EXTRA_USB_DEVICE);
            if (device != null) {
                String category = intent.getStringExtra(EXTRA_MODEL_CATEGORY);
                Intent service_intent = null;
                if (CATEGORY_CASH_DRAWER.equals(category)) {
                    service_intent = new Intent(context, MyCashDrawerService.class);
                }// other device type if supported.
                if (service_intent != null){
                    service_intent.setAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    service_intent.putExtra(EXTRA_MODEL_NAME, intent.getStringExtra(EXTRA_MODEL_NAME));
                    service_intent.putExtra(EXTRA_MODEL_CATEGORY, intent.getStringExtra(EXTRA_MODEL_CATEGORY));
                    service_intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                    context.startService(service_intent);
                }
            }
        }
    }
}
