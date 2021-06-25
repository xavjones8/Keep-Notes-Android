package com.xavierjonesco.keepnotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TrashAdapter  extends RecyclerView.Adapter<TrashAdapter.ViewHolder> implements Filterable {
    LayoutInflater inflater;
    List<Note> notes;
    List<Note> search;

    TrashAdapter(Context context, List<Note> notes) {
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
        search = new ArrayList<>(notes);
    }

    @NonNull
    @Override
    public TrashAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.custom_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashAdapter.ViewHolder viewHolder, int i) {
        String title = notes.get(i).getTitle();
        String date = notes.get(i).getDate();
        String time = notes.get(i).getTime();
        String content = notes.get(i).getContent();
        viewHolder.nTitle.setText(title);
        viewHolder.nDate.setText(date);
        viewHolder.nTime.setText(time);
        viewHolder.nContent.setText(content);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(search);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Note item : search){
                    if(item.getTitle().toLowerCase().contains(filterPattern)||item.getContent().toLowerCase().trim().contains(filterPattern)||item.getDate().toLowerCase().trim().contains(filterPattern)||item.getTime().toLowerCase().trim().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nTitle, nDate, nTime, nContent, nID;

        public ViewHolder(final View itemView) {
            super(itemView);
            nTitle = itemView.findViewById(R.id.nTitle);
            nDate = itemView.findViewById(R.id.nDate);
            nTime = itemView.findViewById(R.id.nTime);
            nContent = itemView.findViewById(R.id.nContents);
            nID = itemView.findViewById(R.id.listId);

            //Handles CLicks on recyclerview
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), TrashRestore.class);
                    i.putExtra("ID", notes.get(getAdapterPosition()).getId());
                    i.putExtra("layout", v.getId());
                    v.getContext().startActivity(i);
                }
            });


        }
    }
}
