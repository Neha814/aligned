package com.andtinder.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andtinder.R;
import com.andtinder.model.CardModel;
import com.squareup.picasso.Picasso;


public final class SimpleCardStackAdapter extends CardStackAdapter {

	public Context context;
    ImageView image;
    TextView name , like,pics;
    String TAG = "SimpleCardStackAdapter";

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);

		context = mContext;
	}

	@Override
	public View getCardView(int position, final CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.card_layout, parent, false);
			assert convertView != null;
		}



//		((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
//		((TextView) convertView.findViewById(R.id.name)).setText(model.getName());
//		((TextView) convertView.findViewById(R.id.like)).setText(model.getLike());
//		((TextView) convertView.findViewById(R.id.pics)).setText(model.getPics());

        image = (ImageView) convertView.findViewById(R.id.image);
        name = (TextView) convertView.findViewById(R.id.name);
        like = (TextView) convertView.findViewById(R.id.like);
        pics = (TextView) convertView.findViewById(R.id.pics);

        Picasso.with(context)
                .load(model.getPic_url())
                .placeholder(R.drawable.profilepic).error(R.drawable.profilepic).fit()
                .into((ImageView) convertView.findViewById(R.id.image));

        name.setText(model.getName());
        like.setText(model.getLike());
        pics.setText(model.getPics());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, Class.forName("com.aligned.activity.ProfileDetailsActivity"));
                    intent.putExtra("name",model.getName());
                    intent.putExtra("like",model.getLike());
                    intent.putExtra("no_of_pics",model.getPics());
                    intent.putExtra("comma_sep_values",model.getCommaSepPics());
                    intent.putExtra("fbid",model.getFbId());

                    context.startActivity(intent);
                    Log.e(TAG,"name= "+model.getName()+", like= "+model.getLike()+", no_of_pics= "+model.getPics()+
                    ", commaSeppics= "+model.getCommaSepPics());

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
		return convertView;
	}
}
