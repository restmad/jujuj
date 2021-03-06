package plain.demo4;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shinado.netframe.sample.R;

import framework.provider.Listener;
import provider.CacheProvider;
import provider.database.DBProvider;
import provider.volley.VolleyProvider;
import sample.Constants;

public class Demo4Activity extends Activity{

	private ListView mListView;
	private UserAdapter mAdapter;
	private TextView mName;
	private TextView mMail;
	private ImageView mPortrait;
	private CheckBox mMarriage;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";

		setContentView(R.layout.activity_demo2n3);
		mListView = (ListView) findViewById(R.id.numbers);
		mName = (TextView) findViewById(R.id.userName);
		mPortrait = (ImageView) findViewById(R.id.userPortrait);
		mMail = (TextView) findViewById(R.id.email);
		mMarriage = (CheckBox) findViewById(R.id.married);

		new DBProvider().handleData(Demo4Activity.this,
				Constants.URL + "get_friends",
				null,
				UserBean.class,
				new Listener.Response<UserBean>() {
					@Override
					public void onResponse(UserBean obj) {
						if (obj == null) {
							new CacheProvider().handleData(Demo4Activity.this,
									Constants.URL + "get_friends",
									null,
									UserBean.class,
									new Listener.Response<UserBean>() {
										@Override
										public void onResponse(UserBean obj) {
											if (obj == null){
												new VolleyProvider().handleData(Demo4Activity.this,
														Constants.URL + "get_friends",
														null,
														UserBean.class,
														new Listener.Response<UserBean>() {
															@Override
															public void onResponse(UserBean obj) {
																setView(obj);
															}
														},
														new Listener.Error() {
															@Override
															public void onError(String msg) {
																Toast.makeText(Demo4Activity.this, msg, Toast.LENGTH_LONG).show();
															}
														});
											}else{
												setView(obj);
											}
										}
									},
									new Listener.Error() {
										@Override
										public void onError(String msg) {
											Toast.makeText(Demo4Activity.this, msg, Toast.LENGTH_LONG).show();
										}
									});
						} else {
							setView(obj);
						}
					}
				},
				new Listener.Error() {
					@Override
					public void onError(String msg) {
						Toast.makeText(Demo4Activity.this, msg, Toast.LENGTH_LONG).show();
					}
				});
	}

	private void setView(UserBean obj){
		ImageLoader.getInstance().displayImage(obj.userPortrait, mPortrait);
		mName.setText(obj.userName);
		mMail.setText(obj.email);
		mMarriage.setChecked(obj.married);
		mAdapter = new UserAdapter(Demo4Activity.this, obj.numbers);
		mListView.setAdapter(mAdapter);
	}
}
