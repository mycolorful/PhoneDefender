package per.yrj.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PeopleListActivity extends ListActivity {
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_peoplelist);
		//获取raw_contact表的uri
		Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
		//获取data表的uri
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		//查询raw_contact表的contact_id
		Cursor rawContactsCursor = getContentResolver().query(rawContactsUri, new String[]{"contact_id"}, null, null, null);
		if(rawContactsCursor != null){
			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				//判断contactId是否为空
				if(contactId == null)
					continue;
				//如果不为空，那么就拿着这个id到data表中去查询该id对应的数据，用于获取名字，电话号码等信息
				Cursor dataCursor = getContentResolver().query(dataUri, new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{contactId}, null);
				if(dataCursor != null){
					//如果查到了，那就通过mimetype值来判断拿到的是名字还是电话号码，然后再分别保存
					HashMap<String, String> map = new HashMap<String, String>();
					while(dataCursor.moveToNext()){
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
							map.put("phone1", data1);
						}else if("vnd.android.cursor.item/name".equals(mimetype)){
							map.put("name", data1);
						}
					}
					data.add(map);
				}
			}
		}
		this.setListAdapter(new SimpleAdapter(this, data, R.layout.peoplelist_item, new String[]{"name", "phone1"}, new int[]{R.id.tv_name, R.id.tv_num}));
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		TextView name, num;
		name = (TextView) v.findViewById(R.id.tv_name);
		num = (TextView) v.findViewById(R.id.tv_num);
		intent.putExtra("info", name.getText()+":"+num.getText());
		setResult(1, intent);
		finish();
		super.onListItemClick(l, v, position, id);
	}
}
