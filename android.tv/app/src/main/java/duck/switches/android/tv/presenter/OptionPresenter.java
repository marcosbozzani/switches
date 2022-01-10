package duck.switches.android.tv.presenter;

import duck.switches.android.tv.model.Option;

public class OptionPresenter extends CardPresenter<Option, Object> {
    @Override
    public void onBind(CardViewHolder<Option> viewHolder, Option item, Object payload) {
        viewHolder.card.setTitleText(item.getName());
        viewHolder.card.setMainImage(viewHolder.context.getDrawable(item.getIconID()));
    }

    @Override
    public void onUnbind(CardViewHolder<Option> viewHolder) {

    }
}
