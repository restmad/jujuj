package sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import jujuj.post.PostActivity;
import jujuj.downloadable.Demo2Activity;
import jujuj.multiple.Demo3Activity;
import jujuj.Loadable.Demo4Activity;
import jujuj.multibean.Demo5Activity;
import jujuj.dependent.Demo6Activity;
import jujuj.listable.Demo7Activity;
import jujuj.action.Demo8Activity;
import jujuj.proxy.ProxyActivity;
import jujuj.validate.ValidateActivity;

public class MainActivity extends ListActivity{

	Class[] targets = {PostActivity.class, ValidateActivity.class, ProxyActivity.class, Demo2Activity.class, Demo3Activity.class, Demo4Activity.class,
			Demo5Activity.class, Demo6Activity.class, Demo7Activity.class, Demo8Activity.class};

	String[] items = { "Posting a request", "Post with validation", "Post with proxy", "Loading from server", "Handling multiple requests",
			"Using Loadable", "Sharing User bean", "Dependent injection",
			"Using Listable", "Using Action"};

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
        
        setListAdapter(adapter);
	}

}
