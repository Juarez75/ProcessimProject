package be.heh.std.customView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.heh.std.R;
import be.heh.std.app.PillsActivity;
import be.heh.std.app.StartActivity;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;


public class RobotView extends LinearLayout {

    private PlcConf robot;
    private Context context;
    private ImageButton remove;
    private ImageButton launch;


    private LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public RobotView(Context context, PlcConf r) {
        super(context);
        this.context = context;
        this.robot = r;
        this.setLayoutParams(layoutParams);
        this.setOrientation(LinearLayout.HORIZONTAL);

        this.addView(description());

        remove = getButton(R.drawable.ic_delete);
        this.addView(remove);

        launch = getButton(R.drawable.ic_play);
        this.addView(launch);
    }

    private ImageButton getButton(int value){
        ImageButton b = new ImageButton(context);
        b.setImageResource(value);
        b.setOnClickListener(clickListener);
        return b;
    }

    private TextView description(){
        TextView tv = new TextView(context);
        tv.setText(robot.ip+" "+robot.type);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.weight= 1;
        tv.setLayoutParams(lp);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        return tv;
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.equals(remove)){
                AppDatabase db = AppDatabase.getInstance(context);
                db.plcConfDao().deleteConfById(robot.id);
                ((Activity)context).recreate();
            }else{
                Intent i = new Intent(context, PillsActivity.class);
                i.putExtra("id", robot.id);
                context.startActivity(i);
            }
        }
    };

}

