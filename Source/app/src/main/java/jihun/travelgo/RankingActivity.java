package jihun.travelgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static jihun.travelgo.R.id.et_age;
import static jihun.travelgo.R.id.et_nickname;
import static jihun.travelgo.R.id.tv_address;

import static jihun.travelgo.R.id.tv_ranking1;
import static jihun.travelgo.R.id.tv_ranking2;
import static jihun.travelgo.R.id.tv_ranking3;
import static jihun.travelgo.R.id.tv_ranking4;
import static jihun.travelgo.R.id.tv_ranking5;
import static jihun.travelgo.R.id.tv_sex;

public class RankingActivity extends Activity {
    String iid;
    phpDown task;
    TextView[] tv_ranking;
    ArrayList<Listuser> list_user= new ArrayList<Listuser>();
    ArrayList<Listuser> list_user1= new ArrayList<Listuser>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Intent intent = getIntent();
        iid = intent.getStringExtra("userid");
        tv_ranking = new TextView[5];
        tv_ranking[0] = (TextView) findViewById(R.id.tv_ranking1);
        tv_ranking[1] = (TextView) findViewById(tv_ranking2);
        tv_ranking[2] = (TextView) findViewById(tv_ranking3);
        tv_ranking[3]= (TextView) findViewById(tv_ranking4);
        tv_ranking[4] = (TextView) findViewById(tv_ranking5);


        task = new phpDown(iid);
        task.execute("http://192.168.218.215/trevelgo/newfile.php");


    }


    public class phpDown extends AsyncTask<String, Integer,String> {

        String id;

        phpDown(String id)
        {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str){
            String id;
            String nickname;
            String age;
            String sex;
            String address;
            String ranking = "";
            String conquest;
            String[][] rank;

            int user_record_count = 0;
            try{

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                user_record_count = ja.length();


                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    id = jo.getString("id");
                    nickname = jo.getString("nickname");
                    age = jo.getString("age");
                    sex = jo.getString("sex");
                    address = jo.getString("address");
                    conquest = jo.getString("conquest");
                    list_user.add(new Listuser(id, nickname, age, sex, address, conquest));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
             rank = new String[user_record_count][2];
            for(int i = 0; i <user_record_count; i++)
            {
                rank[i][0] = Integer.toString(i);
                rank[i][1] = list_user.get(i).getData(5);
            }
           for(int i = 0; i < user_record_count; i++)
            {
                for(int j = i+1; j < user_record_count; j++)
                {
                    if(Integer.parseInt(rank[i][1]) <= Integer.parseInt(rank[j][1]))
                    {
                        String temp_index = rank[i][0];
                        String temp_conquest = rank[i][1];

                        rank[i][0] = rank[j][0];
                        rank[i][1] = rank[j][1];
                        rank[j][0] = temp_index;
                        rank[j][1] = temp_conquest;
                    }
                }
            }


            for(int i = 0; i < 5; i++)
            {

                    ranking = (i+1) + "등           " + list_user.get(Integer.parseInt(rank[i][0])).getData(1) + "           " + list_user.get(Integer.parseInt(rank[i][0])).getData(2)+ "            "  + list_user.get(Integer.parseInt(rank[i][0])).getData(3)+ "              "  + list_user.get(Integer.parseInt(rank[i][0])).getData(5) + "\n";

                   tv_ranking[i].setText(ranking);

                    ranking = "";

            }
        }


    }

}
