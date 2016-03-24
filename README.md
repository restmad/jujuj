
  ![jujuj](https://cloud.githubusercontent.com/assets/3215337/13896194/adebbf0e-edbe-11e5-9dd3-7f78008ce9d2.png)  

<img src="https://cloud.githubusercontent.com/assets/3215337/13972466/f7fdb030-f0d3-11e5-9149-fe37ffc5ea44.png" width = "300"/>

Image you're developing this application, two problems will come into your mind instantly(if not, just read on): 

1. I need to pre-load and display what's cached in the database, before retrieving data from the server and, whatever is loaded from server should be stored in the database. 

2. These posts should only include the identity of the user whom they belong to. Users' information(portrait, name, etc.) is supposed to be managed in a pool, so that whoever wants it can lazy-load it according to the identity, like ImageLoader

This is the very common scenery when developing applications that retrieve data from servers.

That's why `Jujuj` is built, to make these things easier. With `Jujuj`, you do not need to worry about how data is managed, just use it like this:
```
Jujuj.getInstance().inject(this, new LoginRequest(this));
```

`Jujuj` frees you from all the `findViewById`, `setText`, `setAdapter` whatsover by simply declaring which variable is supposed to be bind on which widget, like this:
```
@ViewInj   
public String account;
```

When you have a foreign key, for instance, user's identity in the post object, just put it this way:
```
@DependentInj   
public User user;
```

and `Jujuj` will do all the magic. 

Now wanna [try it](http://github.com/shinado/jujuj/wiki) yourself? 

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
