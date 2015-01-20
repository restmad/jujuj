A client-server flow goes something like: 
query from database -> (load from server -> save ->) display -> edit -> update.
With netframe, the flow above is all automatically set with very simple code, with no need to parse from JSON, display with views. You just need to specify fields with views like:

ViewGroupInj(R.layout.layoutnumber)
public class Number{
	ViewInj(R.id.contactnumber)
	public String number;
}

Netframe supports TextView, ImageView, CheckBox and AbsListView.
