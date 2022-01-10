package duck.switches.android.tv.presenter;

import android.view.Menu;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CardPresenter<TItem, TPayload> extends Presenter {

    public final MoveMode moveMode = new MoveMode();
    private final Map<TItem, TPayload> payloadsCache = new HashMap<>();
    private final ListenersInfo<TItem> listenersInfo = new ListenersInfo<TItem>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new CardViewHolder<TItem>(parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(ViewHolder viewHolder, Object item, List<Object> payloads) {
        CardViewHolder<TItem> cardViewHolder = (CardViewHolder<TItem>) viewHolder;
        cardViewHolder.bind((TItem) item, listenersInfo, moveMode);
        if (payloads.size() == 0) {
            onBind(cardViewHolder, (TItem) item, payloadsCache.get((TItem) item));
        } else {
            for (Object payload : payloads) {
                payloadsCache.put((TItem) item, (TPayload) payload);
                onBind(cardViewHolder, (TItem) item, (TPayload) payload);
            }
        }
    }

    public abstract void onBind(CardViewHolder<TItem> viewHolder, TItem item, TPayload payload);

    @Override
    @SuppressWarnings("unchecked")
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        CardViewHolder<TItem> cardViewHolder = (CardViewHolder<TItem>) viewHolder;
        cardViewHolder.unbind(moveMode);
        onUnbind(cardViewHolder);
    }

    public abstract void onUnbind(CardViewHolder<TItem> viewHolder);

    public TPayload getPayload(TItem item) {
        return payloadsCache.get(item);
    }

    public void setOnClickListener(OnClickListener<TItem> onClickListener) {
        listenersInfo.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener<TItem> onLongClickListener) {
        listenersInfo.onLongClickListener = onLongClickListener;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener<TItem> onCreateContextMenuListener) {
        listenersInfo.onCreateContextMenuListener = onCreateContextMenuListener;
    }

    public void setOnMoveListener(OnMoveListener<TItem> onMoveListener) {
        listenersInfo.onMoveListener = onMoveListener;
    }

    public static class MoveMode {
        private boolean isActive = false;
        private ArrayList<OnMoveModeListener> listeners = new ArrayList<>();

        public boolean isActive() {
            return isActive;
        }

        public void start() {
            isActive = true;
            for (OnMoveModeListener listener : new ArrayList<>(listeners)) {
                listener.onStart();
            }
        }

        public void stop() {
            isActive = false;
            for (OnMoveModeListener listener : new ArrayList<>(listeners)) {
                listener.onStop();
            }
        }

        public interface OnMoveModeListener {
            void onStart();

            void onStop();
        }

        public void addOnMoveModeListener(OnMoveModeListener onMoveModeListener) {
            listeners.add(onMoveModeListener);
        }

        public void removeOnMoveModeListener(OnMoveModeListener onMoveModeListener) {
            listeners.remove(onMoveModeListener);
        }
    }

    protected static class ListenersInfo<T> {
        OnClickListener<T> onClickListener;
        OnLongClickListener<T> onLongClickListener;
        OnCreateContextMenuListener<T> onCreateContextMenuListener;
        OnMoveListener<T> onMoveListener;
    }

    public interface OnClickListener<T> {
        void onClick(T item);
    }

    public interface OnLongClickListener<T> {
        boolean onLongClick(T item);
    }

    public interface OnCreateContextMenuListener<T> {
        void onCreateContextMenu(T item, Menu menu);
    }

    public interface OnMoveListener<T> {
        void onMoveLeft(T item);

        void onMoveRight(T item);

        void onMoveUp(T item);

        void onMoveDown(T item);
    }
}
