Juju was designed to handle data fetching(both from server and local) and display data into layout. It was an abstract framework on top of which you can set up your own network requesting, database querying, image loading and so on as you need. This project provides a default implementation named ‘dependency’, as a result of which, you may find this project is built one on top of another, like this:
  sample
 dependency
jujuj

In module ‘dependency’, it implements data providers using Volley as network request, ActiveAndroid as ORM, and Universal Image Loader as image loader. You can replace them with your favorite libraries.

## How it works

In jujuj, a view can be set in two ways, one by annotation of model, another by an inter-class like what MVVM pattern suggests to. 

Assume you have a view to display information of a person, including his name, portrait, and a list of numbers. You’ll need a model like this:

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

This model defines how it’s loaded(Notice that it could be loaded either from local or server). The annotations define how this model is supposed to display in a layout. 

This is the simple use case for Jujuj. 

## What’s more

Juju also provides with handful of functional interfaces, such as Validatable, to check if a request is validate; Transformable, to transform data types; Notifiable, to notify an Activity from model; Multilpleable, to handle multiple loading requests in an model.

## What needs to be done
Moreable, to load more data, which is very basic for data-fetching apps.

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
