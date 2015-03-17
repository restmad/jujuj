package sample;

import java.util.ArrayList;

import demo1.Demo1Activity;
import demo2.Demo2Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch(position){
				case 0:
					intent = new Intent(MainActivity.this, Demo1Activity.class);
					break;
				case 1:
					intent = new Intent(MainActivity.this, Demo2Activity.class);
					break;
				}
				startActivity(intent);
			}
		});
		
		String[] items = { "Posting a request", "Loading from server", "Handling multiple requests" };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, items);
        
        setListAdapter(adapter);
	}

}
