Netframe is a framework for Android for developers to build C-S applications more efficiently. With netframe, developers will be able to easily access to database, network, images and diaplay them to your UI.
A common client-server flow goes something like: 
query from database -> (load from server -> save ->) display -> edit -> update.
With netframe, the flow above is all automatically set with very simple code. 

@
public class LoginActivity extends Activity(
	@Override
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Netframe.inject(this, new MutableEntity<LoginRequest(new LoginRequest()));
	}

	@ViewGroupInj(R.layout.activity_login)
	public class LoginRequest implements Postable{
				
		@ViewInj(R.id.login_account)
		public String account;

		@ViewInj(R.id.login_pwd)
		public String pwd;

		@Override
		public int getSubmitButtonId() {
			return R.id.login_submit;
		}

		@Override
		public void onPostResponse(Context context, JSONObject obj) {
			//get response from server
		}

		@Override
		public String onPostUrl(Context context) {
			return MyApplication.URL + "netframe_sign_in.php";
		}

	}
}

