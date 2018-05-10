package com.nebulas.io.net.util;


import com.nebulas.io.net.retrofit.NebulasRetrofitService;

/**
 * Created by legend on 2018/5/4.
 */

public class NetConfig {

    private static NET_TYPE currentNetType = NET_TYPE.MAINNET;

    public enum NET_TYPE {
        LOCAL(100),
        MAINNET(1),
        TESTNET(1001);
        private int type;

        NET_TYPE (int i)
        {
            this.type = i;
        }

        public int getType() {
            return type;
        }
    }

    public static NET_TYPE getCurrentNetType() {
        return currentNetType;
    }

    public static void changeNet(NET_TYPE net_type) {
        currentNetType = net_type;
        NebulasRetrofitService.getInstance().changeNet(net_type);
    }

}
