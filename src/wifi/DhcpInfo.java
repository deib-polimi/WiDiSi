    /*
     * Copyright (C) 2008 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    package wifi;

    /**
     * A simple object for retrieving the results of a DHCP request.
     */
    public class DhcpInfo {
        public int ipAddress;
        public int gateway;
        public int netmask;
        public int dns1;
        public int dns2;
        public int serverAddress;
        public int leaseDuration;
        public DhcpInfo() {
            super();
        }
        /** copy constructor {@hide} */
        public DhcpInfo(DhcpInfo source) {
            if (source != null) {
                ipAddress = source.ipAddress;
                gateway = source.gateway;
                netmask = source.netmask;
                dns1 = source.dns1;
                dns2 = source.dns2;
                serverAddress = source.serverAddress;
                leaseDuration = source.leaseDuration;
            }
        }
        public String toString() {
            StringBuffer str = new StringBuffer();
            str.append("ipaddr " +  ipAddress);
            str.append(" gateway " + gateway);
            str.append(" netmask " + netmask);
            str.append(" dns1 " + dns1);
            str.append(" dns2 " + dns2);
            str.append(" DHCP server"  + serverAddress);
            str.append(" lease ").append(leaseDuration).append(" seconds");
            return str.toString();
        }
//        private static void putAddress(StringBuffer buf, int addr) {
//            buf.append(NetworkUtils.intToInetAddress(addr).getHostAddress());
//        }
    }

