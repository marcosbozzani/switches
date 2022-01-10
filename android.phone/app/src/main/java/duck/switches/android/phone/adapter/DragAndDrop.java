package duck.switches.android.phone.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DragAndDrop {
    private static final ItemTouchHelper helper = new ItemTouchHelper(new Callback());

    public static void enable(RecyclerView recyclerView) {
        helper.attachToRecyclerView(recyclerView);
    }

    public static class Callback extends ItemTouchHelper.SimpleCallback {
        public Callback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            if (recyclerView.getAdapter() instanceof DragAndDropHandler) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                ((DragAndDropHandler) recyclerView.getAdapter()).move(from, to);
                return true;
            }
            return false;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder,
                                      int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof DragAndDropItemHandler) {
                    ((DragAndDropItemHandler) viewHolder).select();
                }
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof DragAndDropItemHandler) {
                ((DragAndDropItemHandler) viewHolder).clear();
            }
            if (recyclerView.getAdapter() instanceof DragAndDropHandler) {
                ((DragAndDropHandler) recyclerView.getAdapter()).moveEnd();
            }
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // not used
        }
    }
}
