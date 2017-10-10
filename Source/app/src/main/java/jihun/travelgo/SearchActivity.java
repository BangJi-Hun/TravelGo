package jihun.travelgo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import static jihun.travelgo.R.id.myautocomplete;

public class SearchActivity extends Activity implements TextWatcher {

    // List view
    private ListView lv;
    // Listview Adapter
    ArrayAdapter<String> adapter;
    // Search EditText
    EditText inputSearch;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    private static int checkRes = 0;

    private AutoCompleteTextView autoComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //명소 배열 받기
        Intent intent = getIntent();
        final String[] products = intent.getStringArrayExtra("products");

        //맛집인지 체크
        checkRes = intent.getIntExtra("checkRes", 0);

        //리스트 뷰
        lv = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, products);
        lv.setAdapter(adapter);

        autoComplete = (AutoCompleteTextView) findViewById(myautocomplete);
        autoComplete.addTextChangedListener(this);
        autoComplete.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, products));
        autoComplete.setTextColor(Color.RED);

        inputSearch = (AutoCompleteTextView) findViewById(R.id.myautocomplete);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SearchActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long l) {

                int searchFlag = 1;

                if (checkRes == 1) {
                    Intent intent = new Intent(SearchActivity.this, RestaurantActivity.class);
                    intent.putExtra("searchRes", adapter.getItem(position).toString());
                    intent.putExtra("searchResFlag", searchFlag);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SearchActivity.this, SightsActivity.class);
                    intent.putExtra("searchSights", adapter.getItem(position).toString());
                    intent.putExtra("searchFlag", searchFlag);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (checkRes == 1) {
            Intent intent = new Intent(SearchActivity.this, RestaurantActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SearchActivity.this, SightsActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
    }

}