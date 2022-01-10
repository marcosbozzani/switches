package duck.switches.android.tv.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import duck.switches.android.tv.R;
import duck.switches.android.tv.databinding.MainActivityBinding;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new MainFragment())
                    .commit();
        }
    }
}
