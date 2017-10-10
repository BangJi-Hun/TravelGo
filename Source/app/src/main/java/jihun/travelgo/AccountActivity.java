package jihun.travelgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class AccountActivity extends Activity {

    TextView tv_nickname;
    TextView tv_age;
    TextView tv_sex;
    TextView tv_address;
    TextView tv_con;
    String iid;


    phpDown task;
    ArrayList<Listuser> list_user = new ArrayList<Listuser>();
    Listuser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Intent intent = getIntent();
        iid = intent.getStringExtra("userid");
        final DBConnector idd = new DBConnector();

        tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_con = (TextView) findViewById(R.id.tv_con);
        Button btn_logout = (Button) findViewById(R.id.button2);
        Button btn_update = (Button) findViewById(R.id.button3);
        Button btn_unlink = (Button) findViewById(R.id.button4);


        task = new phpDown();
        task.execute("http://192.168.218.215/trevelgo/newfile.php");


        btn_logout.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              Toast mToast = Toast.makeText(AccountActivity.this, "로그아웃 성공!", Toast.LENGTH_SHORT);
                                              mToast.show();
                                              UserManagement.requestLogout(new LogoutResponseCallback() {


                                                  @Override

                                                  public void onCompleteLogout() {
                                                      //로그아웃 성공 후 하고싶은 내용 코딩 ~


                                                      Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                                      startActivity(intent);
                                                      finish();
                                                  }
                                              });
                                          }
                                      }
        );
        btn_update.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {


                                              Intent intent = new Intent(AccountActivity.this, UpdateActivity.class);


                                              intent.putExtra("userid", iid);
                                              startActivity(intent);
                                              finish();

                                          }
                                      }
        );

        btn_unlink.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
                                              new AlertDialog.Builder(AccountActivity.this)
                                                      .setMessage(appendMessage)
                                                      .setPositiveButton(getString(R.string.com_kakao_ok_button),
                                                              new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                                                          @Override

                                                                          public void onFailure(ErrorResult errorResult) {
                                                                              Logger.e(errorResult.toString());
                                                                          }

                                                                          @Override
                                                                          public void onSessionClosed(ErrorResult errorResult) {
                                                                              Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                                                              startActivity(intent);
                                                                              finish();
                                                                          }

                                                                          @Override
                                                                          public void onNotSignedUp() {
                                                                              Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                                                              startActivity(intent);
                                                                              finish();
                                                                          }

                                                                          @Override
                                                                          public void onSuccess(Long userId) {
                                                                              idd.deleteid(iid);
                                                                              Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                                                              startActivity(intent);
                                                                              finish();
                                                                          }
                                                                      });
                                                                      dialog.dismiss();
                                                                  }
                                                              })
                                                      .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                                                              new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      dialog.dismiss();
                                                                  }
                                                              }).show();


                                          }
                                      }
        );
    }

    public class phpDown extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {
            String id;
            String nickname;
            String age;
            String sex;
            String address;
            String conquest;
            int user_record_count = 0;
            try {

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                user_record_count = ja.length();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    id = jo.getString("id");
                    nickname = jo.getString("nickname");
                    age = jo.getString("age");
                    sex = jo.getString("sex");
                    address = jo.getString("address");
                    conquest = jo.getString("conquest");
                    list_user.add(new Listuser(id, nickname, age, sex, address, conquest));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < user_record_count; i++) {
                if (list_user.get(i).getData(0).equals(iid)) {
                    tv_nickname.setText(list_user.get(i).getData(1));
                    tv_age.setText(list_user.get(i).getData(2));
                    tv_sex.setText(list_user.get(i).getData(3));
                    tv_address.setText(list_user.get(i).getData(4));
                    if (list_user.get(i).getData(5).equals("6")) {
                        tv_con.setText("구미 그자체");
                    }
                    if (list_user.get(i).getData(5).equals("5")) {
                        tv_con.setText("구미의 신");
                    }
                    if (list_user.get(i).getData(5).equals("4")) {
                        tv_con.setText("구미정복자");
                    }
                    if (list_user.get(i).getData(5).equals("3")) {
                        tv_con.setText("구미고수");
                    }
                    if (list_user.get(i).getData(5).equals("2")) {
                        tv_con.setText("구미중수");
                    }
                    if (list_user.get(i).getData(5).equals("1")) {
                        tv_con.setText("구미초보");
                    }
                    if (list_user.get(i).getData(5).equals("0")) {
                        tv_con.setText("마이구미");
                    }
                }
            }
        }
    }
}






