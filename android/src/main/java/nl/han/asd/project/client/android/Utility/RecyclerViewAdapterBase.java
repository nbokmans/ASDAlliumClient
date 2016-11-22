package nl.han.asd.project.client.android.utility;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import nl.han.asd.project.client.commonclient.message.Message;

@EBean
public abstract class RecyclerViewAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

    protected List<T> items = new ArrayList<>();

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<>(onCreateItemView(parent, viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    public void add(List<T> items) {
        add(items, getItemCount());
    }

    public void add(List<T> items, int startPosition) {
        if(items == null)
            return;
        this.items.addAll(startPosition, items);
        notifyDataChanged();
    }

    public void add(T item) {
        add(item, getItemCount());
    }

    public void add(T item, int position) {
        items.add(position, item);
        notifyInserted(position);
    }

    public void remove(T item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyRemoved(position);
    }

    public void clear() {
        int itemCount = items.size();
        items.clear();
        notifyItemsCleared(itemCount);
    }

    @UiThread
    protected void notifyItemsCleared(int count){
        notifyItemRangeRemoved(0, count);
    }

    @UiThread
    protected void notifyInserted(int position) {
        notifyItemInserted(position);
    }

    @UiThread
    protected void notifyRemoved(int position) {
        notifyItemRemoved(position);
    }

    @UiThread
    protected void notifyDataChanged(){
        notifyDataSetChanged();
    }

    public boolean contains(Message message) {
        return items.contains(message);
    }
}