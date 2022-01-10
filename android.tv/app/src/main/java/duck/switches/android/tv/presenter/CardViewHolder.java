package duck.switches.android.tv.presenter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import duck.switches.android.tv.R;

public class CardViewHolder<T> extends Presenter.ViewHolder {
    public final Context context;
    public final ImageCardView card;
    private CardPresenter.MoveMode.OnMoveModeListener onMoveModeListener;

    public CardViewHolder(ViewGroup parent) {
        super(new ImageCardView(parent.getContext()));
        card = (ImageCardView) view;
        context = view.getContext();
    }

    public void bind(T item, CardPresenter.ListenersInfo<T> listenersInfo, CardPresenter.MoveMode moveMode) {
        setupOnClick(item, listenersInfo, moveMode);
        setupOnLongClick(item, listenersInfo, moveMode);
        setupOnCreateContextMenu(item, listenersInfo, moveMode);
        setupOnMoveListener(item, listenersInfo, moveMode);
        setupOnMoveModeListener(moveMode);
    }

    public void unbind(CardPresenter.MoveMode moveMode) {
        card.setMainImage(null);
        card.getMainImageView().getOverlay().clear();
        cleanupOnMoveModeListener(moveMode);
    }

    private void setupOnClick(T item, CardPresenter.ListenersInfo<T> listenersInfo, CardPresenter.MoveMode moveMode) {
        if (listenersInfo.onClickListener == null) {
            card.setOnClickListener(null);
        } else {
            card.setOnClickListener(v -> {
                if (moveMode.isActive()) {
                    moveMode.stop();
                    return;
                }
                listenersInfo.onClickListener.onClick(item);
            });
        }
    }

    private void setupOnLongClick(T item, CardPresenter.ListenersInfo<T> listenersInfo, CardPresenter.MoveMode moveMode) {
        if (listenersInfo.onLongClickListener == null) {
            card.setOnLongClickListener(null);
        } else {
            card.setOnLongClickListener(v -> {
                if (moveMode.isActive()) {
                    return false;
                }
                return listenersInfo.onLongClickListener.onLongClick(item);
            });
        }
    }

    private void setupOnCreateContextMenu(T item, CardPresenter.ListenersInfo<T> listenersInfo, CardPresenter.MoveMode moveMode) {
        if (listenersInfo.onCreateContextMenuListener == null) {
            card.setOnCreateContextMenuListener(null);
        } else {
            card.setOnCreateContextMenuListener((m, view, menuInfo) -> {
                if (moveMode.isActive()) {
                    return;
                }
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                Menu menu = popupMenu.getMenu();
                listenersInfo.onCreateContextMenuListener.onCreateContextMenu(item, menu);
                popupMenu.show();
            });
        }
    }

    private void setupOnMoveListener(T item, CardPresenter.ListenersInfo<T> listenersInfo, CardPresenter.MoveMode moveMode) {
        if (listenersInfo.onMoveListener == null) {
            card.setOnKeyListener(null);
        } else {
            card.setOnKeyListener((v, keyCode, event) -> {
                if (!moveMode.isActive()) {
                    return false;
                }
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        listenersInfo.onMoveListener.onMoveLeft(item);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        listenersInfo.onMoveListener.onMoveRight(item);
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        listenersInfo.onMoveListener.onMoveUp(item);
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        listenersInfo.onMoveListener.onMoveDown(item);
                        break;
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        moveMode.stop();
                        break;
                    default:
                        return false;
                }
                return true;
            });
        }
    }

    private void setupOnMoveModeListener(CardPresenter.MoveMode moveMode) {
        if (onMoveModeListener != null) {
            moveMode.removeOnMoveModeListener(onMoveModeListener);
        }
        onMoveModeListener = new CardPresenter.MoveMode.OnMoveModeListener() {
            private static final int OVERLAY_SIZE = 50;
            private static final int OVERLAY_PADDING = 5;

            @Override
            public void onStart() {
                ImageView imageView = card.getMainImageView();
                imageView.getOverlay().clear();
                if (moveMode.isActive() && card.isSelected()) {
                    int left = OVERLAY_PADDING;
                    int top = OVERLAY_PADDING + imageView.getHeight() / 2 - OVERLAY_SIZE / 2;
                    addOverlay(imageView, R.drawable.icon_left, left, top);
                    left = imageView.getWidth() - OVERLAY_SIZE - OVERLAY_PADDING;
                    addOverlay(imageView, R.drawable.icon_right, left, top);
                }
            }

            @Override
            public void onStop() {
                card.getMainImageView().getOverlay().clear();
            }

            private void addOverlay(ImageView imageView, @DrawableRes int drawableId, int left, int top) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableId);
                assert drawable != null;
                drawable.setBounds(left, top, left + OVERLAY_SIZE, top + OVERLAY_SIZE);
                drawable.setTint(imageView.getImageTintList().getDefaultColor());
                imageView.getOverlay().add(drawable);
            }
        };
        moveMode.addOnMoveModeListener(onMoveModeListener);
    }

    private void cleanupOnMoveModeListener(CardPresenter.MoveMode moveMode) {
        if (onMoveModeListener != null) {
            moveMode.removeOnMoveModeListener(onMoveModeListener);
        }
    }
}
