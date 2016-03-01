package applications.magnet.Routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class NetMessage implements Message, Cloneable{

    // ////////////////////////////////////////////////
    // IPv4 packet: (size = 20 + 4 * options + body)
    // version size: 4 bits
    // header length size: 4 bits
    // type of service (tos) size: 1
    // - priority size: 3 bits
    // - delay bit size: 1 bit
    // - throughput bit size: 1 bit
    // - reliability bit size: 1 bit
    // - reserved size: 2 bits
    // total length size: 2 (measured in 64 bit chunks)
    // id size: 2
    // control flags size: 3 bits
    // - reserved (=0) size: 1 bit
    // - unfragmentable size: 1 bit
    // - more fragments size: 1 bit
    // fragment offset size: 13 bits
    // ttl size: 1
    // protocol size: 1
    // header chksum size: 2
    // src size: 4
    // dst size: variable
    // options: size: 4 * number
    // packet payload: size: variable
    //

    /**
     * IPv4 network packet.
     */
    public static class Ip extends NetMessage
    {

        /** Fixed IP packet size. */
        public static final int BASE_SIZE = 16;

        // ////////////////////////////////////////////////
        // message contents
        //

        /** immutable bit. */
        private boolean         frozen;
        /** ip packet source address. */
        private NetAddress      src;
        /** ip packet destination address. */
        private NetAddress      dst;
        /** ip packet payload. */
        private Message         payload;


		/** ip packet priority level. */
        private byte            priority;
        /** ip packet protocol, such as TCP, UDP, etc. */
        private short           protocol;
        /** ip packet time-to-live. */
        private byte            ttl;
        /** ip packet identification. */
        private short           id;
        /** ip packet fragment offset. */
        private short           fragOffset;
        private MacAddress		lastHopMacAddress;
        // options
        // private IpOption[] option;
        // private ArrayList options; // List alone doesnt provide the Cloneable
        // Interface
        private HashMap         options;       // Map alone doesnt provide the
                                                // Cloneable
                                                // Interface

        /** Next identification number to use. */
        private static short    nextId    = 0;

        /**
         * Create new IPv4 packet.
         * 
         * @param payload
         *            packet payload
         * @param src
         *            packet source address
         * @param dst
         *            packet destination address
         * @param protocol
         *            packet protocol
         * @param priority
         *            packet priority
         * @param ttl
         *            packet time-to-live
         * @param id
         *            packet identification
         * @param fragOffset
         *            packet fragmentation offset
         */
        public Ip(Message payload, NetAddress src, NetAddress dst, short protocol, byte priority, byte ttl, short id,
                short fragOffset) {
            if (payload == null)
                throw new NullPointerException();
            this.frozen = false;
            this.payload = payload;
            this.src = src;
            this.dst = dst;
            this.protocol = protocol;
            this.priority = priority;
            this.ttl = ttl;
            this.id = id;
            this.fragOffset = fragOffset;
            // options = new ArrayList();
            options = new HashMap();
        }

        /**
         * Create new IPv4 packet with default id.
         * 
         * @param payload
         *            packet payload
         * @param src
         *            packet source address
         * @param dst
         *            packet destination address
         * @param protocol
         *            packet protocol
         * @param priority
         *            packet priority
         * @param ttl
         *            packet time-to-live
         */
        public Ip(Message payload, NetAddress src, NetAddress dst, short protocol, byte priority, byte ttl) {
            this(payload, src, dst, protocol, priority, ttl, nextId++, (short) 0);
        }

        public void setPayload(Message payload) {
			this.payload = payload;
		}
        
        /**
         * Render packet immutable.
         * 
         * @return immutable packet, possibly intern()ed
         */
        public Ip freeze() {
            // todo: could perform an intern/hashCons here
            this.frozen = true;
            return this;
        }

        /**
         * Whether packet is immutable.
         * 
         * @return whether packet is immutable
         */
        public boolean isFrozen() {
            return frozen;
        }

        /**
         * Make a semi shallow copy of packet, usually in order to modify it.
         * 
         * @return mutable copy of packet.
         */
        public Ip copy() {
            NetMessage.Ip ip2 = new Ip(payload, src, dst, protocol, priority, ttl, id, fragOffset);
            ip2.options = (HashMap) this.options.clone();
            // ip2.options = (ArrayList)this.options.clone();
            return ip2;
        }

        // ////////////////////////////////////////////////
        // accessors
        //

        /**
         * Return packet source.
         * 
         * @return packet source
         */
        public NetAddress getSrc() {
            return src;
        }

        /**
         * Return packet destination.
         * 
         * @return packet destination
         */
        public NetAddress getDst() {
            return dst;
        }

        /**
         * Return packet payload.
         * 
         * @return packet payload
         */
        public Message getPayload() {
            return payload;
        }

        /**
         * Return packet priority.
         * 
         * @return packet priority
         */
        public byte getPriority() {
            return priority;
        }

        /**
         * Return packet protocol.
         * 
         * @return packet protocol
         */
        public short getProtocol() {
            return protocol;
        }

        /**
         * Return packet identification.
         * 
         * @return packet identification
         */
        public short getId() {
            return id;
        }

        /**
         * Return packet fragmentation offset.
         * 
         * @return packet fragmentation offset
         */
        public short getFragOffset() {
            return fragOffset;
        }

        // ////////////////////////////////////////////////
        // TTL
        //

        /**
         * Return packet time-to-live.
         * 
         * @return time-to-live
         */
        public byte getTTL() {
            return ttl;
        }

        /**
         * Create indentical packet with decremented TTL.
         */
        public void decTTL() {
            if (frozen)
                throw new IllegalStateException();
            ttl--;
        }

        // ////////////////////////////////////////////////
        // IpOption
        //

        /**
         * Return the optional (thus can be null) IpOption object. Note that
         * IpOption objects must be immutable to avoid conflicts with the
         * possibly frozen state of the packet
         * 
         * @return IpOption (can be null)
         */
        public Map getOptions() {
            // return isFrozen() ? Collections.unmodifiableList(options) :
            // options;
            return isFrozen() ? Collections.unmodifiableMap(options) : options;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "ip(src=" + src + " dst=" + dst + " size=" + getSize() + " prot=" + protocol + " ttl=" + ttl
                    + " id=" + id + " option=" + options + " data=" + payload + ")";
        }

        // ////////////////////////////////////////////////
        // message interface
        //

        /** {@inheritDoc} */
        public int getSize() {
            int size = payload.getSize();
            if (size == Constants.ZERO_WIRE_SIZE) {
                return Constants.ZERO_WIRE_SIZE;
            }
            Iterator it = options.values().iterator();
            while (it.hasNext()) {
                size += ((IpOption) it.next()).getSize();
            }
            return BASE_SIZE + size + dst.getSize();
        }

        /** {@inheritDoc} */
        public void getBytes(byte[] b, int offset) {
            throw new RuntimeException("not implemented");
        }

		public MacAddress getLastHopMacAddress() {
			return lastHopMacAddress;
		}

		public void setLastHopMacAddress(MacAddress lastHopMacAddress) {
			this.lastHopMacAddress = lastHopMacAddress;
		}

    } // class: Ip

    /**
     * A generic IP packet option.
     */
    public abstract static class IpOption implements Message
    {
        /**
         * Return option type field.
         * 
         * @return option type field
         */
        public abstract byte getType();

        /**
         * Return option length (in bytes/octets).
         * 
         * @return option length (in bytes/octets)
         */
        public abstract int getSize();

    } // class: IpOption

    /**
     * An IP packet source route option.
     */
    public static class IpOptionSourceRoute extends IpOption
    {
        /** option type constant: source route. */
        public static final byte   TYPE = (byte) 137;

        /** source route. */
        private final NetAddress[] route;
        /** source route pointer: index into route. */
        private final int          ptr;

        /**
         * Create new source route option.
         * 
         * @param route
         *            source route
         */
        public IpOptionSourceRoute(NetAddress[] route) {
            this(route, (byte) 0);
        }

        /**
         * Create new source route option.
         * 
         * @param route
         *            source route
         * @param ptr
         *            source route pointer
         */
        public IpOptionSourceRoute(NetAddress[] route, int ptr) {
            this.route = route;
            this.ptr = ptr;
        }

        /**
         * Return source route.
         * 
         * @return source route (do not modify)
         */
        public NetAddress[] getRoute() {
            return route;
        }

        /**
         * Return source route pointer: index into route.
         * 
         * @return source route pointer: index into route
         */
        public int getPtr() {
            return ptr;
        }

        /** {@inheritDoc} */
        public byte getType() {
            return TYPE;
        }

        /** {@inheritDoc} */
        public int getSize() {
            return (byte) (route.length * 4 + 3);
        }

        /** {@inheritDoc} */
        public void getBytes(byte[] msg, int offset) {
            throw new RuntimeException("not implemented");
        }

        /** {@inheritDoc} */
        public String toString() {
            return ptr + ":[" + Util.stringJoin(route, ",") + "]";
        }

    } // class: IpOptionSourceRoute

    /**
     * The geographic location of an IP packets most recent hop.
     */
    public static class IpOptionHopLoc extends IpOption
    {

        private static final byte TYPE = (byte) 138;
        private Location          lastHop;

        public IpOptionHopLoc(Location lastLoc) {
            lastHop = lastLoc;
        }

        public Location getLoc() {
            return lastHop;
        }

        /** {@inheritDoc} */
        public byte getType() {
            return TYPE;
        }

        /** {@inheritDoc} */
        public int getSize() {
            /** TODO vans: give Location objects a getSize method */
            // return lastHop.getSize();
            return 2;
        }

        /** {@inheritDoc} */
        public void getBytes(byte[] msg, int offset) {
            throw new RuntimeException("not implemented");
        }

        /** {@inheritDoc} */
        public String toString() {
            return "hopLoc:" + lastHop.toString();
        }

    } // class: hopLoc

    public static class IpOptionRecordRoute extends IpOption
    {

        private static final byte TYPE          = (byte) 7;
        private List              recordedRoute = new ArrayList();

        public IpOptionRecordRoute() {
        }

        public void addHop(NetAddress na) {
            recordedRoute.add(na);
        }

        /** {@inheritDoc} */
        public byte getType() {
            return TYPE;
        }

        /** {@inheritDoc} */
        public int getSize() {
            return recordedRoute.size() * 4;
        }

        /** {@inheritDoc} */
        public void getBytes(byte[] msg, int offset) {
            throw new RuntimeException("not implemented");
        }

        /** {@inheritDoc} */
        public String toString() {
            return "IpOptionRecordRoute:" + recordedRoute.toString();
        }

    } // class: IpOptionBeacon

}
