package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.AEAddon;
import appeng.api.IAEAddon;
import appeng.api.IAppEngApi;

@AEAddon
public class AppEngApi implements IAEAddon {

    public static final AppEngApi INSTANCE = new AppEngApi();

    private static IAppEngApi api;

    @Override
    public void onAPIAvailable(IAppEngApi iAppEngApi) {
        api = iAppEngApi;
    }

    public IAppEngApi getApi() {
        return api;
    }
}
