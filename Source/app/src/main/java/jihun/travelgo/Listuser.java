package jihun.travelgo;

/**
 * Created by com on 2016-11-27.
 */

public class Listuser {


    private String[] mData;

    public Listuser(String[] data ){


        mData = data;
    }

    public Listuser(String imgurl, String txt1, String txt2, String txt3, String txt4, String txt5){

        mData = new String[6];
        mData[0] = imgurl;
        mData[1] = txt1;
        mData[2] = txt2;
        mData[3] = txt3;
        mData[4] = txt4;
        mData[5] = txt5;

    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }

}
