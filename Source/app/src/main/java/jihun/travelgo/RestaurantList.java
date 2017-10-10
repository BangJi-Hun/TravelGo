package jihun.travelgo;

/**
 * Created by JIHUN on 2016-11-29.
 */

public class RestaurantList {
    String[] name = {"학생 식당", "맘스터치", "한솥 도시락", "주식 창고", "CU 편의점", "토담"};
    double[] lat = {36.145105, 36.140944, 36.140422, 36.139758, 36.139758, 36.139188};
    double[] lo = {128.394034, 128.395965, 128.395793, 128.396885, 128.396885, 128.395342};

    public String getName(int i) {
        return name[i];
    }

    public double getLat(int i) {
        return lat[i];
    }

    public double getLo(int i) {
        return lo[i];
    }

    public int findRestArray(String s) {
        for (int i = 0; i < 6; i++) {
            if (s.equals(getName(i))) {
                return i;
            }
        }
        return -1;
    }
}