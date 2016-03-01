package applications.magnet.Routing;


public abstract class Location  implements Cloneable{

	   /**
     * Return X-coordinate of location.
     * 
     * @return x-coordinate of location
     */
    public abstract float getX();

    /**
     * Return Y-coordinate of location.
     * 
     * @return y-coordinate of location
     */
    public abstract float getY();

    /**
     * Return height of location.
     * 
     * @return height of location
     */
    public abstract float getHeight();

    /**
     * Compute distance between two locations.
     * 
     * @param l
     *            second location
     * @return distance between current and second location
     */
    public abstract float distance(Location l);

    /**
     * Compute distance squared between two locations.
     * 
     * @param l
     *            second location
     * @return distance squared between current and second location
     */
    public abstract float distanceSqr(Location l);

    /**
     * Divide (scale) vector from current to second location into a number of
     * equal step (displacement) vectors.
     * 
     * @param l
     *            destination location
     * @param steps
     *            number of steps to destination
     * @return step displacement vector
     */
    public abstract Location step(Location l, int steps);

    /**
     * Determine whether point is inside bounds.
     * 
     * @param bounds
     *            bounds to test again
     * @return whether point within bounds
     */
    public abstract boolean inside(Location bounds);  // fix this if needed

    /**
     * Determine whether points is inside bounds.
     * 
     * @param min
     *            lower left bound
     * @param max
     *            upper right bound
     * @return whether point within bounds
     */
   // public abstract boolean inside(Location min, Location max); // fix this if needed

    /**
     * Vector addition of locations. Returns a Location object whose value is
     * (this + l).
     * 
     * @param l
     *            second location / displacement
     */
    public abstract Location add(Location l);

    /**
     * The size of the dataset in bytes.
     * 
     * @return the size of the dataset in bytes
     */
    public abstract int size();

    /**
     * Return clone of location object.
     * 
     * @return clone of location object
     */
    public Location getClone() {
        try {
            return (Location) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse string into 2d or 3d Location object.
     * 
     * @param s
     *            string to be parsed: format = x,y[,h]
     * @return string parsed into Location object
     */
    public static Location parse(String s) {
        String[] data = s.split("x|,");
        if (data.length == 2) {
            return new Location.Location2D(Float.parseFloat(data[0]), Float.parseFloat(data[1]));
        } else if (data.length == 3) {
            return new Location.Location3D(Float.parseFloat(data[0]), Float.parseFloat(data[1]),
                    Float.parseFloat(data[2]));
        } else
            throw new IllegalArgumentException("invalid format, expected x,y[,h]");
    }

    private volatile int hashCode = 0;

    /**
     * Compute hash code
     * 
     * @return hash code
     */
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = result * 31 + Float.floatToIntBits(getX());
            result = result * 31 + Float.floatToIntBits(getY());
            result = result * 31 + Float.floatToIntBits(getHeight());
            hashCode = result;
        }
        return hashCode;
    }

    /**
     * Return whether this is equal to another object.
     * 
     * @param o
     *            object to test equality against
     * @return whether object provided is equal
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Location))
            return false;
        Location pn = (Location) o;
        return (pn.getX() == this.getX()) && (pn.getY() == this.getY()) && (pn.getHeight() == this.getHeight());
    }

    // ////////////////////////////////////////////////
    // 2d
    //

    /**
     * A planar location implementation.
     * 
     * @author Rimon Barr &lt;barr+jist@cs.cornell.edu&gt;
     * @since SWANS1.0
     */

    public static final class Location2D extends Location
    {
        /** co-ordinates. */
        private final float x, y, height;
        private final int   SIZE;

        /**
         * Create two-dimensional coordinate at default height.
         * 
         * @param x
         *            x-coordinate
         * @param y
         *            y-coordinate
         */
        public Location2D(float x, float y) {
            this(x, y, (float) Constants.HEIGHT_DEFAULT);
        }

        /**
         * Create two-dimensional coordinate.
         * 
         * @param x
         *            x-coordinate
         * @param y
         *            y-coordinate
         * @param height
         *            z-coordinate
         */
        public Location2D(float x, float y, float height) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.SIZE = 4 * 2; // two floats
        }

        /** {@inheritDoc} */
        public float distanceSqr(Location l) {
            Location2D l2d = (Location2D) l;
            float dx = x - l2d.x, dy = y - l2d.y;
            return dx * dx + dy * dy;
        }

        /** {@inheritDoc} */
        public float distance(Location l) {
            return (float) StrictMath.sqrt(distanceSqr(l));
        }

        /** {@inheritDoc} */
        public Location step(Location l, int steps) {
            Location2D l2d = (Location2D) l;
            return new Location.Location2D((l2d.x - x) / steps, (l2d.y - y) / steps);
        }

        /** {@inheritDoc} */
        public float getX() {
            return x;
        }

        /** {@inheritDoc} */
        public float getY() {
            return y;
        }

        /** {@inheritDoc} */
        public float getHeight() {
            return height;
        }

        /** {@inheritDoc} */
        public boolean inside(Location bounds) {
            Location2D l2d = (Location2D) bounds;
            return x <= l2d.x && y <= l2d.y && x >= 0 && y >= 0;
        }

//        /** {@inheritDoc} */
//        public boolean inside(Location min, Location max) {
//            Location2D min2d = (Location2D) min, max2d = (Location2D) max;
//            if (Main.ASSERT)
//                Util.assertion(min2d.x <= max2d.x && min2d.y <= max2d.y);
//            return x <= max2d.x && y <= max2d.y && x >= min2d.x && y >= min2d.y;
//        } //fix it if needed

        /** {@inheritDoc} */
        public Location add(Location l) {
            Location2D l2d = (Location2D) l;
            return new Location2D(l2d.x + x, l2d.y + y);
        }

        /** {@inheritDoc} */
        public int size() {
            return SIZE;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        /*
         * (non-Javadoc)
         * 
         * @see jist.swans.misc.Location#bearing(jist.swans.misc.Location)
         */
        public Location bearing(Location nextPoint) {
            Location2D l2d = (Location2D) nextPoint;
            float dx = l2d.x - x;
            float dy = l2d.y - y;
            float dist = this.distance(nextPoint);
            return new Location.Location2D(dx / dist, dy / dist);
        }

    } // class: Location2D

    // ////////////////////////////////////////////////
    // 3d
    //

    /**
     * A three-dimensional location implementation.
     * 
     * @author Rimon Barr &lt;barr+jist@cs.cornell.edu&gt;
     * @since SWANS1.0
     */

    public static final class Location3D extends Location
    {
        /** co-ordinates. */
        private final float x, y, z;
        private final int   SIZE;

        /**
         * Create three-dimensional coordinate.
         * 
         * @param x
         *            x-coordinate
         * @param y
         *            y-coordinate
         * @param z
         *            z-coordinate
         */
        public Location3D(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.SIZE = 4 * 3; // three floats
        }

        /** {@inheritDoc} */
        public float distanceSqr(Location l) {
            Location3D l3d = (Location3D) l;
            float dx = x - l3d.x, dy = y - l3d.y, dz = z - l3d.z;
            return dx * dx + dy * dy + dz * dz;
        }

        /** {@inheritDoc} */
        public float distance(Location l) {
            return (float) StrictMath.sqrt(distanceSqr(l));
        }

        /** {@inheritDoc} */
        public Location step(Location l, int steps) {
            Location3D l3d = (Location3D) l;
            return new Location.Location3D((l3d.x - x) / steps, (l3d.y - y) / steps, (l3d.z - z) / steps);
        }

        /** {@inheritDoc} */
        public float getX() {
            return x;
        }

        /** {@inheritDoc} */
        public float getY() {
            return y;
        }

        /** {@inheritDoc} */
        public float getHeight() {
            return z;
        }

        /** {@inheritDoc} */
        public boolean inside(Location bounds) {
            Location3D l3d = (Location3D) bounds;
            return x <= l3d.x && y <= l3d.y && z <= l3d.z && x >= 0 && y >= 0 && z >= 0;
        }

//        /** {@inheritDoc} */
//        public boolean inside(Location min, Location max) {
//            Location3D min3d = (Location3D) min, max3d = (Location3D) max;
//            if (Main.ASSERT)
//                Util.assertion(min3d.x <= max3d.x && min3d.y <= max3d.y && min3d.z <= max3d.z);
//            return x <= max3d.x && y <= max3d.y && z <= max3d.z && x >= min3d.x && y >= min3d.y && z >= min3d.z;
//        }

        /** {@inheritDoc} */
        public Location add(Location l) {
            Location3D l3d = (Location3D) l;
            return new Location3D(l3d.x + x, l3d.y + y, l3d.z + z);
        }

        /** {@inheritDoc} */
        public int size() {
            return SIZE;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }

        /*
         * (non-Javadoc)
         * 
         * @see jist.swans.misc.Location#bearing(jist.swans.misc.Location)
         */
        public Location bearing(Location nextPoint) {
            Location3D l3d = (Location3D) nextPoint;
            float dx = l3d.x - x;
            float dy = l3d.y - y;
            float dz = l3d.z - z;
            float dist = this.distance(nextPoint);
            return new Location.Location3D(dx / dist, dy / dist, z / dist);
        }

    } // class Location3D

    /**
     * Returns the normalized direction from the current point to the next
     * point.
     * 
     * @param nextPoint
     *            the destination location
     * @return the normalized bearing for this segment
     */
    public abstract Location bearing(Location nextPoint);

    
}
