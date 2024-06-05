package me.SyncWise.Android.Modules.Sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLSocket;

class NoSSLv3SSLSocket extends DelegateSSLSocket {

    private NoSSLv3SSLSocket(SSLSocket delegate) {
        super(delegate);

    }

    @Override
    public void setEnabledProtocols(String[] protocols) {
        if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {

            List<String> enabledProtocols = new ArrayList<String>(Arrays.asList(delegate.getEnabledProtocols()));
            if (enabledProtocols.size() > 1) {
                enabledProtocols.remove("SSLv3");
                System.out.println("Removed SSLv3 from enabled protocols");
            } else {
                System.out.println("SSL stuck with protocol available for " + String.valueOf(enabledProtocols));
            }
            protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
        }

        super.setEnabledProtocols(protocols);
    }
}

