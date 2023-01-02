package ch.epfl.javelo.projection;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class Ch1903 {
    private Ch1903(){}

    /**
     * Public method that determines the coordinate E based on longitudes and latitudes in the WGS84 system
     * @param lon : longitude
     * @param lat : latitude
     * @return the coordinate e from lon and lat in the WGS84 system
     */
    public static double e(double lon, double lat) {
        double lambda1 = ((3600 * Math.toDegrees(lon)) - 26782.5) * Math.pow(10, -4);

        double fi1 = ((3600 * Math.toDegrees(lat)) - 169028.66) * Math.pow(10, -4);

        double e = 2600072.37 + (211455.93 * lambda1)
                - (10938.51 * lambda1 * fi1)
                - (0.36 * lambda1 * Math.pow(fi1, 2))
                - (44.54 * Math.pow(lambda1, 3));

        return e;
    }

    /**
     * Public method that determines the coordinate N based on longitudes and latitudes in the WGS84 system
     * @param lon : longitude
     * @param lat : latitude
     * @return the coordinate n from lon and lat in the WGS84 system
     */
    public static double n(double lon, double lat) {
        double lambda1 = ((3600 * Math.toDegrees(lon)) - 26782.5) * Math.pow(10, -4);

        double fi1 = ((3600 * Math.toDegrees(lat)) - 169028.66) * Math.pow(10, -4);

        double n = 1200147.07 + (308807.95 * fi1)
                + (3745.25 * Math.pow(lambda1, 2))
                + (76.63 * Math.pow(fi1, 2))
                - (194.56 * Math.pow(lambda1, 2) * fi1)
                + (119.79 * Math.pow(fi1, 3));

        return n;
    }

    /**
     * Public method that determines the longitude based on the E and N coordinates
     * @param e : coordinate e
     * @param n : coordinate n
     * @return the longitude in the WGS84 system from the coordinates e and n
     */
    public static double lon(double e, double n) {
        double x = (e - 2600000) * Math.pow(10, -6);

        double y = (n - 1200000) * Math.pow(10, -6);

        double lambda0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * Math.pow(y, 2)
                - 0.0436 * Math.pow(x, 3);

        double lon = lambda0 * 100 / 36;

        return Math.toRadians(lon);
    }

    /**
     * Public method that determines the latitude based on E and N coordinates
     * @param e : coordinate e
     * @param n : coordinate n
     * @return the latitude in the WGS84 system from the coordinates e and n
     */
    public static double lat(double e, double n) {
        double x = (e - 2600000) * Math.pow(10, -6);

        double y = (n - 1200000) * Math.pow(10, -6);

        double fi0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * Math.pow(x, 2)
                - 0.002528 * Math.pow(y, 2)
                - 0.0447 * y * Math.pow(x, 2)
                - 0.0140 * Math.pow(y, 3);

        double lat = fi0 * 100 / 36;

        return Math.toRadians(lat);
    }

}