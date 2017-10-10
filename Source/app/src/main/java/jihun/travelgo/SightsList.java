package jihun.travelgo;

/**
 * Created by JIHUN on 2016-11-29.
 */

public class SightsList {
    String[] name = {"금오공대 디지털관", "금오산 명금폭포", "금오랜드", "박정희 대통령 생가", "구미역", "동락공원"};
    double[] lat = {36.145832, 36.102989, 36.113073, 36.088558, 36.128479, 36.098034};
    double[] lo = {128.392539, 128.296935, 128.316206, 128.348731, 128.330718, 128.401340};

    public String getName(int i) {
        return name[i];
    }

    public double getLat(int i) {
        return lat[i];
    }

    public double getLo(int i) {
        return lo[i];
    }

    public int findSightsArray(String s) {
        for (int i = 0; i < 6; i++) {
            if (s.equals(getName(i))) {
                return i;
            }
        }
        return -1;
    }
}
