package nl.han.asd.project.client.android.utility;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Julius on 31/05/16.
 */

public class ViewWrapper<V extends View> extends RecyclerView.ViewHolder {

    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }
}