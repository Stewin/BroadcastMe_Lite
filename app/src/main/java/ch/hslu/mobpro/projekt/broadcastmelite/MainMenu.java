package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Context;

import java.util.ArrayList;

public class MainMenu {


    public static ArrayList<MenuItems> Items;

    public static void LoadModel(Context context) {

        Items = new ArrayList<MenuItems>();

        String[] mPlanetTitles = context.getResources().getStringArray(R.array.menuPoints_array);
        for (int i = 0; i < mPlanetTitles.length; i++) {
            Items.add(new MenuItems(i + 1, R.mipmap.navigation_next_item, mPlanetTitles[i]));

        }
    }

    public static MenuItems GetbyId(int id) {

        for (MenuItems item : Items) {
            if (item.Id == id) {
                return item;
            }
        }
        return null;
    }
}
