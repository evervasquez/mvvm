package pe.mobytes.examplemvvm1.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import pe.mobytes.examplemvvm1.MainActivity;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}
