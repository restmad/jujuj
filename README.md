A client-server flow always goes something like: 

query from database -> (load from server -> save ->) display -> edit -> update.

With netframe, the flow above is all automatically set with very simple code, with no need to parse from JSON, display on views. You just need to specify fields with view ids, all the datas will be automatically displayed.

Netframe is based on ActiveAndroid, Gson, universal image loader and volley, which supports TextView, SpinnerView, ImageView, CheckBox and AbsListView now. 
##sample code 

 In the sample code below, the activity does 3 things:
 	1. query from database
	2. if get nothing from database, load data from server and save
	3. display data, image included on the view

```sh
public class Demo3Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		UserBean bean = new UserBean();
		bean.userName = "Dan";
		Netframe.inject(this, new MutableEntity<UserBean>(bean));
	}
	
}
@ActivityInj(R.layout.activity_demo2)
@Table(name = "User")
public class UserBean extends Entity implements Downloadable{
	
	@ViewInj(R.id.user_portrait)
	@Column(name = "portrait")
	public String userPortrait;

	@ViewInj(R.id.user_name)
	@Column(name = "userName")
	public String userName;

	@ViewInj(R.id.email)
	@Column(name = "email")
	public String email;

	@ViewInj(R.id.married)
	@Column(name = "married")
	public boolean married;

	@ViewInj(R.id.number_list)
	public List<Number> numbers;

	public List<Number> numbers(){
		return getMany(Number.class, "userBean");
	}
	
	public UserBean(){
		super();  
	}
	
	@Override
	public Entity query() {
		UserBean entity = new Select().from(UserBean.class)
        		.where("userName = ?", userName).executeSingle();
		if(entity != null){
			entity.numbers = entity.numbers();
		} 
		return entity;
	}

	@Override
	public String onDownLoadUrl(Context context) {
		return MyApplication.URL + "netframe_get_user.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onDownloadParams() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		return params;
	}

}
@GroupViewInj(R.layout.layout_number)
@Table(name = "Number")
public class Number extends Entity{
	
	public Number(){
		super();
	}
	
	@ViewInj(R.id.contact_number)
	@Column(name = "number")
	public String number;

	@Column(name = "userBean")
	public UserBean user;

	@Override
	public Entity query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setForignKey(Entity entity) {
		user = (UserBean) entity;
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
