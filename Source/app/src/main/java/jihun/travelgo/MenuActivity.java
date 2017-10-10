package jihun.travelgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {

    private BackPressCloseHandler backPressCloseHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // xml파일에 있는 Button을 id값으로 불러와서 사용 ,
        Button bt1 = (Button) findViewById(R.id.btn_sights);
        Button bt2 = (Button) findViewById(R.id.btn_restaurant);
        Button bt3 = (Button) findViewById(R.id.btn_account);
        Button bt4 = (Button) findViewById(R.id.btn_ranking);


        // 버튼 클릭 이벤트 처리
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    // 버튼을 눌렀을 때 실행되는 메소드(명시적 인텐트)
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        String id = intent.getStringExtra("userid");

        // TODO Auto-generated method stub
        // 클릭 될 버튼1개의 id값을 스위치구문으로 분기
        switch (v.getId()) {
            // 첫번쨰버튼 눌렷을 때
            case R.id.btn_sights:    // 명소
                Intent i1 = new Intent(this, SightsActivity.class);
                startActivity(i1);
                break;
            case R.id.btn_restaurant:    // 맛집
                Intent i2 = new Intent(this, RestaurantActivity.class);
                startActivity(i2);
                break;
            case R.id.btn_account:    // 계정
                Intent i3 = new Intent(this, AccountActivity.class);
                i3.putExtra("userid", id);

                startActivity(i3);
                break;
            case R.id.btn_ranking:    // 랭킹
                Intent i4 = new Intent(this, RankingActivity.class);
                i4.putExtra("userid", id);
                startActivity(i4);
                break;

        }
    }

}
