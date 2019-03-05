package geishaproject.demonote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    private Resources mResources;

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取传来的title
        String title = intent.getStringExtra("title");
        Intent intent1;
        intent1 = new Intent(context, TimeWram.class);
        //将title内容继续传递给闹钟提示的activity
        intent1.putExtra("title",title);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }

}


