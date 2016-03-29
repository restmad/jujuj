package sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import demo1.Demo1Activity;
import demo2.Demo2Activity;
import demo3.Demo3Activity;
import demo4.Demo4Activity;
import demo5.Demo5Activity;
import demo6.Demo6Activity;
import demo7.Demo7Activity;
import demo8.Demo8Activity;

public class MainActivity extends ListActivity{

	Class[] targets = {Demo1Activity.class, Demo2Activity.class, Demo3Activity.class, Demo4Activity.class,
			Demo5Activity.class, Demo6Activity.class, Demo7Activity.class, Demo8Activity.class};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, targets[position]);
				startActivity(intent);
			}
		});
		
		String[] items = { "Posting a request", "Loading from server", "Handling multiple requests",
				"Using Loadable", "Sharing User bean", "Dependent injection",
				"Using Listable", "Using Action"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
        
        setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
