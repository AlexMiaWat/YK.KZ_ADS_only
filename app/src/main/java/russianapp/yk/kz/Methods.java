package russianapp.yk.kz;

import android.content.Context;

public class Methods {
    // Снимаем замеры экрана, выдаем разрешения картинок
    static int GetMetrics(Context context, int param) {
        float density = context.getResources().getDisplayMetrics().density;
        int halfHeight = 20;

        if ((density >= 0.75)&&(density < 1.0)) {
            if (param == 1) {
                halfHeight = 40;
            } else {
                halfHeight = 20;
            }
            ;

        } else if ((density >= 1.0)&&(density < 1.5)) {
            if (param == 1) {
                halfHeight = 120;
            } else {
                halfHeight = 30;
            }
            ;

        } else if ((density >= 1.5)&&(density < 2.0)){
            if (param == 1) {
                halfHeight = 190;
            } else {
                halfHeight = 40;
            }
            ;

        } else if ((density >= 2.0)&&(density < 3.0)) {
            if (param == 1) {
                halfHeight = 300;
            } else {
                halfHeight = 60;
            }
            ;

        } else if ((density >= 3.0)&&(density < 4.0)) {
            if (param == 1) {
                halfHeight = 450;
            } else {
                halfHeight = 100;
            }
            ;

        } else if (density >= 4.0) {
            if (param == 1) {
                halfHeight = 600;
            } else {
                halfHeight = 200;
            }
            ;
        }
        return halfHeight;
    }
}
