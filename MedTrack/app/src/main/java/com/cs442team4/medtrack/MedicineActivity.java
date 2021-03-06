package com.cs442team4.medtrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cs442team4.medtrack.db.MedList;
import com.cs442team4.medtrack.helper.DailogMedicineDetails;

@SuppressLint("NewApi")
public class MedicineActivity extends Activity {
	MedList ML;
	ListView list;
	ArrayAdapter<String[]> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medicine);

		list = (ListView) findViewById(R.id.MedListView);

		// Creating the list adapter and populating the list
		listAdapter = new CustomListAdapter(this, R.layout.medlist);

		ML = new MedList(this.getBaseContext());
		ML.openReadable();
		Cursor cursor = ML.getMedList();

		if (cursor.getCount() == 0) {
			TextView tv = (TextView) findViewById(R.id.MedListMsg);
			tv.setVisibility(View.VISIBLE);
		}

		while (cursor.moveToNext()) {
			String timings = MedList.getTimings(
					cursor.getString(cursor.getColumnIndex(MedList.TIME1)),
					cursor.getString(cursor.getColumnIndex(MedList.TIME2)),
					cursor.getString(cursor.getColumnIndex(MedList.TIME3)),
					cursor.getString(cursor.getColumnIndex(MedList.TIME4)));

			String[] medcontent = {
					cursor.getString(cursor.getColumnIndex(MedList.NAME)),
					timings,
					String.valueOf(cursor.getInt(cursor
							.getColumnIndex(MedList.COUNT))),
					String.valueOf(cursor.getInt(cursor
							.getColumnIndex(MedList.MED_ID))),
					String.valueOf(cursor.getInt(cursor
							.getColumnIndex(MedList.IMAGE))) };
			listAdapter.add(medcontent);
		}

		list.setAdapter(listAdapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				TextView medIdTv = (TextView) view.findViewById(R.id.MedListId);
				long MedId = 0;
				try {
					MedId = Integer.parseInt(medIdTv.getText().toString());
				} catch (Exception ex) {
				}
				DailogMedicineDetails
						.app_launched(MedicineActivity.this, MedId);
			}
		});
		ML.close();
	}

	@SuppressLint("InflateParams")
	class CustomListAdapter extends ArrayAdapter<String[]> {

		public CustomListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.medlist,
						null);
			}
			((TextView) convertView.findViewById(R.id.MedListName))
					.setText(getItem(position)[0]);
			((TextView) convertView.findViewById(R.id.MedListTimings))
					.setText(getItem(position)[1]);
			((TextView) convertView.findViewById(R.id.MedListCount))
					.setText("Remaining Medicine Count : "
							+ getItem(position)[2]);
			((TextView) convertView.findViewById(R.id.MedListId))
					.setText(getItem(position)[3]);
			int Imgid = convertView.getResources().getIdentifier(
					"pill0" + getItem(position)[4], "drawable",
					getPackageName());
			((ImageView) convertView.findViewById(R.id.MedListImage))
					.setImageResource(Imgid);
			return convertView;
		}

	}

	public void AddMed(View v) {
		Intent intent = new Intent(this, CreateMedActivity.class);
		startActivity(intent);
		this.finish();
	}

	public void settings(View v) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
}
