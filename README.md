
  ![jujuj](https://cloud.githubusercontent.com/assets/3215337/13896194/adebbf0e-edbe-11e5-9dd3-7f78008ce9d2.png)  

## What is Jujuj

Jujuj makes your code cleaner. Basically, Jujuj solves two major problems. 
- multiple data handling
- dependency 

For the first problem, let's assume you have a ListView to display posts. When this view is launched, it loads posts in the database and displays it, while in the meantime, load more posts in the server, and display it. 
Therefore, the basic structure of Jujuj looks like this:

  ![jujuj](https://cloud.githubusercontent.com/assets/3215337/13946903/6cca8910-f052-11e5-8f4d-2ba28c200223.png)  

Now with Jujuj, you don't have to do all those dirty works, just ask Jujuj to handle it with:
```
Jujuj.getInstance().inject(this, new PostDlb());
```

For the second problem, let's say in the posts above, every post contains a userId to identify whom this post belongs to. In this case, users' portraits, names should be displayed on the posts, which are supposed to be loaded from a pool, for instance. 

With Jujuj, you just need to annotate this object
```
@DependentInj
public User user;
```
and Jujuj will load this object and display it accordingly, like this:

  ![jujuj](https://cloud.githubusercontent.com/assets/3215337/13946913/75d7e4da-f052-11e5-9dd7-f3e2a2480c56.png)  

Jujuj is an abstract framework on top of which you can set up your own network requesting, database querying, image loading and so on as you need. This project provides a default implementation named ‘dependency’, as a result of which, you may find this project is built one on top of another, like this:

- sample
- dependency
- jujuj-lib

In module ‘dependency’, it implements data providers using Volley as network request, ActiveAndroid as ORM, and Universal Image Loader as image loader. You can replace them with your favorite libraries.

## Why Jujuj
- Lazy loading. Just display a view without even knowing where the data comes from, like what ImageLoader does.
- MVVM. Jujuj makes you write codes in a MVVM pattern.
- Independent. of UI, of any data agency.
- Testable. network and data can be tested separately. 
- Abstract. you can implement if with your favorite libs.
- No reflection. 

## How it works

In jujuj, a view can be set in two ways, one by annotation of model, another by an inter-class like what MVVM pattern suggests to. 

Assume you have a view to display information of a person, including his name, portrait, and a list of numbers. You’ll need a model like this:

```
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

	public List<Number> numbers(){
		return getMany(Number.class, "userBean");
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
```

This model defines how it’s loaded. The annotations define how this model is supposed to display in a layout. 

This is the simple use case for Jujuj. It's fast and easy to understand, except that it is not so reasonable. Therefore, we extend it with MVVM pattern. In the following case, UserBean is ONLY a model, while UserDlb defines how this UserBean is supposed to be loaded and displayed in a layout.

```
@ActivityInj(R.layout.activity_demo4)
public class UserDlb extends Loadable<UserBean>{
    
    public UserDlb(String userName){
        setEntity(new UserBean(userName));
    }

    @ViewValueInj(R.id.user_name)
    public String userName(){
        return getEntity().userName;
    }

    @ViewValueInj(R.id.user_portrait)
    public String portrait(){
        return getEntity().userPortrait;
    }

    @Override
    public String onDownLoadUrl(Context context) {
        return MyApplication.URL + "netframe_get_all_users.php";
    }

    @Override
    public void onDownLoadResponse(Context context) {

    }

    @Override
    public Object onDownloadParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", getEntity().userName);
        return params;
    }

    @Override
    public void onError(Context context, String msg) {

    }
}
```

## What’s more

Jujuj also provides with handful of functional interfaces, such as Validatable, to check if a request is validate; Transformable, to transform data types; Notifiable, to notify an Activity from model; Multilpleable, to handle multiple loading requests in an model.

## What needs to be done
- Moreable, to load more data, which is very basic for data-fetching apps.
- ListView. ListView works pretty bad currently.

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
