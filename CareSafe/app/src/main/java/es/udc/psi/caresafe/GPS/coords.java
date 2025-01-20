package es.udc.psi.caresafe.GPS;

public class coords {
    double altitude, longitude;

    @Override
    public String toString() {
        return "altitude = " + altitude +
                ", longitude = " + longitude;
    }

    public coords(double longitude, double altitude) {
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
