package jihun.travelgo;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.kakao.usermgmt.StringSet.id;
import static com.kakao.usermgmt.StringSet.nickname;
import static com.nhn.android.data.g.a;

/**
 * Created by com on 2016-11-23.
 */

public class DBConnector
{
public void insertToDatabase(String id, String nickname){

class InsertData extends AsyncTask<String, Void, String>{   //AsyncTask: 백그라운드 스레드에서 실행되는 비동기 클래스

//            ProgressDialog loading;    //진행상황을 알려줄때 쓰는 클레스

    @Override
    protected void onPreExecute() {  //doInBackground 메소드가 실행되기 전에 실행되는 메소드
        super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
    }

    @Override
    protected void onPostExecute(String s) {  //doInBackground 메소드 후에 실행되는 메소드, 백그라운드 메소드의 반환값을 인자로 받아 그 결과를 화면에 반영
        super.onPostExecute(s);
//                loading.dismiss();
    }

    @Override
    protected String doInBackground(String... params) {  //처리하고 싶은 내용을 작성

        try{
            String id = (String)params[0];
            String nickname = (String)params[1];

            String link="http://192.168.218.215/trevelgo/sign_up.php";  //실행할 php페이지
            String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");  //보낼 데이터
            data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");

            URL url = new URL(link);  //페이지에 연결
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);  //전송허용
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }
}

    InsertData task = new InsertData();  //쓰레드 생성
task.execute(id,nickname);  //쓰레드 시작

        }


    public void deleteid(String id){

        class InsertData extends AsyncTask<String, Void, String>{   //AsyncTask: 백그라운드 스레드에서 실행되는 비동기 클래스

//            ProgressDialog loading;    //진행상황을 알려줄때 쓰는 클레스

            @Override
            protected void onPreExecute() {  //doInBackground 메소드가 실행되기 전에 실행되는 메소드
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {  //doInBackground 메소드 후에 실행되는 메소드, 백그라운드 메소드의 반환값을 인자로 받아 그 결과를 화면에 반영
                super.onPostExecute(s);
//                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {  //처리하고 싶은 내용을 작성

                try{
                    String id = (String)params[0];

                    String link="http://192.168.218.215/trevelgo/drop_out.php";  //실행할 php페이지
                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");  //보낼 데이터

                    URL url = new URL(link);  //페이지에 연결
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);  //전송허용
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();  //쓰레드 생성
        task.execute(id);  //쓰레드 시작

    }

    public void updateInfo(String id, String nickname, String age, String sex, String address){

        class InsertData extends AsyncTask<String, Void, String>{   //AsyncTask: 백그라운드 스레드에서 실행되는 비동기 클래스

//            ProgressDialog loading;    //진행상황을 알려줄때 쓰는 클레스

            @Override
            protected void onPreExecute() {  //doInBackground 메소드가 실행되기 전에 실행되는 메소드
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {  //doInBackground 메소드 후에 실행되는 메소드, 백그라운드 메소드의 반환값을 인자로 받아 그 결과를 화면에 반영
                super.onPostExecute(s);
//                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {  //처리하고 싶은 내용을 작성

                try{
                    String id = (String)params[0];
                    String nickname = (String)params[1];
                    String age = (String)params[2];
                    String sex = (String)params[3];
                    String address = (String)params[4];

                    String link="http://192.168.218.215/trevelgo/update_info.php";  //실행할 php페이지
                    String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");  //보낼 데이터
                    data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");
                    data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
                    data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");


                    URL url = new URL(link);  //페이지에 연결
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);  //전송허용
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();  //쓰레드 생성
        task.execute(id, nickname, age, sex, address);  //쓰레드 시작

    }



}





