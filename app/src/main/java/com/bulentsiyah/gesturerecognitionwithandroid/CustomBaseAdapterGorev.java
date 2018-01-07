package com.bulentsiyah.gesturerecognitionwithandroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomBaseAdapterGorev extends BaseAdapter {
	Context context;
	List<RowItemGorev> rowItems;
	Boolean bbcheckBoxKonum = true;
	Boolean bbCheckBoxBaslamaZamani;
	Boolean bbCheckBoxPlanlananSaat;
	Boolean bbCheckBoxAciklama;
	Boolean bbCheckBoxNotlar;
	private ArrayList<RowItemGorev> arraylist;

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		rowItems.clear();
		if (charText.length() == 0) {
			rowItems.addAll(arraylist);
		} else {
//			for (RowItemGorev wp : arraylist) {
//				if (wp.title.toLowerCase(Locale.getDefault())
//						.contains(charText)) {
//					rowItems.add(wp);
//				} else if (wp.notlar.toLowerCase(Locale.getDefault()).contains(
//						charText)) {
//					rowItems.add(wp);
//				} else if (wp.konum.toLowerCase(Locale.getDefault()).contains(
//						charText)) {
//					rowItems.add(wp);
//				} else if (wp.aciklama.toLowerCase(Locale.getDefault())
//						.contains(charText)) {
//					rowItems.add(wp);
//				}
//			}

		}
		notifyDataSetChanged();
	}

	public CustomBaseAdapterGorev(Context context, List<RowItemGorev> items,
			Boolean bbcheckBoxKonum, Boolean bbCheckBoxBaslamaZamani,
			Boolean bbCheckBoxPlanlananSaat, Boolean bbCheckBoxAciklama,
			Boolean bbCheckBoxNotlar) {
		try {
			this.context = context;
			this.rowItems = items;
			this.bbcheckBoxKonum = bbcheckBoxKonum;
			this.bbCheckBoxBaslamaZamani = bbCheckBoxBaslamaZamani;
			this.bbCheckBoxPlanlananSaat = bbCheckBoxPlanlananSaat;
			this.bbCheckBoxAciklama = bbCheckBoxAciklama;
			this.bbCheckBoxNotlar = bbCheckBoxNotlar;
			this.arraylist = new ArrayList<RowItemGorev>();
			this.arraylist.addAll(rowItems);
		} catch (Exception exp) {

		}

	}

	public CustomBaseAdapterGorev(TextWatcher textWatcher,
			List<RowItemGorev> rowItems2) {
	}

	private class ViewHolder {
		ImageView imageView;
		//ImageView imageView2;
		TextView txtGorevAdi;
		TextView txtKonum;
		TextView txtPlan;
		TextView txtBasl;
		TextView txtNot;
		TextView txtNotlabel;
		TextView txtAcik;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sop_gorev_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.txtGorevAdi = (TextView) convertView
					.findViewById(R.id.textViewGorevAdii);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			//holder.imageView2 = (ImageView) convertView.findViewById(R.id.icon2);
			holder.txtKonum = (TextView) convertView
					.findViewById(R.id.textViewKonum);
			convertView.setTag(holder);
//			holder.txtBasl = (TextView) convertView
//					.findViewById(R.id.TextViewBaslaaa);
//			holder.txtPlan = (TextView) convertView
//					.findViewById(R.id.TextViewPlanZamanii);
//			holder.txtNot = (TextView) convertView
//					.findViewById(R.id.TextViewMainNotlar);
//			holder.txtNotlabel = (TextView) convertView
//					.findViewById(R.id.TextViewMainNotLabel);
//			holder.txtAcik = (TextView) convertView
//					.findViewById(R.id.TextViewAcik);
			// holder.ButtonDetail=(Button)convertView.findViewById(R.id.ButtonDetail);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RowItemGorev rowItem = (RowItemGorev) getItem(position);

		holder.txtGorevAdi.setText(rowItem.title);
		holder.imageView.setImageResource(rowItem.imageId);
		holder.imageView.setVisibility(View.GONE);
		//holder.imageView2.setImageResource(rowItem.imageId);

		holder.txtKonum.setText(rowItem.konum);
		if (!bbcheckBoxKonum) {
			holder.txtKonum.setVisibility(View.GONE);
		}
//		holder.txtBasl.setText(rowItem.baslamaZamani);
//		if (!bbCheckBoxBaslamaZamani) {
//			holder.txtBasl.setVisibility(View.GONE);
//		}
//		holder.txtPlan.setText(rowItem.planlamaZamani);
//		if (!bbCheckBoxPlanlananSaat) {
//			holder.txtPlan.setVisibility(View.GONE);
//		}
//		holder.txtAcik.setText(rowItem.aciklama);
//		if (!bbCheckBoxAciklama) {
//			holder.txtAcik.setVisibility(View.GONE);
//		}
//
//		holder.txtNot.setText(rowItem.notlar);
		if (!bbCheckBoxNotlar) {
			holder.txtNot.setVisibility(View.GONE);
			holder.txtNotlabel.setVisibility(View.GONE);
		}

		// holder.ButtonDetail.setTag(rowItem);

		// holder.ButtonDetail.setOnClickListener(new
		// AdapterView.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// try {
		// RowItemGorev itemToRemove = (RowItemGorev) v.getTag();
		// Variable_All.setSOPSecilenGorevDetayi(itemToRemove);
		// Intent intent=new
		// Intent(context,SOP_Activity_Gorev_Details.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent);
		// } catch (Exception e) {
		// e.toString();
		// }
		//
		// }
		// });
		return convertView;
	}

	@Override
	public int getCount() {
		try {
			return rowItems.size();
		} catch (Exception exp) {
			return 0;
		}

	}

	@Override
	public RowItemGorev getItem(int position) {
		try {

		} catch (Exception exp) {

		}
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItems.indexOf(getItem(position));
	}
}
