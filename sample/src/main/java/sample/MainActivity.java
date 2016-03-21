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
                    case 2:
						intent = new Intent(MainActivity.this, Demo3Activity.class);
						break;
					case 3:
						intent = new Intent(MainActivity.this, Demo4Activity.class);
						break;
					case 4:
						intent = new Intent(MainActivity.this, Demo5Activity.class);
						break;
					case 5:
						intent = new Intent(MainActivity.this, Demo6Activity.class);
						break;
				}
				startActivity(intent);
			}
		});
		
		String[] items = { "Posting a request", "Loading from server", "Handling multiple requests",
				"Using Loadable", "Setting from different presenter", "Using lazy loading", "Tell me about it"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, items);
        
        setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
