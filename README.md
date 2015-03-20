A client-server flow always goes something like: 

query from database -> (load from server -> save ->) display -> edit -> update.

With netframe, the flow above is all automatically set with very simple code, with no need to parse from JSON, display on views. You just need to specify fields with view ids, all the datas will be automatically displayed.

Netframe is based on ActiveAndroid, Gson, universal image loader and volley, which supports TextView, SpinnerView, ImageView, CheckBox and AbsListView now. 
  
  ##sample code
```sh
public class Demo2Activity extends Activity{
    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
            
		ImageLoader.getInstance().init(
        	ImageLoaderConfiguration.createDefault(this));
		UserBean bean = new UserBean();
		bean.userName = "Dan";
		/**
		 * if your model is a Downloadable or Entity, 
             	it MSUT be wrapped it with MutableEntity
		 * With MutableEntity, an object will be changed after loading from server, 
		 * in the meantime not reloaded when using inject method many times
		 * 
		 */
		Netframe.inject(this, new MutableEntity<UserBean>(bean));
	}
}
@ActivityInj(R.layout.activity_demo2)
public class UserBean implements Downloadable{
	
	@ViewInj(R.id.user_portrait)
	public String userPortrait;

	@ViewInj(R.id.user_name)
	public String userName;

	@ViewInj(R.id.email)
	public String email;

	@ViewInj(R.id.married)
	public boolean married;

	@ViewInj(R.id.number_list)
	public List<Number> numbers;

	@Override
	public String onDownLoadUrl(Context context) {
		return MyApplication.URL + "netframe_get_user.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
	}

	@Override
	public Object onDownloadParams() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		return params;
	}
}
```
## Copyright

Copyright (C) 2014 shinado <shinado023@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
