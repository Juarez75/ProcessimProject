package be.heh.std.customView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.heh.std.R;
import be.heh.std.app.LiquidActivity;
import be.heh.std.app.PillsActivity;
import be.heh.std.app.StartActivity;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.PlcType;
import be.heh.std.database.Role;


public class PlcView extends LinearLayout {

    private PlcConf plcConf;
    private Context context;
    private ImageButton remove;
    private ImageButton play;
    private Role role;


    private LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public PlcView(Context context, PlcConf r, Role role) {
        super(context);
        this.context = context;
        this.plcConf = r;
        this.role = role;
        this.setLayoutParams(layoutParams);
        this.setOrientation(LinearLayout.HORIZONTAL);

        this.addView(description());

        play = getButton(R.drawable.ic_play);
        this.addView(play);

        if(role != Role.BASIC){
            Log.i("PlcView",role+"");
            remove = getButton(R.drawable.ic_delete);
            this.addView(remove);
        }
    }

    private ImageButton getButton(int value){
        ImageButton button = new ImageButton(context);
        button.setImageResource(value);
        button.setOnClickListener(clickListener);
        return button;
    }

    private TextView description(){
        TextView textView = new TextView(context);
        textView.setText(plcConf.ip+" "+plcConf.type);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.CENTER_VERTICAL;
        layout.weight= 1;
        textView.setLayoutParams(layout);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        return textView;
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.equals(remove)){
                AppDatabase db = AppDatabase.getInstance(context);
                db.plcConfDao().deleteConfById(plcConf.id);
                ((Activity)context).recreate();
            }else{
                Intent i;
                if(plcConf.type == PlcType.PILLS)
                {
                    i = new Intent(context, PillsActivity.class);
                }else{
                    i = new Intent(context, LiquidActivity.class);
                }
                i.putExtra("id", plcConf.id);
                context.startActivity(i);

            }
        }
    };

}

