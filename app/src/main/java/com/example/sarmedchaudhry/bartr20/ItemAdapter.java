package com.example.sarmedchaudhry.bartr20;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<Items> data = Collections.emptyList();
    private Context context;
    private Fragment fragment;

    public ItemAdapter(Context context, List<Items> data, Fragment fragment){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.fragment = fragment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Items current = data.get(position);

        holder.name.setText(current.getName());
        holder.price.setText(current.getPrice());
        holder.description.setText(current.getDescription());

        if(current.getImage() != null)
            holder.image.setImageBitmap(current.getImage());
        else
            holder.image.setImageResource(R.drawable.ic_no_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name;
        TextView description;
        TextView price;
        public ImageButton delete, edit, viewOffers;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.item_Image);
            name = (TextView) itemView.findViewById(R.id.item_Name);
            price = (TextView) itemView.findViewById(R.id.item_Price);
            description = (TextView) itemView.findViewById(R.id.item_Description);
            delete = (ImageButton) itemView.findViewById(R.id.item_delete);
            edit = (ImageButton) itemView.findViewById(R.id.item_edit);
            viewOffers = (ImageButton) itemView.findViewById(R.id.item_view_offers);

            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
            viewOffers.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == delete.getId()){
                delete(getAdapterPosition());
            }
            else if(view.getId() == edit.getId()){
                if(fragment instanceof MyList){
                    ((MyList)fragment).ItemUpdate(view);
                    notifyDataSetChanged();
                }

            }
            else if(view.getId() == viewOffers.getId()){

            }
        }
    }

    private void delete(int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
    }
}
