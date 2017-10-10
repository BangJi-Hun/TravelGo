package jihun.travelgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.kakao.usermgmt.StringSet.id;
import static jihun.travelgo.R.array.sex;
import static jihun.travelgo.R.id.spinner;
import static jihun.travelgo.R.id.tv_address;
import static jihun.travelgo.R.id.tv_age;
import static jihun.travelgo.R.id.tv_nickname;
import static jihun.travelgo.R.id.tv_sex;

public class UpdateActivity extends Activity {
    DBConnector updateinfo;
    Button btn_update;
    Button btn_back;
    EditText et_nickname;
    EditText et_age;
    TextView tv_sex;
    TextView tv_address;
    phpDown task;
    int index;
    String changed_sex;
    String changed_address;
    ArrayList<Listuser> list_user= new ArrayList<Listuser>();
    String iid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateinfo = new DBConnector();
        Intent intent = getIntent();
        iid = intent.getStringExtra("userid");
        btn_update =(Button) findViewById(R.id.button3);
        btn_back =(Button) findViewById(R.id.button4);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_age = (EditText) findViewById(R.id.et_age);
        tv_sex = (TextView)findViewById(R.id.tv_sex);
        tv_address = (TextView)findViewById(R.id.tv_address);
        task = new phpDown(id);
        task.execute("http://192.168.218.215/trevelgo/newfile.php");

        Spinner sp1 = (Spinner) findViewById(R.id.spinner);
        Spinner sp2 = (Spinner) findViewById(R.id.spinner2);
        sp2.setPrompt("동을 고르세요.");
        sp1.setPrompt("성별을 고르세요.");


        final ArrayAdapter<CharSequence> adsex = ArrayAdapter.createFromResource(this, sex, android.R.layout.simple_spinner_item);
        adsex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adsex);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence sex = adsex.getItem(position);
                String ssex = sex.toString();
                tv_sex.setText(ssex);
                changed_sex = ssex;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<CharSequence> adaddress = ArrayAdapter.createFromResource(this, R.array.address, android.R.layout.simple_spinner_item);
        adaddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adaddress);

        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence address = adaddress.getItem(position);
                String addresss = address.toString();
                tv_address.setText(addresss);
                changed_address = addresss;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_back.setOnClickListener(new View.OnClickListener()
                                      {
                                          public void onClick(View v)
                                          {
                                              Intent intent = new Intent(UpdateActivity.this, AccountActivity.class);
                                              intent.putExtra("userid", iid);
                                              startActivity(intent);
                                              finish();
                                          }
                                      }
        );
        btn_update.setOnClickListener(new View.OnClickListener()
                                    {
                                        public void onClick(View v)
                                        {
                                            updateinfo.updateInfo(list_user.get(index).getData(0), et_nickname.getText().toString(),et_age.getText().toString(),changed_sex, changed_address);
                                            Intent intent = new Intent(UpdateActivity.this, AccountActivity.class);
                                            intent.putExtra("userid", iid);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
        );

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
            String conquest;
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

            for(int i = 0; i < user_record_count; i++)
            {
                if(list_user.get(i).getData(0).equals(iid))
                {
                    index = i;
                    et_nickname.setText(list_user.get(i).getData(1));
                    et_age.setText(list_user.get(i).getData(2));
                    tv_sex.setText(list_user.get(i).getData(3));
                    tv_address.setText(list_user.get(i).getData(4));
                }
            }
        }


    }
}
