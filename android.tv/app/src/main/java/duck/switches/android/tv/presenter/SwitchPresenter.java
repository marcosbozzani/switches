package duck.switches.android.tv.presenter;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.ImageCardView;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.model.SwitchState;
import duck.switches.android.tv.R;

public class SwitchPresenter extends CardPresenter<Switch, SwitchInfo> {
    @Override
    public void onBind(CardViewHolder<Switch> viewHolder, Switch item, SwitchInfo switchInfo) {
        viewHolder.card.setTitleText(item.getName());
        if (switchInfo == null) {
            setState(viewHolder.context, viewHolder.card, STATE_UNAVAILABLE);
        } else {
            if (switchInfo.getSwitchState() == SwitchState.ON) {
                setState(viewHolder.context, viewHolder.card, STATE_ACTIVE);
            } else {
                setState(viewHolder.context, viewHolder.card, STATE_INACTIVE);
            }
        }
    }

    private void setState(Context context, ImageCardView card, StateRes stateRes) {
        card.setMainImage(context.getDrawable(stateRes.iconId));
        card.getMainImageView().setImageTintList(getColor(context, stateRes.fgColorId));
        card.setBackgroundTintList(getColor(context, stateRes.bgColorId));
    }

    private ColorStateList getColor(Context context, @ColorRes int colorId) {
        return ColorStateList.valueOf(ContextCompat.getColor(context, colorId));
    }

    @Override
    public void onUnbind(CardViewHolder<Switch> viewHolder) {

    }

    private static final StateRes STATE_UNAVAILABLE =
            new StateRes(R.drawable.icon_offline, R.color.gray_800, R.color.gray_900);
    private static final StateRes STATE_ACTIVE =
            new StateRes(R.drawable.icon_power, R.color.white_off, R.color.green_800);
    private static final StateRes STATE_INACTIVE =
            new StateRes(R.drawable.icon_power, R.color.white_off, R.color.gray_700);

    private static class StateRes {
        public @DrawableRes
        int iconId;
        public @ColorRes
        int fgColorId;
        public @ColorRes
        int bgColorId;

        public StateRes(int iconId, int fgColorId, int bgColorId) {
            this.iconId = iconId;
            this.fgColorId = fgColorId;
            this.bgColorId = bgColorId;
        }
    }
}
