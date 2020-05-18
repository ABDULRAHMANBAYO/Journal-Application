package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.self.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    //Add context

    private Context context;
    private List<Journal> journalList;

    //Create constructor
    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view(row)
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder holder, int position) {
        //Bind widget with data

        Journal journal = journalList.get(position);
        String imageUrl;

        imageUrl = journal.getImageUrl();
        /*
        Use picasso to download and show image
        */
        Picasso.get().load(imageUrl).placeholder(R.drawable.pexels_photo_1632790)
        .fit()
        .into(holder.imageView);
        String timeAgo = (String) DateUtils.
                getRelativeTimeSpanString(journal.getTimeAdded()
                        .getSeconds() * 1000);
        holder.title.setText(journal.getTitle());
        holder.thought.setText(journal.getThought());
        holder.dateAdded.setText(timeAgo);
        holder.name.setText(journal.getUserName());



    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Get widget
        public TextView title,thought,dateAdded,name;
        public ImageView imageView;
        public ImageButton shareButton;
        String userId;
        String username;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.journal_title_list);
            thought = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            imageView = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_username);
            shareButton = itemView.findViewById(R.id.journal_row_share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    context.startActivity();
                }
            });
        }
    }
}
